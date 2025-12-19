public class Main {
    public static void main(String[] args) {
        MenuItem pizza = new MenuItem("Пицца Маргарита", 2500);
        MenuItem burger = new MenuItem("Чизбургер", 1800);
        MenuItem burger2 = new MenuItem("Чизбургер", 1800);

        Order order1 = new Order(101, "Готовится");
        Restaurant myRest = new Restaurant("Astana Food", "ул. Достык, 10");

        System.out.println(myRest);
        System.out.println(pizza);
        System.out.println(order1);

        System.out.println("\n--- Сравнение цен ---");
        if (pizza.getPrice() > burger.getPrice()) {
            System.out.println(pizza.getName() + " дороже, чем " + burger.getName());
        } else if (pizza.getPrice() < burger.getPrice()) {
            System.out.println(burger.getName() + " дороже, чем " + pizza.getName());
        } else {
            System.out.println("Цены одинаковые.");
        }
    }
}