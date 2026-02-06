package com.restaurant;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RestaurantController {
    private final DatabaseManager db = new DatabaseManager();

    @GetMapping("/menu")
    public List<MenuItem> getMenu() {
        return db.getMenu();
    }

    @PostMapping("/menu")
    public String add(@RequestBody Food food) {
        db.addMenuItem(food.getName(), food.getPrice(), "com.restaurant.Food");
        return "Item added!";
    }

    @DeleteMapping("/menu/{id}")
    public String delete(@PathVariable int id) {
        db.deleteItem(id);
        return "Item deleted!";
    }
}