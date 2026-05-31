package org.karane.notetaking.sync;

import org.karane.notetaking.NoteService;
import org.karane.notetaking.domain.Note;

public class SyncService {

    public void sync(NoteService local, NoteService remote) {
        for (Note remoteNote : remote.listAll()) {
            local.getStore().get(remoteNote.getId()).ifPresentOrElse(
                    localNote -> {
                        if (remoteNote.getUpdatedAt().isAfter(localNote.getUpdatedAt())) {
                            localNote.setTitle(remoteNote.getTitle());
                            localNote.setContent(remoteNote.getContent());
                            localNote.setUpdatedAt(remoteNote.getUpdatedAt());
                        }
                    },
                    () -> {
                        Note copy = new Note(remoteNote.getId(), remoteNote.getTitle(), remoteNote.getContent());
                        copy.setUpdatedAt(remoteNote.getUpdatedAt());
                        local.getStore().put(copy);
                    }
            );
        }

        for (Note localNote : local.listAll()) {
            remote.getStore().get(localNote.getId()).ifPresentOrElse(
                    remoteNote -> {
                        if (localNote.getUpdatedAt().isAfter(remoteNote.getUpdatedAt())) {
                            remoteNote.setTitle(localNote.getTitle());
                            remoteNote.setContent(localNote.getContent());
                            remoteNote.setUpdatedAt(localNote.getUpdatedAt());
                        }
                    },
                    () -> {
                        Note copy = new Note(localNote.getId(), localNote.getTitle(), localNote.getContent());
                        copy.setUpdatedAt(localNote.getUpdatedAt());
                        remote.getStore().put(copy);
                    }
            );
        }
    }
}
