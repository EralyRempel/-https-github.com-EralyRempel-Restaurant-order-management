public class Main {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();

        db.addMenuItem("Pizza Margarita", 3500.0, "Food");
        db.addMenuItem("Green Tea", 600.0, "Drink");

        System.out.println("Initial menu from database:");
        db.readMenu();

        db.updatePrice(1, 3800.0);

        db.deleteItem(2);

        System.out.println("\nUpdated menu from database:");
        db.readMenu();
    }
}