package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Marks a person as starred and pins them to the top of the displayed list.
 */
public class StarCommand extends Command {

    public static final String COMMAND_WORD = ":star";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the contact identified by the index number used in the displayed contact list as starred.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_STARRED_PERSON_SUCCESS =
            "Starred contact: %1$s";

    public static final String MESSAGE_ALREADY_STARRED =
            "This contact is already starred.";

    private final Index targetIndex;

    public StarCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToStar = lastShownList.get(targetIndex.getZeroBased());

        if (personToStar.isStarred()) {
            throw new CommandException(MESSAGE_ALREADY_STARRED);
        }

        Person starredPerson = personToStar.withStar(true);
        model.setPerson(personToStar, starredPerson);

        return CommandResult.createWithPerson(
                String.format(MESSAGE_STARRED_PERSON_SUCCESS, starredPerson.getName()),
                starredPerson
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof StarCommand)) {
            return false;
        }
        StarCommand otherCommand = (StarCommand) other;
        return targetIndex.equals(otherCommand.targetIndex);
    }
}
