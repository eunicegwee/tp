package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Lists all tags in the TagsRegistry.
 */
public class TagsCommand extends Command {

    public static final String COMMAND_WORD = ":tags";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + ": Lists all tags in the address book.\n"
                    + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Tags: %1$s";
    public static final String MESSAGE_NO_TAGS = "No tags found.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        String tags = model.getFormattedTags();

        if (tags.isEmpty()) {
            return new CommandResult(MESSAGE_NO_TAGS);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, tags));
    }
}
