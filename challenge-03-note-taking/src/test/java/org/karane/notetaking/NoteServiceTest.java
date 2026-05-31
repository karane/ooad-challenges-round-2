package org.karane.notetaking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.karane.notetaking.domain.Note;
import org.karane.notetaking.sync.SyncService;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoteServiceTest {

    private NoteService service;

    @BeforeEach
    void setUp() {
        service = new NoteService();
    }

    @Nested
    class AddNote {
        @Test
        void addedNoteAppearsInListAll() {
            service.addNote("Groceries", "Milk, Eggs");

            assertEquals(1, service.listAll().size());
        }

        @Test
        void addedNoteHasCorrectTitleAndContent() {
            Note note = service.addNote("Meeting", "Discuss budget");

            assertEquals("Meeting", note.getTitle());
            assertEquals("Discuss budget", note.getContent());
        }

        @Test
        void addedNoteReceivesUniqueId() {
            Note a = service.addNote("A", "");
            Note b = service.addNote("B", "");

            assertNotEquals(a.getId(), b.getId());
        }

        @Test
        void addNoteRejectsBlankTitle() {
            assertThrows(IllegalArgumentException.class, () -> service.addNote("  ", "content"));
        }

        @Test
        void addNoteRejectsNullContent() {
            assertThrows(IllegalArgumentException.class, () -> service.addNote("Title", null));
        }

        @Test
        void addNoteTrimsTitle() {
            Note note = service.addNote("  Padded  ", "content");

            assertEquals("Padded", note.getTitle());
        }
    }

    @Nested
    class SaveNote {
        @Test
        void saveNoteUpdatesContent() {
            Note note = service.addNote("Draft", "v1");
            service.saveNote(note.getId(), "v2");

            assertEquals("v2", service.getNote(note.getId()).getContent());
        }

        @Test
        void saveNoteUpdatesTimestamp() throws InterruptedException {
            Note note = service.addNote("Draft", "v1");
            Instant before = note.getUpdatedAt();
            Thread.sleep(5);
            service.saveNote(note.getId(), "v2");

            assertTrue(service.getNote(note.getId()).getUpdatedAt().isAfter(before));
        }

        @Test
        void saveNoteDoesNotChangeTitle() {
            Note note = service.addNote("Keep Me", "old");
            service.saveNote(note.getId(), "new content");

            assertEquals("Keep Me", service.getNote(note.getId()).getTitle());
        }

        @Test
        void saveNoteThrowsForUnknownId() {
            assertThrows(IllegalArgumentException.class, () -> service.saveNote("no-such-id", "content"));
        }

        @Test
        void saveNoteRejectsNullContent() {
            Note note = service.addNote("Title", "content");

            assertThrows(IllegalArgumentException.class, () -> service.saveNote(note.getId(), null));
        }
    }

    @Nested
    class EditNote {
        @Test
        void editNoteUpdatesTitleAndContent() {
            Note note = service.addNote("Old Title", "old content");
            service.editNote(note.getId(), "New Title", "new content");
            Note updated = service.getNote(note.getId());

            assertEquals("New Title", updated.getTitle());
            assertEquals("new content", updated.getContent());
        }

        @Test
        void editNoteUpdatesTimestamp() throws InterruptedException {
            Note note = service.addNote("Title", "content");
            Instant before = note.getUpdatedAt();
            Thread.sleep(5);
            service.editNote(note.getId(), "Title 2", "content 2");

            assertTrue(service.getNote(note.getId()).getUpdatedAt().isAfter(before));
        }

        @Test
        void editNoteThrowsForUnknownId() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.editNote("ghost", "t", "c"));
        }

        @Test
        void editNoteRejectsBlankTitle() {
            Note note = service.addNote("Title", "content");

            assertThrows(IllegalArgumentException.class,
                    () -> service.editNote(note.getId(), "", "content"));
        }
    }

    @Nested
    class DeleteNote {
        @Test
        void deleteRemovesNoteFromList() {
            Note note = service.addNote("Temp", "data");
            service.deleteNote(note.getId());

            assertTrue(service.listAll().isEmpty());
        }

        @Test
        void deleteThrowsForUnknownId() {
            assertThrows(IllegalArgumentException.class, () -> service.deleteNote("ghost"));
        }

        @Test
        void deletedNoteCannotBeRetrieved() {
            Note note = service.addNote("Gone", "bye");
            service.deleteNote(note.getId());

            assertThrows(IllegalArgumentException.class, () -> service.getNote(note.getId()));
        }
    }

    @Nested
    class ListAll {
        @Test
        void returnsEmptyListWhenNoNotes() {
            assertTrue(service.listAll().isEmpty());
        }

        @Test
        void returnsAllNotesInInsertionOrder() {
            Note a = service.addNote("A", "");
            Note b = service.addNote("B", "");
            Note c = service.addNote("C", "");
            List<Note> notes = service.listAll();

            assertEquals(3, notes.size());
            assertEquals(a.getId(), notes.get(0).getId());
            assertEquals(b.getId(), notes.get(1).getId());
            assertEquals(c.getId(), notes.get(2).getId());
        }
    }

    @Nested
    class Sync {
        private SyncService syncService;
        private NoteService remote;

        @BeforeEach
        void setUp() {
            syncService = new SyncService();
            remote = new NoteService();
        }

        @Test
        void localOnlyNoteIsCopiedToRemote() {
            service.addNote("Local Only", "content");
            syncService.sync(service, remote);

            assertEquals(1, remote.listAll().size());
            assertEquals("Local Only", remote.listAll().get(0).getTitle());
        }

        @Test
        void remoteOnlyNoteIsCopiedToLocal() {
            remote.addNote("Remote Only", "content");
            syncService.sync(service, remote);

            assertEquals(1, service.listAll().size());
            assertEquals("Remote Only", service.listAll().get(0).getTitle());
        }

        @Test
        void newerRemoteNoteWinsOnConflict() throws InterruptedException {
            Note local = service.addNote("Note", "local version");
            Thread.sleep(10);
            Note remoteNote = remote.addNote("Note", "remote version");
            remote.getStore().get(remoteNote.getId()).ifPresent(n -> {
            });

            NoteService localFresh = new NoteService();
            NoteService remoteFresh = new NoteService();
            Note l = localFresh.addNote("Shared", "local content");
            Thread.sleep(10);
            Note r = new Note(l.getId(), "Shared", "remote content");
            r.setUpdatedAt(Instant.now());
            remoteFresh.getStore().put(r);

            syncService.sync(localFresh, remoteFresh);
            
            assertEquals("remote content", localFresh.getNote(l.getId()).getContent());
        }

        @Test
        void newerLocalNoteWinsOnConflict() throws InterruptedException {
            NoteService localFresh = new NoteService();
            NoteService remoteFresh = new NoteService();
            Note l = localFresh.addNote("Shared", "local content");
            Note r = new Note(l.getId(), "Shared", "remote old content");
            r.setUpdatedAt(l.getCreatedAt().minusMillis(100));
            remoteFresh.getStore().put(r);

            syncService.sync(localFresh, remoteFresh);

            assertEquals("local content", remoteFresh.getNote(l.getId()).getContent());
        }

        @Test
        void syncIsBidirectional() {
            service.addNote("Local Note", "local");
            remote.addNote("Remote Note", "remote");
            syncService.sync(service, remote);

            assertEquals(2, service.listAll().size());
            assertEquals(2, remote.listAll().size());
        }
    }
}
