package seedu.address.ui;

import java.util.logging.Logger;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    private final PersonDetailsPanel personDetailsPanel;

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList} of persons
     * and links it to a {@code PersonDetailsPanel} to display selected person details.
     * <p>
     * This constructor sets up the list view with custom cells for each person and
     * adds listeners to ensure the details panel is always up to date:
     * <ul>
     *     <li>Whenever a person is selected in the list, the {@code PersonDetailsPanel} is updated automatically.</li>
     *     <li>When the list is modified (e.g., a person is added or removed), the panel ensures a valid selection:
     *         <ul>
     *             <li>If the list becomes empty, the details panel is cleared.</li>
     *             <li>If no person is selected, the last added person is selected by default.</li>
     *         </ul>
     *     </li>
     *     <li>When the panel is first initialized, the first person in the list is selected by default (if any).</li>
     * </ul>
     *
     * @param personList the list of persons to display in this panel
     * @param personDetailsPanel the details panel to update when a person is selected
     */
    public PersonListPanel(ObservableList<Person> personList, PersonDetailsPanel personDetailsPanel) {
        super(FXML);

        this.personDetailsPanel = personDetailsPanel;

        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());

        personListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> personDetailsPanel.display(newValue));

        if (!personList.isEmpty()) {
            personListView.getSelectionModel().selectFirst();
        }

        personListView.getItems().addListener((ListChangeListener<Person>) change -> {
            ObservableList<Person> list = personListView.getItems();

            if (list.isEmpty()) {
                personDetailsPanel.display(null);
            } else if (personListView.getSelectionModel().getSelectedItem() == null) {
                personListView.getSelectionModel().select(list.size() - 1); // select newly added person
            }
        });
    }

    /**
     * Selects the specified {@link Person} in the {@code personListView} and
     * scrolls the list to ensure the selected person is visible to the user.
     *
     * @param person The {@link Person} to be selected and brought into view in the list.
     */
    public void selectPerson(Person person) {
        personListView.getSelectionModel().select(person);
        personListView.scrollTo(person);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            getStyleClass().remove("favourite-list-cell");

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                if (person.isFavourite()) {
                    getStyleClass().add("favourite-list-cell");
                }
                setGraphic(new PersonCard(person, getIndex() + 1).getRoot());
            }
        }
    }

}
