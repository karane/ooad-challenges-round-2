package org.karane.fileshare;

import org.karane.fileshare.crypto.EncryptionService;
import org.karane.fileshare.domain.FileEntry;
import org.karane.fileshare.store.FileStore;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class FileShareService {

    private final FileStore store;
    private final EncryptionService encryption;

    public FileShareService(EncryptionService encryption, Path storageDir) {
        this.store = new FileStore(storageDir);
        this.encryption = encryption;
    }

    public void save(String name, String content) {
        validateName(name);
        if (content == null) throw new IllegalArgumentException("Content must not be null");
        byte[] encrypted = encryption.encrypt(content.getBytes(StandardCharsets.UTF_8));
        store.put(new FileEntry(name.trim(), encrypted));
    }

    public String restore(String name) {
        FileEntry entry = store.get(name.trim())
                .orElseThrow(() -> new IllegalArgumentException("File not found: " + name));
        byte[] decrypted = encryption.decrypt(entry.getEncryptedContent());
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public void delete(String name) {
        boolean removed = store.remove(name.trim());
        if (!removed) throw new IllegalArgumentException("File not found: " + name);
    }

    public List<String> listFiles() {
        return store.all().stream()
                .map(FileEntry::getName)
                .toList();
    }

    public List<String> search(String query) {
        if (query == null || query.isBlank()) throw new IllegalArgumentException("Search query must not be blank");
        String lower = query.trim().toLowerCase();
        return store.all().stream()
                .map(FileEntry::getName)
                .filter(name -> name.toLowerCase().contains(lower))
                .toList();
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("File name must not be blank");
    }
}
