package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Marks a person as favourite and pins them to the top of the displayed list.
 */
public class FavouriteCommand extends Command {

    public static final String COMMAND_WORD = ":favourite";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the contact identified by the index number used in the displayed contact list as favourite.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_FAVOURITE_PERSON_SUCCESS =
            "Starred contact: %1$s";

    public static final String MESSAGE_ALREADY_FAVOURITE =
            "This contact is already starred.";

    private final Index targetIndex;

    public FavouriteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToFavourite = lastShownList.get(targetIndex.getZeroBased());

        if (personToFavourite.isFavourite()) {
            throw new CommandException(MESSAGE_ALREADY_FAVOURITE);
        }

        Person favouritedPerson = personToFavourite.withFavourite(true);
        model.setPerson(personToFavourite, favouritedPerson);

        model.setUndoAction(() -> model.setPerson(favouritedPerson, personToFavourite));

        return CommandResult.createWithPerson(
                String.format(MESSAGE_FAVOURITE_PERSON_SUCCESS, favouritedPerson.getName()),
                favouritedPerson
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FavouriteCommand)) {
            return false;
        }
        FavouriteCommand otherCommand = (FavouriteCommand) other;
        return targetIndex.equals(otherCommand.targetIndex);
    }
}
