package org.karane.notetaking;

import org.karane.notetaking.domain.Note;
import org.karane.notetaking.store.NoteStore;

import java.util.List;
import java.util.UUID;

public class NoteService {

    private final NoteStore store = new NoteStore();

    public Note addNote(String title, String content) {
        validateTitle(title);
        if (content == null) throw new IllegalArgumentException("Content must not be null");
        Note note = new Note(UUID.randomUUID().toString(), title.trim(), content);
        store.put(note);
        return note;
    }

    public Note saveNote(String id, String content) {
        if (content == null) throw new IllegalArgumentException("Content must not be null");
        Note note = findOrThrow(id);
        note.setContent(content);
        return note;
    }

    public Note editNote(String id, String title, String content) {
        validateTitle(title);
        if (content == null) throw new IllegalArgumentException("Content must not be null");
        Note note = findOrThrow(id);
        note.setTitle(title.trim());
        note.setContent(content);
        return note;
    }

    public void deleteNote(String id) {
        boolean removed = store.remove(id);
        if (!removed) throw new IllegalArgumentException("Note not found: " + id);
    }

    public List<Note> listAll() {
        return List.copyOf(store.all());
    }

    public Note getNote(String id) {
        return findOrThrow(id);
    }

    public NoteStore getStore() {
        return store;
    }

    private Note findOrThrow(String id) {
        return store.get(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + id));
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title must not be blank");
    }
}
