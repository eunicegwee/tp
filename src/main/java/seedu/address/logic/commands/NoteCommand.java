package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.NoteList;
import seedu.address.model.person.Person;

/**
 * Appends a note to a person identified by their displayed index in the address book.
 */
public class NoteCommand extends Command {

    public static final String COMMAND_WORD = ":note";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a note to the person identified "
            + "by the index number used in the displayed person list. "
            + "The note will be appended to the person's existing notes.\n"
            + "Parameters: INDEX (must be a positive integer) NOTE\n"
            + "Example: " + COMMAND_WORD + " 1 met him at networking event";

    public static final String MESSAGE_SUCCESS = "Added note to %1$s: %2$s";

    private final Index index;
    private final String note;

    /**
     * Creates a {@code NoteCommand} to append the given {@code note}
     * to the person at the specified {@code index}.
     */
    public NoteCommand(Index index, String note) {
        this.index = index;
        this.note = note;
    }

    /**
     * Appends the note to the selected person and updates the model.
     *
     * @throws CommandException if the given index is invalid
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());

        NoteList updatedNotes = personToEdit.appendNote(note);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                updatedNotes,
                personToEdit.isFavourite()
        );

        model.setPerson(personToEdit, editedPerson);

        return new CommandResult(String.format(MESSAGE_SUCCESS, personToEdit.getName(), note));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof NoteCommand)) {
            return false;
        }

        NoteCommand otherCommand = (NoteCommand) other;
        return index.equals(otherCommand.index) && note.equals(otherCommand.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, note);
    }
}
