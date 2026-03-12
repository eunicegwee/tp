package seedu.address.logic.commands;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Shows the details of a single person to the user.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + ": Views the details of the contact identified "
                    + "by the index number used in the displayed contact list.\n"
                    + "Parameters: INDEX (must be a positive integer)\n"
                    + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_VIEW_PERSON_SUCCESS =
            "Viewing contact: %1$s";

    private final Index targetIndex;

    public ViewCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {

        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException("Invalid person index");
        }

        Person personToView = lastShownList.get(targetIndex.getZeroBased());

        return new CommandResult(
                String.format(MESSAGE_VIEW_PERSON_SUCCESS, personToView.getName())
        );
    }
}
