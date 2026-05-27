package org.karane.fileshare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.karane.fileshare.crypto.AesEncryptionService;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileShareServiceTest {

    @TempDir
    Path tempDir;

    private FileShareService service;

    @BeforeEach
    void setUp() {
        service = new FileShareService(new AesEncryptionService(), tempDir);
    }

    @Nested
    class Save {
        @Test
        void savesFileSuccessfully() {
            service.save("notes.txt", "hello");
           
            assertTrue(service.listFiles().contains("notes.txt"));
        }

        @Test
        void overwritesExistingFile() {
            service.save("doc.txt", "v1");
            service.save("doc.txt", "v2");
           
            assertEquals("v2", service.restore("doc.txt"));
        }

        @Test
        void rejectsBlankName() {
            assertThrows(IllegalArgumentException.class, () -> service.save("  ", "content"));
        }

        @Test
        void rejectsNullContent() {
            assertThrows(IllegalArgumentException.class, () -> service.save("file.txt", null));
        }
    }

    @Nested
    class Restore {
        @Test
        void decryptsContentCorrectly() {
            String original = "Top secret data 123!";
            service.save("secret.txt", original);
           
            assertEquals(original, service.restore("secret.txt"));
        }

        @Test
        void throwsWhenFileNotFound() {
            assertThrows(IllegalArgumentException.class, () -> service.restore("ghost.txt"));
        }

        @Test
        void restoresEmptyContent() {
            service.save("empty.txt", "");
           
            assertEquals("", service.restore("empty.txt"));
        }

        @Test
        void restoresMultilineContent() {
            String content = "line1\nline2\nline3";
            service.save("multi.txt", content);
           
            assertEquals(content, service.restore("multi.txt"));
        }
    }

    @Nested
    class Delete {
        @Test
        void deletesExistingFile() {
            service.save("temp.txt", "data");
            service.delete("temp.txt");
           
            assertFalse(service.listFiles().contains("temp.txt"));
        }

        @Test
        void throwsWhenFileNotFound() {
            assertThrows(IllegalArgumentException.class, () -> service.delete("ghost.txt"));
        }

        @Test
        void deletedFileCannotBeRestored() {
            service.save("gone.txt", "bye");
            service.delete("gone.txt");
           
            assertThrows(IllegalArgumentException.class, () -> service.restore("gone.txt"));
        }
    }

    @Nested
    class ListFiles {
        @Test
        void returnsAllFileNamesInInsertionOrder() {
            service.save("a.txt", "a");
            service.save("b.txt", "b");
            service.save("c.txt", "c");
           
            assertEquals(List.of("a.txt", "b.txt", "c.txt"), service.listFiles());
        }

        @Test
        void returnsEmptyListWhenNoFiles() {
            assertTrue(service.listFiles().isEmpty());
        }
    }

    @Nested
    class Search {
        @Test
        void findsFilesByPartialName() {
            service.save("report-q1.txt", "data");
            service.save("report-q2.txt", "data");
            service.save("budget.csv", "data");

            List<String> results = service.search("report");
           
            assertEquals(2, results.size());
            assertTrue(results.contains("report-q1.txt"));
            assertTrue(results.contains("report-q2.txt"));
        }

        @Test
        void searchIsCaseInsensitive() {
            service.save("Notes.md", "data");
           
            assertEquals(1, service.search("notes").size());
        }

        @Test
        void returnsEmptyListWhenNoMatch() {
            service.save("file.txt", "data");
           
            assertTrue(service.search("xyz").isEmpty());
        }

        @Test
        void rejectsBlankQuery() {
            assertThrows(IllegalArgumentException.class, () -> service.search("  "));
        }
    }

    @Nested
    class Encryption {
        @Test
        void storedContentIsNotPlaintext(@TempDir Path otherDir) {
            service.save("secret.txt", "my password is 1234");
            FileShareService other = new FileShareService(new AesEncryptionService(), otherDir);
            other.save("secret.txt", "different");
           
            assertNotEquals(service.restore("secret.txt"), other.restore("secret.txt"));
        }

        @Test
        void eachSaveProducesDifferentCiphertext(@TempDir Path dir2) {
            service.save("f1.txt", "same content");
            FileShareService service2 = new FileShareService(new AesEncryptionService(), dir2);
            service2.save("f1.txt", "same content");
           
            assertEquals("same content", service.restore("f1.txt"));
            assertEquals("same content", service2.restore("f1.txt"));
        }
    }
}
