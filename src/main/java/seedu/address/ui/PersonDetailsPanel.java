package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

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

    public PersonDetailsPanel() {
        super(FXML);
    }

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

    private void clear() {
        name.setText("");
        phone.setText("");
        email.setText("");
        address.setText("");
    }
}
