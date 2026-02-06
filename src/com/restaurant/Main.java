package com.restaurant;

public class Main {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();

        db.addMenuItem("Pizza Margarita", 3500.0, "com.restaurant.Food");
        db.addMenuItem("Green Tea", 600.0, "com.restaurant.Drink");

        System.out.println("Initial menu from database:");
        db.getMenu();

        db.updatePrice(1, 3800.0);

        db.deleteItem(2);

        System.out.println("\nUpdated menu from database:");
        db.getMenu();
    }
}