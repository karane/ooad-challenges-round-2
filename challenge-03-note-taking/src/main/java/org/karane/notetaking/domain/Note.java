package org.karane.notetaking.domain;

import java.time.Instant;

public class Note {

    private final String id;
    private String title;
    private String content;
    private final Instant createdAt;
    private Instant updatedAt;

    public Note(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = Instant.now();
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " (updated: " + updatedAt + ")";
    }
}
