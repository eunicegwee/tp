package seedu.address.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;

/**
 * A UI component that displays detailed information of a {@link Person}.
 * <p>
 * This panel shows the person's name, phone number, email, and address.
 * It can be updated dynamically when a different person is selected on
 * the {@code PersonListPanel}.
 */
public class PersonDetailsPanel extends UiPart<Region> {

    private static final String FXML = "PersonDetailsPanel.fxml";

    @FXML
    private Label name;

    @FXML
    private Label phone;

    @FXML
    private Label email;

    @FXML
    private Label address;

    @FXML
    private FlowPane notes;

    /**
     * Creates a {@code PersonDetailsPanel} and loads the associated FXML layout.
     */
    public PersonDetailsPanel() {
        super(FXML);
        notes.prefWrapLengthProperty().bind(getRoot().widthProperty().subtract(24));
        notes.prefWidthProperty().bind(getRoot().widthProperty().subtract(24));
    }

    /**
     * Updates the panel to display the details of the given {@code person}.
     * If {@code person} is null, the panel is cleared.
     *
     * @param person the person whose details are to be displayed
     */
    public void display(Person person) {
        if (person == null) {
            clear();
            return;
        }

        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        email.setText(person.getEmail().value);
        address.setText(person.getAddress().value);
        notes.getChildren().clear();
        person.getNoteList().stream()
                .map(Note::toString)
                .map(this::createNoteLabel)
                .forEach(noteLabel -> notes.getChildren().add(noteLabel));
    }

    /**
     * Clears all the labels in the panel.
     * This is used when there is no person to display.
     */
    private void clear() {
        name.setText("");
        phone.setText("");
        email.setText("");
        address.setText("");
        notes.getChildren().clear();
    }

    private Label createNoteLabel(String note) {
        Label noteLabel = new Label(note);
        noteLabel.getStyleClass().add("note-box");
        noteLabel.setWrapText(true);
        noteLabel.setAlignment(Pos.CENTER);
        noteLabel.prefWidthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(160, Math.min(320, notes.getWidth() - 12)),
                notes.widthProperty()));
        return noteLabel;
    }
}
