package org.karane.grocery.model;

public class GroceryItem {

    private final String name;
    private boolean done;

    public GroceryItem(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name must not be blank");
        }
        this.name = name.trim();
        this.done = false;
    }

    public String getName() {
        return name;
    }

    public boolean isDone() {
        return done;
    }

    public void markDone() {
        this.done = true;
    }

    public void markUndone() {
        this.done = false;
    }

    @Override
    public String toString() {
        return (done ? "[x] " : "[ ] ") + name;
    }
}
