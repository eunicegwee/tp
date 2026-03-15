package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
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

    /**
     * Creates a {@code PersonDetailsPanel} and loads the associated FXML layout.
     */
    public PersonDetailsPanel() {
        super(FXML);
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
    }
}
