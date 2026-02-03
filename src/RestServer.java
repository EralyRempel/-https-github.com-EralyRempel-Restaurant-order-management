import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.BindException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RestServer {
    public static void main(String[] args) throws IOException {
        int preferredPort = parsePort(args);
        HttpServer server = null;
        int port = preferredPort;


        for (int attempt = 0; attempt < 20; attempt++) {
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                break;
            } catch (BindException e) {
                port++;
            }
        }

        if (server == null) {
            throw new IOException("Could not bind to ports starting at " + preferredPort);
        }

        DatabaseManager db = new DatabaseManager();
        server.createContext("/", new HomeHandler(db));
        server.createContext("/api/menu", new MenuHandler(db));

        server.start();
        System.out.println("REST server started: http://localhost:" + port + "/api/menu");
        System.out.println("Home page: http://localhost:" + port + "/");
    }

    private static int parsePort(String[] args) {

        if (args != null && args.length > 0) {
            try {
                return Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }
        String env = System.getenv("PORT");
        if (env != null && !env.isBlank()) {
            try {
                return Integer.parseInt(env.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return 8080;
    }

    static final class MenuHandler implements HttpHandler {
        private static final Pattern PRICE_FIELD = Pattern.compile("\"price\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");
        private static final Pattern NAME_FIELD = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]*)\"");
        private static final Pattern TYPE_FIELD = Pattern.compile("\"type\"\\s*:\\s*\"([^\"]*)\"");

        private final DatabaseManager db;

        MenuHandler(DatabaseManager db) {
            this.db = db;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath(); // e.g. /api/menu/5/price

                if ("GET".equalsIgnoreCase(method) && "/api/menu".equals(path)) {
                    handleGet(exchange);
                    return;
                }
                if ("POST".equalsIgnoreCase(method) && "/api/menu".equals(path)) {
                    handlePost(exchange);
                    return;
                }
                if ("PATCH".equalsIgnoreCase(method) && path.startsWith("/api/menu/") && path.endsWith("/price")) {
                    handlePatchPrice(exchange, path);
                    return;
                }

                sendJson(exchange, 404, "{\"error\":\"Not found\"}");
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, "{\"error\":\"Internal server error\"}");
            }
        }

        private void handleGet(HttpExchange exchange) throws IOException, SQLException {
            List<MenuRow> rows = db.getMenu();
            sendJson(exchange, 200, toJson(rows));
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            String body = readBody(exchange);
            String name = extractString(body, NAME_FIELD);
            String type = extractString(body, TYPE_FIELD);
            Double price = extractDouble(body, PRICE_FIELD);

            if (name == null || type == null || price == null) {
                sendJson(exchange, 400, "{\"error\":\"Expected JSON: {\\\"name\\\":string,\\\"price\\\":number,\\\"type\\\":string}\"}");
                return;
            }

            db.addMenuItem(name, price, type);
            sendJson(exchange, 201, "{\"status\":\"created\"}");
        }

        private void handlePatchPrice(HttpExchange exchange, String path) throws IOException {
            // /api/menu/{id}/price
            String[] parts = path.split("/");
            if (parts.length != 5) { // ["", "api", "menu", "{id}", "price"]
                sendJson(exchange, 400, "{\"error\":\"Bad path\"}");
                return;
            }
            int id;
            try {
                id = Integer.parseInt(parts[3]);
            } catch (NumberFormatException e) {
                sendJson(exchange, 400, "{\"error\":\"Bad id\"}");
                return;
            }

            String body = readBody(exchange);
            Double price = extractDouble(body, PRICE_FIELD);
            if (price == null) {
                sendJson(exchange, 400, "{\"error\":\"Expected JSON: {\\\"price\\\":number}\"}");
                return;
            }

            db.updatePrice(id, price);
            sendJson(exchange, 200, "{\"status\":\"updated\"}");
        }

        private static String readBody(HttpExchange exchange) throws IOException {
            try (InputStream in = exchange.getRequestBody()) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        private static String extractString(String json, Pattern p) {
            Matcher m = p.matcher(json);
            if (!m.find()) return null;
            return unescape(m.group(1));
        }

        private static Double extractDouble(String json, Pattern p) {
            Matcher m = p.matcher(json);
            if (!m.find()) return null;
            try {
                return Double.parseDouble(m.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        private static String unescape(String s) {
            // Minimal unescape for \" and \\ sequences
            return s.replace("\\\\", "\\").replace("\\\"", "\"");
        }

        private static String toJson(List<MenuRow> rows) {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (int i = 0; i < rows.size(); i++) {
                MenuRow r = rows.get(i);
                if (i > 0) sb.append(',');
                sb.append('{')
                        .append("\"id\":").append(r.id).append(',')
                        .append("\"name\":").append(quote(r.name)).append(',')
                        .append("\"price\":").append(r.price).append(',')
                        .append("\"type\":").append(quote(r.type))
                        .append('}');
            }
            sb.append(']');
            return sb.toString();
        }

        private static String quote(String s) {
            if (s == null) return "null";
            return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }

        private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            Headers headers = exchange.getResponseHeaders();
            headers.set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    static final class HomeHandler implements HttpHandler {
        private final DatabaseManager db;

        HomeHandler(DatabaseManager db) {
            this.db = db;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (!"/".equals(path)) {
                sendHtml(exchange, 404, "<h1>404 Not Found</h1><p>No context found for request</p>");
                return;
            }
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendHtml(exchange, 405, "<h1>405 Method Not Allowed</h1>");
                return;
            }

            try {
                List<MenuRow> rows = db.getMenu();
                String html = buildHomePage(rows);
                sendHtml(exchange, 200, html);
            } catch (Exception e) {
                e.printStackTrace();
                sendHtml(exchange, 500, "<h1>500 Internal Server Error</h1>");
            }
        }

        private static String buildHomePage(List<MenuRow> rows) {
            StringBuilder sb = new StringBuilder();
            sb.append("<!doctype html><html><head><meta charset=\"utf-8\">")
                    .append("<title>Salatik-Burmaldatik</title>")
                    .append("<style>")
                    .append("body{font-family:system-ui,Arial,sans-serif;max-width:820px;margin:40px auto;padding:0 16px;}")
                    .append("h1{margin:0 0 8px 0;}")
                    .append(".meta{color:#444;margin:0 0 18px 0;}")
                    .append("table{width:100%;border-collapse:collapse;}")
                    .append("th,td{border-bottom:1px solid #eee;padding:10px 6px;text-align:left;}")
                    .append("th{background:#fafafa;}")
                    .append("</style></head><body>");

            sb.append("<h1>Salatik-Burmaldatik</h1>");
            sb.append("<p class=\"meta\">Address: Seifulina 52</p>");
            sb.append("<p class=\"meta\">Rating: 4.8/5</p>");

            sb.append("<h2>Menu (sorted)</h2>");
            sb.append("<table><thead><tr><th>#</th><th>Name</th><th>Type</th><th>Price</th></tr></thead><tbody>");
            for (MenuRow r : rows) {
                sb.append("<tr>")
                        .append("<td>").append(r.id).append("</td>")
                        .append("<td>").append(escapeHtml(r.name)).append("</td>")
                        .append("<td>").append(escapeHtml(r.type)).append("</td>")
                        .append("<td>").append(r.price).append("</td>")
                        .append("</tr>");
            }
            sb.append("</tbody></table>");
            sb.append("<p class=\"meta\">JSON API: <a href=\"/api/menu\">/api/menu</a></p>");
            sb.append("</body></html>");
            return sb.toString();
        }

        private static String escapeHtml(String s) {
            if (s == null) return "";
            return s.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
        }

        private static void sendHtml(HttpExchange exchange, int status, String html) throws IOException {
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            Headers headers = exchange.getResponseHeaders();
            headers.set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}

