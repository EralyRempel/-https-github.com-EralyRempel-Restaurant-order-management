import java.sql.*;

public class DatabaseManager {
    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String user = "postgres";
    private final String password = "Partier123";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void addMenuItem(String name, double price, String type) {
        String sql = "INSERT INTO menu_items(name, price, type) VALUES(?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setString(3, type);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void readMenu() {
        String sql = "SELECT * FROM menu_items";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ": " + rs.getString("name") + " | " + rs.getDouble("price") + "tg");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePrice(int id, double newPrice) {
        String sql = "UPDATE menu_items SET price = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteItem(int id) {
        String sql = "DELETE FROM menu_items WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}