package com.restaurant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Order {
    private int orderNumber;
    private List<MenuItem> items;

    public Order(int orderNumber) {
        this.orderNumber = orderNumber;
        this.items = new ArrayList<>();
    }

    public void addItem(MenuItem item) {
        items.add(item);
    }

    public void sortByPrice() {
        items.sort(Comparator.comparingDouble(MenuItem::getPrice));
    }

    public List<MenuItem> filterByMaxPrice(double maxPrice) {
        return items.stream()
                .filter(item -> item.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    public void printOrder() {
        System.out.println("com.restaurant.Order #" + orderNumber);
        for (MenuItem item : items) {
            System.out.println(" - " + item);
        }
    }
}