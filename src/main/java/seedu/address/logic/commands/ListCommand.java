package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SORT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Comparator;
import java.util.function.Predicate;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Lists all persons in the address book to the user.
 * Optionally filters by tag, name, email, phone, and/or address.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = ":list";

    public static final String MESSAGE_SUCCESS = "Listed all persons";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all persons. "
            + "Optionally filters by tags, emails, phone numbers, addresses, and/or names. "
            + "Also supports sorting by name, phone, or favorites.\n"
            + "Multiple keywords for the same field use OR logic. Different fields are combined with AND logic.\n"
            + "Parameters: [" + PREFIX_TAG + "TAG]... [" + PREFIX_EMAIL + "EMAIL]... "
            + "[" + PREFIX_PHONE + "PHONE]... [" + PREFIX_ADDRESS + "ADDRESS]... [" + PREFIX_NAME + "NAME]... "
            + "[" + PREFIX_SORT + "[+|-]FIELD]... [" + PREFIX_SORT + "*]...\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_TAG + "friend " + PREFIX_EMAIL + "gmail "
            + PREFIX_PHONE + "9123 " + PREFIX_ADDRESS + "clementi " + PREFIX_NAME + "Alice " + PREFIX_SORT + "+n "
            + PREFIX_SORT + "*";

    private final Predicate<Person> predicate;
    private final Comparator<Person> comparator;

    /**
     * Creates a ListCommand that shows all persons.
     */
    public ListCommand() {
        this.predicate = PREDICATE_SHOW_ALL_PERSONS;
        this.comparator = null;
    }

    /**
     * Creates a ListCommand that filters persons by the given predicate.
     */
    public ListCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
        this.comparator = null;
    }

    /**
     * Creates a ListCommand that filters and sorts persons.
     */
    public ListCommand(Predicate<Person> predicate, Comparator<Person> comparator) {
        this.predicate = predicate;
        this.comparator = comparator;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        if (comparator != null) {
            model.updateSortedPersonList(comparator);
        } else {
            // If no comparator is provided, we might want to reset sorting or keep it.
            // Requirement doesn't specify. Assuming "list" without sort resets sort?
            // "list" without sort usually just shows the list.
            // If I type ":list", I expect to see the list. If it was sorted, maybe it stays sorted?
            // But if I type ":list s/+n", it sorts.
            // If I type ":list" alone, usually it resets the filter to show all.
            // So it should probably reset the sort too, to get back to default order (insertion order maybe?).
            model.updateSortedPersonList(null);
        }

        if (predicate.equals(PREDICATE_SHOW_ALL_PERSONS)) {
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

        // instanceof handles nulls
        if (!(other instanceof ListCommand)) {
            return false;
        }

        ListCommand otherListCommand = (ListCommand) other;
        // Check both predicate and comparator
        boolean predicateEquals = predicate.equals(otherListCommand.predicate);
        boolean comparatorEquals = (comparator == null && otherListCommand.comparator == null)
                || (comparator != null && comparator.equals(otherListCommand.comparator));

        return predicateEquals && comparatorEquals;
    }
}
