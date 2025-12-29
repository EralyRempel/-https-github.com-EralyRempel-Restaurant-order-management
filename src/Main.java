public class Main {
    public static void main(String[] args) {
        Restaurant myRest = new Restaurant("Tasty Java", "Street 101");
        Order myOrder = new Order(101);

        myOrder.addItem(new Food("Steak", 4500, "Meat"));
        myOrder.addItem(new Food("Salad", 1800, "Veggie"));
        myOrder.addItem(new Drink("Lemonade", 900, 0.4));
        myOrder.addItem(new Drink("Tea", 600, 0.5));

        System.out.println(myRest);

        System.out.println("\n--- Initial Order ---");
        myOrder.printOrder();

        System.out.println("\n--- Sorted by Price ---");
        myOrder.sortByPrice();
        myOrder.printOrder();

        System.out.println("\n--- Items under 1000 ---");
        myOrder.filterByMaxPrice(1000).forEach(System.out::println);
    }
}