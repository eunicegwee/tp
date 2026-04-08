package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

public class StarCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToStar = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person starredPerson = personToStar.withStar(true);

        StarCommand starCommand = new StarCommand(INDEX_FIRST_PERSON);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToStar, starredPerson);

        CommandResult expectedCommandResult = CommandResult.createWithPerson(
                String.format(StarCommand.MESSAGE_STARRED_PERSON_SUCCESS, starredPerson.getName()),
                starredPerson);

        assertCommandSuccess(starCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_alreadyStarredUnfilteredList_throwsCommandException() {
        Person personToStar = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person starredPerson = personToStar.withStar(true);
        model.setPerson(personToStar, starredPerson);

        StarCommand starCommand = new StarCommand(INDEX_FIRST_PERSON);

        assertCommandFailure(starCommand, model, StarCommand.MESSAGE_ALREADY_STARRED);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        StarCommand starCommand = new StarCommand(outOfBoundIndex);

        assertCommandFailure(starCommand, model, "The person index provided is invalid");
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToStar = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person starredPerson = personToStar.withStar(true);

        StarCommand starCommand = new StarCommand(INDEX_FIRST_PERSON);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);
        expectedModel.setPerson(personToStar, starredPerson);

        CommandResult expectedCommandResult = CommandResult.createWithPerson(
                String.format(StarCommand.MESSAGE_STARRED_PERSON_SUCCESS, starredPerson.getName()),
                starredPerson);

        assertCommandSuccess(starCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        StarCommand starCommand = new StarCommand(INDEX_SECOND_PERSON);

        assertCommandFailure(starCommand, model, "The person index provided is invalid");
    }

    @Test
    public void equals() {
        StarCommand starFirstCommand = new StarCommand(INDEX_FIRST_PERSON);
        StarCommand starSecondCommand = new StarCommand(INDEX_SECOND_PERSON);

        assertTrue(starFirstCommand.equals(starFirstCommand));
        assertTrue(starFirstCommand.equals(new StarCommand(INDEX_FIRST_PERSON)));

        assertFalse(starFirstCommand.equals(null));
        assertFalse(starFirstCommand.equals(1));
        assertFalse(starFirstCommand.equals(starSecondCommand));
    }
}
