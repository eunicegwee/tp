package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores command history and allows navigation through previous commands
 * using up/down arrow keys. Stores up to {@code MAX_HISTORY_SIZE} commands.
 */
public class CommandHistory {
    public static final int MAX_HISTORY_SIZE = 64;

    private final List<String> history = new ArrayList<>();

    /**
     * Index pointing to the current position in history.
     * When equal to history.size(), the user is at the "new command" position.
     */
    private int currentIndex = 0;

    /**
     * Adds a command to the history. If the history exceeds the maximum size,
     * the oldest command is removed. Resets the navigation index to the end.
     */
    public void add(String command) {
        requireNonNull(command);
        history.add(command);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
        currentIndex = history.size();
    }

    /**
     * Navigates to the previous command in history.
     * Returns the previous command, or {@code null} if already at the oldest command.
     */
    public String getPrevious() {
        if (currentIndex <= 0) {
            return null;
        }
        currentIndex--;
        return history.get(currentIndex);
    }

    /**
     * Navigates to the next command in history.
     * Returns the next command, or an empty string if navigating past the newest command.
     * Returns {@code null} if already past the newest command.
     */
    public String getNext() {
        if (currentIndex >= history.size()) {
            return null;
        }
        currentIndex++;
        if (currentIndex == history.size()) {
            return "";
        }
        return history.get(currentIndex);
    }

    /**
     * Returns the number of commands in history.
     */
    public int size() {
        return history.size();
    }
}
