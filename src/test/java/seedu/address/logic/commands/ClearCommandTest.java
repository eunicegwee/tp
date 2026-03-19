package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_emptyAddressBook_showsPreview() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        CommandResult expectedResult = new CommandResult(ClearCommand.MESSAGE_CONFIRM_CLEAR, false,
                false, () -> null, null);
        assertCommandSuccess(new ClearCommand(), model, expectedResult, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_showsPreview() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        CommandResult expectedResult = new CommandResult(ClearCommand.MESSAGE_CONFIRM_CLEAR, false,
                false, () -> null, null);
        assertCommandSuccess(new ClearCommand(), model, expectedResult, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_confirmClears() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        ClearCommand clearCommand = new ClearCommand();

        CommandResult previewResult = clearCommand.execute(model);
        assertTrue(previewResult.isAwaitingConfirmation());

        CommandResult confirmResult = previewResult.getConfirmationAction().get();
        assertEquals(ClearCommand.MESSAGE_SUCCESS, confirmResult.getFeedbackToUser());
        assertEquals(new AddressBook(), model.getAddressBook());
    }

}
