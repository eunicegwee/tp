package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void constructor_tagNameLongerThan32Characters_throwsIllegalArgumentException() {
        String invalidTagName = "a".repeat(Tag.MAX_LENGTH + 1);
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void constructor_uppercaseTag_storesLowercase() {
        Tag tag = new Tag("FrIeNdS");
        assertEquals("friends", tag.tagName);
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));

        assertTrue(Tag.isValidTagName("a".repeat(Tag.MAX_LENGTH)));
        assertFalse(Tag.isValidTagName("a".repeat(Tag.MAX_LENGTH + 1)));
    }

}
