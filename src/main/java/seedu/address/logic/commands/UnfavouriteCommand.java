package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Removes the favourite status from a person.
 */
public class UnfavouriteCommand extends Command {

    public static final String COMMAND_WORD = ":unfavourite";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes the favourite status from the contact identified by the index number used in"
            + " the displayed contact list \n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_UNFAVOURITE_PERSON_SUCCESS =
            "Unstarred contact: %1$s";

    public static final String MESSAGE_NOT_FAVOURITE =
            "This contact is not starred.";

    private final Index targetIndex;

    public UnfavouriteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUnfavourite = lastShownList.get(targetIndex.getZeroBased());

        if (!personToUnfavourite.isFavourite()) {
            throw new CommandException(MESSAGE_NOT_FAVOURITE);
        }

        Person updatedPerson = personToUnfavourite.withFavourite(false);
        model.setPerson(personToUnfavourite, updatedPerson);

        return CommandResult.createWithPerson(
                String.format(MESSAGE_UNFAVOURITE_PERSON_SUCCESS, updatedPerson.getName()),
                updatedPerson
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UnfavouriteCommand)) {
            return false;
        }
        UnfavouriteCommand otherCommand = (UnfavouriteCommand) other;
        return targetIndex.equals(otherCommand.targetIndex);
    }
}
