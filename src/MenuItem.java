import java.util.Objects;

public abstract class MenuItem {
    private String name;
    private double price;

    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " — " + price + "tg";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Double.compare(menuItem.price, price) == 0 && Objects.equals(name, menuItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}

class Food extends MenuItem {
    private String category;

    public Food(String name, double price, String category) {
        super(name, price);
        this.category = category;
    }

    @Override
    public String toString() {
        return super.toString() + " [" + category + "]";
    }
}

class Drink extends MenuItem {
    private double volume;

    public Drink(String name, double price, double volume) {
        super(name, price);
        this.volume = volume;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + volume + "L)";
    }
}