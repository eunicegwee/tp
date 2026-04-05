package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NoteTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Note(null));
    }

    @Test
    public void constructor_blankNote_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Note(""));
        assertThrows(IllegalArgumentException.class, () -> new Note("   "));
    }

    @Test
    public void constructor_tooLongNote_throwsIllegalArgumentException() {
        String tooLongNote = "a".repeat(Note.MAX_LENGTH + 1);
        assertThrows(IllegalArgumentException.class, () -> new Note(tooLongNote));
    }

    @Test
    public void isValidNote() {
        assertFalse(Note.isValidNote(""));
        assertFalse(Note.isValidNote("   "));
        assertTrue(Note.isValidNote("a".repeat(Note.MAX_LENGTH)));
        assertFalse(Note.isValidNote("a".repeat(Note.MAX_LENGTH + 1)));
    }

    @Test
    public void equals() {
        Note note = new Note("Follow up tomorrow");

        assertTrue(note.equals(new Note("Follow up tomorrow")));
        assertTrue(note.equals(note));

        assertFalse(note.equals(null));
        assertFalse(note.equals(1));
        assertFalse(note.equals(new Note("Different note")));
    }

    @Test
    public void toString_returnsValue() {
        Note note = new Note("Met at career fair");
        assertTrue(note.toString().equals("Met at career fair"));
    }
}
