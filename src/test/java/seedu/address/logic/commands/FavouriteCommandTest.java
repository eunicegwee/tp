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

public class FavouriteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToFavourite = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person favouritedPerson = personToFavourite.withFavourite(true);

        FavouriteCommand favouriteCommand = new FavouriteCommand(INDEX_FIRST_PERSON);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToFavourite, favouritedPerson);

        CommandResult expectedCommandResult = CommandResult.createWithPerson(
                String.format(FavouriteCommand.MESSAGE_FAVOURITE_PERSON_SUCCESS, favouritedPerson.getName()),
                favouritedPerson);

        assertCommandSuccess(favouriteCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_alreadyFavouriteUnfilteredList_throwsCommandException() {
        Person personToFavourite = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person favouritedPerson = personToFavourite.withFavourite(true);
        model.setPerson(personToFavourite, favouritedPerson);

        FavouriteCommand favouriteCommand = new FavouriteCommand(INDEX_FIRST_PERSON);

        assertCommandFailure(favouriteCommand, model, FavouriteCommand.MESSAGE_ALREADY_FAVOURITE);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        FavouriteCommand favouriteCommand = new FavouriteCommand(outOfBoundIndex);

        assertCommandFailure(favouriteCommand, model, "The person index provided is invalid");
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToFavourite = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person favouritedPerson = personToFavourite.withFavourite(true);

        FavouriteCommand favouriteCommand = new FavouriteCommand(INDEX_FIRST_PERSON);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);
        expectedModel.setPerson(personToFavourite, favouritedPerson);

        CommandResult expectedCommandResult = CommandResult.createWithPerson(
                String.format(FavouriteCommand.MESSAGE_FAVOURITE_PERSON_SUCCESS, favouritedPerson.getName()),
                favouritedPerson);

        assertCommandSuccess(favouriteCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        FavouriteCommand favouriteCommand = new FavouriteCommand(INDEX_SECOND_PERSON);

        assertCommandFailure(favouriteCommand, model, "The person index provided is invalid");
    }

    @Test
    public void equals() {
        FavouriteCommand favouriteFirstCommand = new FavouriteCommand(INDEX_FIRST_PERSON);
        FavouriteCommand favouriteSecondCommand = new FavouriteCommand(INDEX_SECOND_PERSON);

        assertTrue(favouriteFirstCommand.equals(favouriteFirstCommand));
        assertTrue(favouriteFirstCommand.equals(new FavouriteCommand(INDEX_FIRST_PERSON)));

        assertFalse(favouriteFirstCommand.equals(null));
        assertFalse(favouriteFirstCommand.equals(1));
        assertFalse(favouriteFirstCommand.equals(favouriteSecondCommand));
    }
}
