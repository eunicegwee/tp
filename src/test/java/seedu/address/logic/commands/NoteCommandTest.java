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
import seedu.address.model.person.Note;
import seedu.address.model.person.NoteList;
import seedu.address.model.person.Person;

public class NoteCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String noteContent = "Met at alumni event";
        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                new NoteList(personToEdit.getListOfNotes()).append(noteContent),
                personToEdit.isStarred()
        );

        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, noteContent);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(noteCommand, model,
                String.format(NoteCommand.MESSAGE_SUCCESS, personToEdit.getName(), noteContent), expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        NoteCommand noteCommand = new NoteCommand(outOfBoundIndex, "Met at alumni event");

        assertCommandFailure(noteCommand, model, "The person index provided is invalid");
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String noteContent = "Requested proposal follow-up";
        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                new NoteList(personToEdit.getListOfNotes()).append(noteContent),
                personToEdit.isStarred()
        );

        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, noteContent);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(noteCommand, model,
                String.format(NoteCommand.MESSAGE_SUCCESS, personToEdit.getName(), noteContent), expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        NoteCommand noteCommand = new NoteCommand(INDEX_SECOND_PERSON, "Met at alumni event");

        assertCommandFailure(noteCommand, model, "The person index provided is invalid");
    }

    @Test
    public void execute_whenPersonAlreadyHasMaxNotes_throwsCommandException() {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        NoteList maxedNoteList = new NoteList(java.util.stream.IntStream.range(0, NoteList.MAX_NOTES)
                .mapToObj(i -> new Note("Note " + i))
                .toList());
        Person maxedPerson = new Person(
                originalPerson.getName(),
                originalPerson.getPhone(),
                originalPerson.getEmail(),
                originalPerson.getAddress(),
                originalPerson.getTags(),
                maxedNoteList,
                originalPerson.isStarred()
        );
        model.setPerson(originalPerson, maxedPerson);

        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, "Overflow note");

        assertCommandFailure(noteCommand, model, NoteList.MESSAGE_MAX_NOTES);
    }

    @Test
    public void equals() {
        NoteCommand firstCommand = new NoteCommand(INDEX_FIRST_PERSON, "First note");
        NoteCommand secondCommand = new NoteCommand(INDEX_SECOND_PERSON, "Second note");

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(new NoteCommand(INDEX_FIRST_PERSON, "First note")));

        assertFalse(firstCommand.equals(null));
        assertFalse(firstCommand.equals(1));
        assertFalse(firstCommand.equals(secondCommand));
        assertFalse(firstCommand.equals(new NoteCommand(INDEX_FIRST_PERSON, "Different note")));
    }
}
