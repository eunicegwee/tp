package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class NoteListTest {

    @Test
    public void constructor_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new NoteList(null));
    }

    @Test
    public void constructor_defensiveCopy_modifyingSourceListDoesNotAffectNoteList() {
        List<Note> source = new ArrayList<>();
        source.add(new Note("Initial note"));

        NoteList noteList = new NoteList(source);
        source.add(new Note("Added later"));

        assertEquals(List.of(new Note("Initial note")), noteList.getAll());
    }

    @Test
    public void append_nullContent_throwsNullPointerException() {
        NoteList noteList = new NoteList();
        assertThrows(NullPointerException.class, () -> noteList.append(null));
    }

    @Test
    public void append_validContent_returnsNewListAndKeepsOriginalUnchanged() {
        NoteList original = new NoteList(List.of(new Note("Existing")));

        NoteList updated = original.append("New");

        assertEquals(List.of(new Note("Existing")), original.getAll());
        assertEquals(List.of(new Note("Existing"), new Note("New")), updated.getAll());
    }

    @Test
    public void clear_nonEmptyList_returnsEmptyListAndKeepsOriginalUnchanged() {
        NoteList original = new NoteList(List.of(new Note("Existing")));

        NoteList cleared = original.clear();

        assertFalse(original.isEmpty());
        assertTrue(cleared.isEmpty());
    }

    @Test
    public void getAll_modifyReturnedList_throwsUnsupportedOperationException() {
        NoteList noteList = new NoteList(List.of(new Note("Existing")));

        assertThrows(UnsupportedOperationException.class, () -> noteList.getAll().add(new Note("Another")));
    }

    @Test
    public void equals() {
        NoteList notes = new NoteList(List.of(new Note("One"), new Note("Two")));

        assertTrue(notes.equals(new NoteList(List.of(new Note("One"), new Note("Two")))));
        assertTrue(notes.equals(notes));

        assertFalse(notes.equals(null));
        assertFalse(notes.equals(1));
        assertFalse(notes.equals(new NoteList(List.of(new Note("One")))));
    }

    @Test
    public void toString_returnsUnderlyingListString() {
        NoteList noteList = new NoteList(List.of(new Note("One"), new Note("Two")));
        assertEquals("[One, Two]", noteList.toString());
    }
}
