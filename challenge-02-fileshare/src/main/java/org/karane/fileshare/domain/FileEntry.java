package org.karane.fileshare.domain;

import java.time.Instant;

public class FileEntry {

    private final String name;
    private final byte[] encryptedContent;
    private final Instant savedAt;

    public FileEntry(String name, byte[] encryptedContent) {
        this.name = name;
        this.encryptedContent = encryptedContent;
        this.savedAt = Instant.now();
    }

    public String getName() {
        return name;
    }

    public byte[] getEncryptedContent() {
        return encryptedContent;
    }

    public Instant getSavedAt() {
        return savedAt;
    }

    @Override
    public String toString() {
        return name + " (saved: " + savedAt + ")";
    }
}
