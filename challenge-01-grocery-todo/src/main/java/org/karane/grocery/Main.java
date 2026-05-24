package org.karane.grocery;

import org.karane.grocery.repository.GroceryRepository;
import org.karane.grocery.service.GroceryService;

public class Main {

    public static void main(String[] args) {
        GroceryService list = new GroceryService(new GroceryRepository());

        System.out.println("=== Grocery TODO List ===\n");

        list.add("Milk");
        list.add("Eggs");
        list.add("Bread");
        list.add("Butter");
        list.add("Apples");

        System.out.println("After adding 5 items:");
        printList(list);

        list.markDone("Milk");
        list.markDone("Eggs");

        System.out.println("\nAfter marking Milk and Eggs as done:");
        printList(list);

        list.markUndone("Eggs");

        System.out.println("\nAfter re-doing Eggs (marked undone):");
        printList(list);

        list.remove("Butter");

        System.out.println("\nAfter removing Butter:");
        printList(list);
    }

    private static void printList(GroceryService list) {
        list.listAll().forEach(System.out::println);
    }
}
