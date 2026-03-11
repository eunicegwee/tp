package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.model.Model;

/**
 * Lists all persons in the address book to the user.
 *
 * <p>Supports a Vim-like colon prefix; canonical command is {@code ":list"} but the
 * legacy alias {@code "list"} is still accepted for compatibility.
 */
public class ListCommand extends Command {

    // Canonical (Vim-like) command word; legacy alias provided for backward compatibility
    public static final String COMMAND_WORD = ":list";
    public static final String ALIAS = "list";

    public static final String MESSAGE_SUCCESS = "Listed all persons";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
