package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

public class UnstarCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @BeforeEach
    public void setUp() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(firstPerson, firstPerson.withStar(true));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToUnstar = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        UnstarCommand unstarCommand = new UnstarCommand(INDEX_FIRST_PERSON);
        Person unstarredPerson = personToUnstar.withStar(false);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToUnstar, unstarredPerson);

        CommandResult expectedCommandResult = CommandResult.createWithPerson(
                String.format(UnstarCommand.MESSAGE_UNSTARRED_PERSON_SUCCESS, unstarredPerson.getName()),
                unstarredPerson);

        assertCommandSuccess(unstarCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_notStarredUnfilteredList_throwsCommandException() {
        Person notStarredPerson = null;
        int index = -1;

        for (int i = 0; i < model.getFilteredPersonList().size(); i++) {
            if (!model.getFilteredPersonList().get(i).isStarred()) {
                notStarredPerson = model.getFilteredPersonList().get(i);
                index = i;
                break;
            }
        }

        UnstarCommand unstarCommand = new UnstarCommand(Index.fromZeroBased(index));

        assertCommandFailure(unstarCommand, model, UnstarCommand.MESSAGE_NOT_STARRED);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UnstarCommand unstarCommand = new UnstarCommand(outOfBoundIndex);

        assertCommandFailure(unstarCommand, model, "The person index provided is invalid");
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToUnstar = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        if (!personToUnstar.isStarred()) {
            model.setPerson(personToUnstar, personToUnstar.withStar(true));
            personToUnstar = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        }

        UnstarCommand unstarCommand = new UnstarCommand(INDEX_FIRST_PERSON);
        Person unstarredPerson = personToUnstar.withStar(false);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);
        expectedModel.setPerson(personToUnstar, unstarredPerson);

        CommandResult expectedCommandResult = CommandResult.createWithPerson(
                String.format(UnstarCommand.MESSAGE_UNSTARRED_PERSON_SUCCESS, unstarredPerson.getName()),
                unstarredPerson);

        assertCommandSuccess(unstarCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        UnstarCommand unstarCommand = new UnstarCommand(INDEX_SECOND_PERSON);

        assertCommandFailure(unstarCommand, model, "The person index provided is invalid");
    }

    @Test
    public void equals() {
        UnstarCommand unstarFirstCommand = new UnstarCommand(INDEX_FIRST_PERSON);
        UnstarCommand unstarSecondCommand = new UnstarCommand(INDEX_SECOND_PERSON);

        assertTrue(unstarFirstCommand.equals(unstarFirstCommand));
        assertTrue(unstarFirstCommand.equals(new UnstarCommand(INDEX_FIRST_PERSON)));

        assertFalse(unstarFirstCommand.equals(null));
        assertFalse(unstarFirstCommand.equals(1));
        assertFalse(unstarFirstCommand.equals(unstarSecondCommand));
    }
}
