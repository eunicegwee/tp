package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.function.Predicate;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Lists all persons in the address book to the user.
 * Optionally filters by tag.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = ":list";

    public static final String MESSAGE_SUCCESS = "Listed all persons";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all persons. "
            + "Optionally filters by one or more tags (OR relationship).\n"
            + "Parameters: [" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_TAG + "friend " + PREFIX_TAG + "colleague";

    private final Predicate<Person> predicate;

    /**
     * Creates a ListCommand that shows all persons.
     */
    public ListCommand() {
        this.predicate = PREDICATE_SHOW_ALL_PERSONS;
    }

    /**
     * Creates a ListCommand that filters persons by the given predicate.
     */
    public ListCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        if (predicate == PREDICATE_SHOW_ALL_PERSONS) {
            return new CommandResult(MESSAGE_SUCCESS);
        }
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ListCommand)) {
            return false;
        }

        ListCommand otherCommand = (ListCommand) other;
        return predicate.equals(otherCommand.predicate);
    }
}
