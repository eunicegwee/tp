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

public class UnfavouriteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @BeforeEach
    public void setUp() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(firstPerson, firstPerson.withFavourite(true));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToUnfavourite = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        UnfavouriteCommand unfavouriteCommand = new UnfavouriteCommand(INDEX_FIRST_PERSON);
        Person unfavouritedPerson = personToUnfavourite.withFavourite(false);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToUnfavourite, unfavouritedPerson);

        CommandResult expectedCommandResult = CommandResult.createWithPerson(
                String.format(UnfavouriteCommand.MESSAGE_UNFAVOURITE_PERSON_SUCCESS, unfavouritedPerson.getName()),
                unfavouritedPerson);

        assertCommandSuccess(unfavouriteCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_notFavouriteUnfilteredList_throwsCommandException() {
        Person notFavouritePerson = null;
        int index = -1;

        for (int i = 0; i < model.getFilteredPersonList().size(); i++) {
            if (!model.getFilteredPersonList().get(i).isFavourite()) {
                notFavouritePerson = model.getFilteredPersonList().get(i);
                index = i;
                break;
            }
        }

        UnfavouriteCommand unfavouriteCommand = new UnfavouriteCommand(Index.fromZeroBased(index));

        assertCommandFailure(unfavouriteCommand, model, UnfavouriteCommand.MESSAGE_NOT_FAVOURITE);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UnfavouriteCommand unfavouriteCommand = new UnfavouriteCommand(outOfBoundIndex);

        assertCommandFailure(unfavouriteCommand, model, "The person index provided is invalid");
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToUnfavourite = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        if (!personToUnfavourite.isFavourite()) {
            model.setPerson(personToUnfavourite, personToUnfavourite.withFavourite(true));
            personToUnfavourite = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        }

        UnfavouriteCommand unfavouriteCommand = new UnfavouriteCommand(INDEX_FIRST_PERSON);
        Person unfavouritedPerson = personToUnfavourite.withFavourite(false);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);
        expectedModel.setPerson(personToUnfavourite, unfavouritedPerson);

        CommandResult expectedCommandResult = CommandResult.createWithPerson(
                String.format(UnfavouriteCommand.MESSAGE_UNFAVOURITE_PERSON_SUCCESS, unfavouritedPerson.getName()),
                unfavouritedPerson);

        assertCommandSuccess(unfavouriteCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        UnfavouriteCommand unfavouriteCommand = new UnfavouriteCommand(INDEX_SECOND_PERSON);

        assertCommandFailure(unfavouriteCommand, model, "The person index provided is invalid");
    }

    @Test
    public void equals() {
        UnfavouriteCommand unfavouriteFirstCommand = new UnfavouriteCommand(INDEX_FIRST_PERSON);
        UnfavouriteCommand unfavouriteSecondCommand = new UnfavouriteCommand(INDEX_SECOND_PERSON);

        assertTrue(unfavouriteFirstCommand.equals(unfavouriteFirstCommand));
        assertTrue(unfavouriteFirstCommand.equals(new UnfavouriteCommand(INDEX_FIRST_PERSON)));

        assertFalse(unfavouriteFirstCommand.equals(null));
        assertFalse(unfavouriteFirstCommand.equals(1));
        assertFalse(unfavouriteFirstCommand.equals(unfavouriteSecondCommand));
    }
}
