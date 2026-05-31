package org.karane.notetaking;

import org.karane.notetaking.domain.Note;
import org.karane.notetaking.sync.SyncService;

public class Main {

    public static void main(String[] args) {
        NoteService local = new NoteService();
        NoteService remote = new NoteService();
        SyncService syncService = new SyncService();

        Note shopping = local.addNote("Shopping List", "Milk, Eggs, Bread");
        Note ideas = local.addNote("Project Ideas", "Build a note-taking app");

        System.out.println("Local notes after add:");
        local.listAll().forEach(System.out::println);

        local.saveNote(shopping.getId(), "Milk, Eggs, Bread, Butter");
        System.out.println("\nAfter saveNote (shopping):");
        System.out.println(local.getNote(shopping.getId()));

        local.editNote(ideas.getId(), "Project Ideas 2025", "Build a note-taking app with sync");
        System.out.println("\nAfter editNote (ideas):");
        System.out.println(local.getNote(ideas.getId()));

        remote.addNote("Meeting Notes", "Discuss Q1 goals");

        syncService.sync(local, remote);
        System.out.println("\nLocal notes after sync:");
        local.listAll().forEach(System.out::println);
        System.out.println("\nRemote notes after sync:");
        remote.listAll().forEach(System.out::println);

        local.deleteNote(shopping.getId());
        System.out.println("\nLocal notes after delete:");
        local.listAll().forEach(System.out::println);
    }
}
