package org.karane.grocery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.karane.grocery.repository.GroceryRepository;
import org.karane.grocery.service.GroceryService;

import static org.junit.jupiter.api.Assertions.*;

class GroceryServiceTest {

    private GroceryService service;

    @BeforeEach
    void setUp() {
        service = new GroceryService(new GroceryRepository());
    }

    @Nested
    class Add {
        @Test
        void addsItemToList() {
            service.add("Milk");
            assertEquals(1, service.size());
            assertEquals("Milk", service.listAll().getFirst().getName());
        }

        @Test
        void trimsWhitespaceOnAdd() {
            service.add("  Eggs  ");
            assertEquals("Eggs", service.listAll().getFirst().getName());
        }

        @Test
        void rejectsDuplicatesCaseInsensitive() {
            service.add("Milk");
            assertThrows(IllegalArgumentException.class, () -> service.add("milk"));
        }

        @Test
        void rejectsBlankName() {
            assertThrows(IllegalArgumentException.class, () -> service.add("   "));
        }
    }

    @Nested
    class Remove {
        @Test
        void removesExistingItem() {
            service.add("Bread");
            service.remove("Bread");
            assertEquals(0, service.size());
        }

        @Test
        void removesItemCaseInsensitive() {
            service.add("Bread");
            service.remove("BREAD");
            assertEquals(0, service.size());
        }

        @Test
        void throwsWhenItemNotFound() {
            assertThrows(IllegalArgumentException.class, () -> service.remove("Ghost"));
        }
    }

    @Nested
    class MarkDone {
        @Test
        void marksItemAsDone() {
            service.add("Butter");
            service.markDone("Butter");
            assertTrue(service.listAll().getFirst().isDone());
        }

        @Test
        void throwsWhenItemNotFound() {
            assertThrows(IllegalArgumentException.class, () -> service.markDone("Ghost"));
        }
    }

    @Nested
    class MarkUndone {
        @Test
        void undoesADoneItem() {
            service.add("Apples");
            service.markDone("Apples");
            service.markUndone("Apples");
            assertFalse(service.listAll().getFirst().isDone());
        }

        @Test
        void throwsWhenItemNotFound() {
            assertThrows(IllegalArgumentException.class, () -> service.markUndone("Ghost"));
        }
    }

    @Nested
    class ListAll {
        @Test
        void returnsAllItemsInInsertionOrder() {
            service.add("Milk");
            service.add("Eggs");
            service.add("Bread");

            var items = service.listAll();
            assertEquals(3, items.size());
            assertEquals("Milk", items.get(0).getName());
            assertEquals("Eggs", items.get(1).getName());
            assertEquals("Bread", items.get(2).getName());
        }

        @Test
        void returnsUnmodifiableView() {
            service.add("Milk");
            var items = service.listAll();
            assertThrows(UnsupportedOperationException.class, () -> items.remove(0));
        }

        @Test
        void returnsEmptyListWhenNoItems() {
            assertTrue(service.listAll().isEmpty());
        }
    }
}
