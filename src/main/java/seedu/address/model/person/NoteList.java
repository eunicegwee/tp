package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents an append-only collection of notes.
 * Immutable; each modification returns a new instance.
 */
public class NoteList {
    public static final int MAX_NOTES = 20;
    public static final String MESSAGE_MAX_NOTES =
            String.format("Oops! You have reached the maximum limit of %d notes.", MAX_NOTES);

    private final List<Note> notes;

    /**
     * Creates an empty Notes object.
     */
    public NoteList() {
        this.notes = new ArrayList<>();
    }

    /**
     * Creates a Notes object with existing notes.
     */
    public NoteList(List<Note> notes) {
        requireNonNull(notes);
        requireWithinLimit(notes.size());
        for (Note note : notes) {
            requireNonNull(note);
        }
        this.notes = new ArrayList<>(notes); // defensive copy
    }

    /**
     * Appends a new note and returns a new Notes instance.
     */
    public NoteList append(String content) {
        requireNonNull(content);
        requireWithinLimit(notes.size() + 1);

        List<Note> updated = new ArrayList<>(notes);
        updated.add(new Note(content));

        return new NoteList(updated);
    }

    /**
     * Returns an unmodifiable view of notes.
     */
    public List<Note> getAll() {
        return Collections.unmodifiableList(notes);
    }

    /**
     * Returns true if there are no notes.
     */
    public boolean isEmpty() {
        return notes.isEmpty();
    }

    @Override
    public String toString() {
        return notes.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof NoteList)) {
            return false;
        }

        NoteList otherNotes = (NoteList) other;
        return notes.equals(otherNotes.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notes);
    }

    private static void requireWithinLimit(int noteCount) {
        if (noteCount > MAX_NOTES) {
            throw new IllegalArgumentException(MESSAGE_MAX_NOTES);
        }
    }
}
