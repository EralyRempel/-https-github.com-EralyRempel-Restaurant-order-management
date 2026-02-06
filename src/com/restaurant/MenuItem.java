package com.restaurant;

public abstract class MenuItem {
    private int id;
    private String name;
    private double price;

    public MenuItem() {}

    public MenuItem(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return name + " — " + price + "tg";
    }
}

class Food extends MenuItem {
    private String category;

    public Food() { super(); }

    public Food(int id, String name, double price, String category) {
        super(id, name, price);
        this.category = category;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return super.toString() + " [" + category + "]";
    }
}

class Drink extends MenuItem {
    private double volume;

    public Drink() { super(); }

    public Drink(int id, String name, double price, double volume) {
        super(id, name, price);
        this.volume = volume;
    }

    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }

    @Override
    public String toString() {
        return super.toString() + " (" + volume + "L)";
    }
}