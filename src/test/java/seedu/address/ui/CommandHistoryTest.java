package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandHistoryTest {

    private CommandHistory history;

    @BeforeEach
    public void setUp() {
        history = new CommandHistory();
    }

    @Test
    public void getPrevious_emptyHistory_returnsNull() {
        assertNull(history.getPrevious());
    }

    @Test
    public void getNext_emptyHistory_returnsNull() {
        assertNull(history.getNext());
    }

    @Test
    public void getPrevious_singleCommand_returnsCommand() {
        history.add(":list");
        assertEquals(":list", history.getPrevious());
    }

    @Test
    public void getPrevious_atOldestCommand_returnsNull() {
        history.add(":list");
        history.getPrevious(); // now at index 0
        assertNull(history.getPrevious());
    }

    @Test
    public void getNext_pastNewestCommand_returnsEmptyString() {
        history.add(":list");
        history.getPrevious(); // navigate back
        assertEquals("", history.getNext()); // navigate forward past newest
    }

    @Test
    public void getNext_alreadyPastNewest_returnsNull() {
        history.add(":list");
        history.getPrevious();
        history.getNext(); // now at "new command" position
        assertNull(history.getNext());
    }

    @Test
    public void navigation_multipleCommands_correctOrder() {
        history.add(":list");
        history.add(":list n/Alice");
        history.add(":delete 1");

        assertEquals(":delete 1", history.getPrevious());
        assertEquals(":list n/Alice", history.getPrevious());
        assertEquals(":list", history.getPrevious());
        assertNull(history.getPrevious());

        assertEquals(":list n/Alice", history.getNext());
        assertEquals(":delete 1", history.getNext());
        assertEquals("", history.getNext());
        assertNull(history.getNext());
    }

    @Test
    public void add_resetsIndexToEnd() {
        history.add(":list");
        history.add(":list n/Alice");
        history.getPrevious(); // :list n/Alice
        history.getPrevious(); // :list

        history.add(":help");
        // index should be reset; getPrevious returns the newest command
        assertEquals(":help", history.getPrevious());
    }

    @Test
    public void add_exceedsMaxSize_dropsOldest() {
        for (int i = 0; i < CommandHistory.MAX_HISTORY_SIZE + 5; i++) {
            history.add(":command" + i);
        }

        assertEquals(CommandHistory.MAX_HISTORY_SIZE, history.size());

        // Navigate to the oldest — should be command 5 (0–4 were dropped)
        String oldest = null;
        String command;
        while ((command = history.getPrevious()) != null) {
            oldest = command;
        }
        assertEquals(":command5", oldest);
    }

    @Test
    public void add_nullCommand_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> history.add(null));
    }
}
