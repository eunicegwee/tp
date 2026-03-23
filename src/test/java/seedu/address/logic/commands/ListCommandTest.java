package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.AddressContainsKeywordPredicate;
import seedu.address.model.person.EmailContainsKeywordPredicate;
import seedu.address.model.person.ListCommandPredicate;
import seedu.address.model.person.PhoneContainsKeywordPredicate;
import seedu.address.model.person.TagContainsKeywordPredicate;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_tagFilter_showsMatchingPersons() {
        // ALICE, BENSON, DANIEL have "friends" tag
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        TagContainsKeywordPredicate predicate =
                new TagContainsKeywordPredicate(Collections.singletonList("friends"));
        ListCommand command = new ListCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_tagFilter_noMatchingPersons() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        TagContainsKeywordPredicate predicate =
                new TagContainsKeywordPredicate(Collections.singletonList("nonexistent"));
        ListCommand command = new ListCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleTagFilter_showsUnion() {
        // "owesMoney" is only on BENSON, "friends" is on ALICE, BENSON, DANIEL — OR gives all 3
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        TagContainsKeywordPredicate predicate =
                new TagContainsKeywordPredicate(Arrays.asList("owesMoney", "friends"));
        ListCommand command = new ListCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_uniqueTagFilter_showsSinglePerson() {
        // Only BENSON has "owesMoney"
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        TagContainsKeywordPredicate predicate =
                new TagContainsKeywordPredicate(Collections.singletonList("owesMoney"));
        ListCommand command = new ListCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.singletonList(BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_emailFilter_showsMatchingPersons() {
        // "johnd" matches BENSON's email
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        EmailContainsKeywordPredicate emailPredicate =
                new EmailContainsKeywordPredicate(Collections.singletonList("johnd"));
        ListCommandPredicate predicate = new ListCommandPredicate(null, emailPredicate, null, null);
        ListCommand command = new ListCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_tagAndEmailFilter_showsIntersection() {
        // "friends" matches ALICE, BENSON, DANIEL
        // "example.com" matches everyone
        // Intersection should be ALICE, BENSON, DANIEL
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        TagContainsKeywordPredicate tagPredicate =
                new TagContainsKeywordPredicate(Collections.singletonList("friends"));
        EmailContainsKeywordPredicate emailPredicate =
                new EmailContainsKeywordPredicate(Collections.singletonList("example.com"));
        ListCommandPredicate predicate = new ListCommandPredicate(tagPredicate, emailPredicate, null, null);
        ListCommand command = new ListCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_phoneFilter_showsMatchingPersons() {
        // "94351253" matches ALICE's phone
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        PhoneContainsKeywordPredicate phonePredicate =
                new PhoneContainsKeywordPredicate(Collections.singletonList("94351253"));
        ListCommandPredicate predicate = new ListCommandPredicate(null, null, phonePredicate, null);
        ListCommand command = new ListCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_addressFilter_showsMatchingPersons() {
        // "jurong" matches ALICE's address
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        AddressContainsKeywordPredicate addressPredicate =
                new AddressContainsKeywordPredicate(Collections.singletonList("jurong"));
        ListCommandPredicate predicate = new ListCommandPredicate(null, null, null, addressPredicate);
        ListCommand command = new ListCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_allFilters_showsIntersection() {
        // "friends" matches ALICE, BENSON, DANIEL
        // "example.com" matches everyone
        // "94351253" matches ALICE
        // "jurong" matches ALICE
        // Intersection should be ALICE
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        TagContainsKeywordPredicate tagPredicate =
                new TagContainsKeywordPredicate(Collections.singletonList("friends"));
        EmailContainsKeywordPredicate emailPredicate =
                new EmailContainsKeywordPredicate(Collections.singletonList("example.com"));
        PhoneContainsKeywordPredicate phonePredicate =
                new PhoneContainsKeywordPredicate(Collections.singletonList("94351253"));
        AddressContainsKeywordPredicate addressPredicate =
                new AddressContainsKeywordPredicate(Collections.singletonList("jurong"));
        ListCommandPredicate predicate = new ListCommandPredicate(
                tagPredicate, emailPredicate, phonePredicate, addressPredicate);
        ListCommand command = new ListCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void equals() {
        TagContainsKeywordPredicate firstPredicate =
                new TagContainsKeywordPredicate(Collections.singletonList("friends"));
        TagContainsKeywordPredicate secondPredicate =
                new TagContainsKeywordPredicate(Collections.singletonList("owesMoney"));

        ListCommand listAllCommand = new ListCommand();
        ListCommand listFirstCommand = new ListCommand(firstPredicate);
        ListCommand listSecondCommand = new ListCommand(secondPredicate);

        // same object -> returns true
        assertTrue(listAllCommand.equals(listAllCommand));
        assertTrue(listFirstCommand.equals(listFirstCommand));

        // same values -> returns true
        ListCommand listFirstCommandCopy = new ListCommand(firstPredicate);
        assertTrue(listFirstCommand.equals(listFirstCommandCopy));

        // different types -> returns false
        assertFalse(listFirstCommand.equals(1));

        // null -> returns false
        assertFalse(listFirstCommand.equals(null));

        // different predicate -> returns false
        assertFalse(listFirstCommand.equals(listSecondCommand));

        // list all vs list with filter -> returns false
        assertFalse(listAllCommand.equals(listFirstCommand));
    }
}
