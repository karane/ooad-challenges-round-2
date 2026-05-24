package org.karane.grocery.repository;

import org.karane.grocery.model.GroceryItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GroceryRepository {

    private final List<GroceryItem> items = new ArrayList<>();

    public void save(GroceryItem item) {
        items.add(item);
    }

    public void delete(GroceryItem item) {
        items.remove(item);
    }

    public Optional<GroceryItem> findByName(String name) {
        return items.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name.trim()))
                .findFirst();
    }

    public List<GroceryItem> findAll() {
        return Collections.unmodifiableList(items);
    }

    public int count() {
        return items.size();
    }
}
