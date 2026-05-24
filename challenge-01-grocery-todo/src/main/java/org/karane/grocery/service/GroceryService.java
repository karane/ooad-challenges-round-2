package org.karane.grocery.service;

import org.karane.grocery.model.GroceryItem;
import org.karane.grocery.repository.GroceryRepository;

import java.util.List;

public class GroceryService {

    private final GroceryRepository repository;

    public GroceryService(GroceryRepository repository) {
        this.repository = repository;
    }

    public void add(String name) {
        if (repository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Item already exists: " + name);
        }
        repository.save(new GroceryItem(name));
    }

    public void remove(String name) {
        GroceryItem item = repository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + name));
        repository.delete(item);
    }

    public void markDone(String name) {
        repository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + name))
                .markDone();
    }

    public void markUndone(String name) {
        repository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + name))
                .markUndone();
    }

    public List<GroceryItem> listAll() {
        return repository.findAll();
    }

    public int size() {
        return repository.count();
    }
}
