package org.karane.fileshare.store;

import org.karane.fileshare.domain.FileEntry;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

public class FileStore {

    private final Path directory;

    public FileStore(Path directory) {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create storage directory", e);
        }
        this.directory = directory;
    }

    public void put(FileEntry entry) {
        try {
            Files.write(diskPath(entry.getName()), entry.getEncryptedContent());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write file: " + entry.getName(), e);
        }
    }

    public Optional<FileEntry> get(String name) {
        Path path = diskPath(name);
        if (!Files.exists(path)) return Optional.empty();
        try {
            byte[] data = Files.readAllBytes(path);
            return Optional.of(new FileEntry(name, data));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file: " + name, e);
        }
    }

    public boolean remove(String name) {
        try {
            return Files.deleteIfExists(diskPath(name));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete file: " + name, e);
        }
    }

    public Collection<FileEntry> all() {
        try (var stream = Files.list(directory)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(this::load)
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to list files", e);
        }
    }

    public boolean contains(String name) {
        return Files.exists(diskPath(name));
    }

    // Base64-encode the name so any character is safe as a filename
    private Path diskPath(String name) {
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(name.getBytes());
        return directory.resolve(encoded);
    }

    private FileEntry load(Path path) {
        try {
            String name = new String(Base64.getUrlDecoder().decode(path.getFileName().toString()));
            byte[] data = Files.readAllBytes(path);
            return new FileEntry(name, data);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load file", e);
        }
    }
}
