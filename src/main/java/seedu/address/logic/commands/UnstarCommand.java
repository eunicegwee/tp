package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Removes the star status from a person.
 */
public class UnstarCommand extends Command {

    public static final String COMMAND_WORD = ":unstar";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes the star status from the contact identified by the index number used in"
            + " the displayed contact list \n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_UNSTARRED_PERSON_SUCCESS =
            "Unstarred contact: %1$s";

    public static final String MESSAGE_NOT_STARRED =
            "This contact is not starred.";

    private final Index targetIndex;

    public UnstarCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUnstar = lastShownList.get(targetIndex.getZeroBased());

        if (!personToUnstar.isStarred()) {
            throw new CommandException(MESSAGE_NOT_STARRED);
        }

        Person updatedPerson = personToUnstar.withStar(false);
        model.setPerson(personToUnstar, updatedPerson);

        return CommandResult.createWithPerson(
                String.format(MESSAGE_UNSTARRED_PERSON_SUCCESS, updatedPerson.getName()),
                updatedPerson
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UnstarCommand)) {
            return false;
        }
        UnstarCommand otherCommand = (UnstarCommand) other;
        return targetIndex.equals(otherCommand.targetIndex);
    }
}
