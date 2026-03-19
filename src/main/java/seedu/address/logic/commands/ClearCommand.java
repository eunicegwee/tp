package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = ":clear";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";
    public static final String MESSAGE_CONFIRM_CLEAR =
            "Are you sure you want to clear the entire address book?\n"
            + "Type 'yes' to confirm. Any other input will be taken as no.";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(MESSAGE_CONFIRM_CLEAR, false, false, () -> {
            model.setAddressBook(new AddressBook());
            model.clearTagsRegistry();
            return new CommandResult(MESSAGE_SUCCESS);
        }, null);
    }
}
