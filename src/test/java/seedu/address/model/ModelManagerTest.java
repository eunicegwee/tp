package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setAddressBookFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookFilePath(path);
        assertEquals(path, modelManager.getAddressBookFilePath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }

    //=========== Tag Registry Tests =============================================================
    @Test
    public void addTags_tagsAppearInRegistry() {
        Person person = new PersonBuilder().withTags("friend").build();

        modelManager.addTags(person);

        String tags = modelManager.getFormattedTags();
        assertTrue(tags.contains("friend"));
    }

    @Test
    public void deleteTags_tagsRemovedFromRegistry() {
        Person person = new PersonBuilder().withTags("friend").build();
        modelManager.addTags(person);

        modelManager.deleteTags(person);

        String tags = modelManager.getFormattedTags();
        assertFalse(tags.contains("friend"));
    }

    @Test
    public void updateEditedTags_tagsUpdatedCorrectly() {
        Person oldPerson = new PersonBuilder().withTags("friend").build();
        Person editedPerson = new PersonBuilder().withTags("colleague").build();

        modelManager.addTags(oldPerson);
        modelManager.updateEditedTags(oldPerson, editedPerson);

        String tags = modelManager.getFormattedTags();

        assertFalse(tags.contains("friend"));
        assertTrue(tags.contains("colleague"));
    }

    @Test
    public void clearTagsRegistry_allTagsCleared() {
        Person person = new PersonBuilder().withTags("friend").build();
        modelManager.addTags(person);

        modelManager.clearTagsRegistry();

        assertEquals("", modelManager.getFormattedTags());
    }

    @Test
    public void getFormattedTags_emptyRegistry_returnsEmptyString() {
        assertEquals("", modelManager.getFormattedTags());
    }

    @Test
    public void setAddressBook_resetsTagsRegistry() {
        ModelManager model = new ModelManager();

        Person personA = new PersonBuilder().withTags("friend").build();
        model.addTags(personA);

        AddressBook newBook = new AddressBook();
        Person personB = new PersonBuilder().withTags("colleague").build();
        newBook.addPerson(personB);

        model.setAddressBook(newBook);

        String tags = model.getFormattedTags();

        assertFalse(tags.contains("friend"));
        assertTrue(tags.contains("colleague"));
    }

    @Test
    public void updateSortedPersonList_nullComparator_resetsSorting() {
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BENSON);

        // Sort by name descending
        modelManager.updateSortedPersonList((p1, p2) -> p2.getName().fullName.compareTo(p1.getName().fullName));
        assertEquals(Arrays.asList(BENSON, ALICE), modelManager.getFilteredPersonList());

        // Reset sorting
        modelManager.updateSortedPersonList(null);
        assertEquals(Arrays.asList(ALICE, BENSON), modelManager.getFilteredPersonList());
    }

    @Test
    public void updateSortedPersonList_validComparator_sortsList() {
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BENSON);

        // Sort by name descending
        modelManager.updateSortedPersonList((p1, p2) -> p2.getName().fullName.compareTo(p1.getName().fullName));
        assertEquals(Arrays.asList(BENSON, ALICE), modelManager.getFilteredPersonList());
    }

    @Test
    public void updateSortedPersonList_withFilter_sortsFilteredList() {
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BENSON);
        modelManager.addPerson(seedu.address.testutil.TypicalPersons.CARL);
        modelManager.addPerson(seedu.address.testutil.TypicalPersons.DANIEL);

        // Filter by name containing "a" (Alice, Carl, Daniel)
        modelManager.updateFilteredPersonList(p -> p.getName().fullName.toLowerCase().contains("a"));

        // Sort by name descending (Daniel, Carl, Alice)
        modelManager.updateSortedPersonList((p1, p2) -> p2.getName().fullName.compareTo(p1.getName().fullName));

        assertEquals(Arrays.asList(
                seedu.address.testutil.TypicalPersons.DANIEL,
                seedu.address.testutil.TypicalPersons.CARL,
                ALICE),
                modelManager.getFilteredPersonList());
    }

    @Test
    public void updateSortedPersonList_filteredList_resetsSortKeepsFilter() {
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BENSON);
        modelManager.addPerson(seedu.address.testutil.TypicalPersons.CARL);

        // Filter only ALICE and BENSON
        modelManager.updateFilteredPersonList(p ->
                p.getName().fullName.startsWith("A") || p.getName().fullName.startsWith("B"));

        // Sort descending: Benson, Alice
        modelManager.updateSortedPersonList((p1, p2) -> p2.getName().fullName.compareTo(p1.getName().fullName));
        assertEquals(Arrays.asList(BENSON, ALICE), modelManager.getFilteredPersonList());

        // Reset sort: Alice, Benson (Insertion order if added in that order)
        modelManager.updateSortedPersonList(null);
        assertEquals(Arrays.asList(ALICE, BENSON), modelManager.getFilteredPersonList());
    }
}
