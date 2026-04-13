package seedu.address.logic.commands;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Undoes the last mutating command.
 */
public class UndoCommand extends Command {
    private static final Logger logger = LogsCenter.getLogger(UndoCommand.class);

    public static final String COMMAND_WORD = ":undo";
    public static final String MESSAGE_SUCCESS = "Undo successful!";
    public static final String MESSAGE_NOTHING_TO_UNDO = "Nothing to undo! You can only undo once after a command.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Runnable undoAction = model.getUndoAction();
        if (undoAction == null) {
            logger.fine("Undo requested, but there is no undo action available.");
            throw new CommandException(MESSAGE_NOTHING_TO_UNDO);
        }
        undoAction.run();
        model.clearUndoAction();
        logger.info("Undo action executed.");
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
