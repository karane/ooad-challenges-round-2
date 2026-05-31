package org.karane.notetaking.store;

import org.karane.notetaking.domain.Note;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class NoteStore {

    private final Map<String, Note> notes = new LinkedHashMap<>();

    public void put(Note note) {
        notes.put(note.getId(), note);
    }

    public Optional<Note> get(String id) {
        return Optional.ofNullable(notes.get(id));
    }

    public boolean remove(String id) {
        return notes.remove(id) != null;
    }

    public Collection<Note> all() {
        return notes.values();
    }
}
