---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `:delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute(":delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `:delete 5` command to delete the 5th person in the address book. The `:delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `:delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `:add n/David …` to add a new person. The `:add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user then decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `:list`. Commands that do not modify the address book, such as `:list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `:clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `:add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `:delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Developers who prefer keyboard-driven workflows

**Value proposition**: Vim-ify the experience for developers who are more used to the Vim interface — provide a keyboard-first, modal interaction model that lets developers navigate and edit contacts without leaving the keyboard so they feel comfortable and at home.


### User stories

| ID   | Priority | As a …​                                    | I want to …​                 | So that I can…​                                                        |
|------|----------|--------------------------------------------|------------------------------|------------------------------------------------------------------------|
| US-01 | Medium   | new user                                   | see sample contacts when I first launch the app | I understand how entries are structured                 |
| US-02 | Medium   | new user                                   | access a help guide         | I know what commands are available                 |
| US-03 | High     | new user                                   | add a contact with name, phone number, and email | I can store basic information             |
| US-04 | Medium   | new user                                   | assign tags when adding a contact | I can categorize people immediately           |
| US-05 | Medium   | new user                                   | visually distinguish fields (e.g., name, phone, email, tags) using different colours | visually distinguish fields (e.g., name, phone, email, tags) using different colours |
| US-06 | High     | user                                       | list all contacts           | I can review everyone I've added                 |
| US-07 | High     | user                                       | search by name             | I can quickly find someone's contact details                 |
| US-08 | Low      | user                                       | copy contact information easily | I can paste it into other apps like Telegram or email                 |
| US-09 | Medium   | user with multiple contacts sharing the same name | see additional identifiers (e.g., tags or email preview) in search results | I can differentiate them easily                 |
| US-10 | Medium   | user                                       | search using both name and tag together | I can narrow down results more precisely                 |
| US-11 | High     | user                                       | rely on consistent UI behaviour | the app feels predictable and efficient                 |
| US-12 | Medium   | user                                       | filter contacts by tag     | I can view only people in a specific role or project                 |
| US-13 | High     | user                                       | view full details of a selected contact | I can verify I have the correct person                 |
| US-14 | Medium   | user                                       | sort search results meaningfully | similar names do not confuse me                 |
| US-15 | Medium   | user                                       | mark certain contacts as favorites | I can access frequently contacted people faster                 |
| US-16 | Medium   | user                                       | add tags to an existing contact | I can update their roles over time                 |
| US-17 | High     | user                                       | edit a specific tag without deleting other tags | I do not accidentally lose information                 |
| US-18 | Medium   | user                                       | remove a single tag from a contact | I can keep tags accurate                 |
| US-19 | Medium   | user                                       | view all existing tags in the system | I know what categories I have created                 |
| US-20 | Low      | user                                       | bulk-edit tags for multiple contacts | I save time when projects change                 |
| US-21 | High     | user                                       | avoid accidental deletion of all tags when editing one tag | my data remains intact                 |
| US-22 | High     | user                                       | edit a contact's details   | I can update outdated information                 |
| US-23 | Medium   | user                                       | delete a contact by specifying something more meaningful than index (e.g., name + tag) | I do not delete the wrong person                 |
| US-24 | High     | user                                       | preview the contact before confirming deletion | I avoid mistakes                 |
| US-25 | Medium   | user                                       | undo my last action        | I can recover from accidental deletions                 |
| US-26 | Low      | user                                       | archive contacts instead of permanently deleting them | I can restore them later if needed                 |
| US-27 | High     | user                                       | receive confirmation before clearing the entire list | I do not lose data impulsively                 |
| US-28 | Medium   | user                                       | back up my contact list    | I do not permanently lose information                 |
| US-29 | Medium   | user                                       | restore from a backup      | I can recover after accidental clearing                 |
| US-30 | Medium   | user with over 100 contacts                | rely on powerful search instead of scrolling | I can find people efficiently                 |
| US-31 | Low      | user                                       | paginate or limit displayed results | large lists are manageable                 |
| US-32 | Medium   | user                                       | group contacts by tag      | I can view teams separately                 |
| US-33 | Low      | user managing multiple teams               | create logical groupings   | work and personal contacts do not mix                 |
| US-34 | Low      | user                                       | quickly clear completed or irrelevant contacts | the list remains relevant                 |
| US-35 | Medium   | user                                       | use the app for both professional and personal contacts | everything is centralized                 |
| US-36 | Medium   | user                                       | tag contacts as "family" or "friends" | I can separate them logically                 |
| US-37 | Low      | user                                       | search by location (e.g., 'Bishan') | I can find someone nearby                 |
| US-38 | Medium   | user                                       | store address information   | I can plan gatherings easily                 |
| US-39 | High     | user                                       | quickly retrieve a phone number | I can call someone immediately                 |
| US-40 | High     | user                                       | quickly retrieve an email address | I can send messages efficiently                 |
| US-41 | Medium   | frequent user                              | use keyboard shortcuts     | I can work faster                 |
| US-42 | Medium   | frequent user                              | autocomplete contact names when typing | I reduce typing effort                 |
| US-43 | Low      | frequent user                              | pin important contacts     | I do not need to search repeatedly                 |
| US-44 | Low      | frequent user                              | view recently accessed contacts | I can quickly reconnect with them                 |
| US-45 | Medium   | frequent user                              | search partial matches     | I do not need exact spelling                 |
| US-46 | Medium   | user managing projects                     | group contacts by project  | I can easily manage and view team members for a specific project    |
| US-47 | Medium   | user                                       | tag contacts by the programming language they use | I can search for a person who knows a specific language (e.g. Java) immediately |

### Use cases

#### Use Case 1: Updating a Contact's Role/Tag

**Goal:** A developer needs to update a contact's role/tag in the system for a new project. They need a way to easily find and modify the correct contact entry.

| Step | Action | Outcome/System Response | Corresponding User Story (Reference) |
|------|--------|-------------------------|--------------------------------------|
| 1 | Developer searches for the contact by name: `:list n/Alice` | The Contact List Panel displays multiple entries for "Alice" along with their distinguishing tags and emails. | US-07 (Search by name), US-09 (Additional identifiers in search) |
| 2 | Developer reviews the list and uses the index to select the correct "Alice": `:view 3` | The Browser Panel displays the full details for Alice (index 3). | US-13 (View full details) |
| 3 | Developer executes the edit command to add a new tag for the project: `:edit 3 t/new-project` | The system updates the contact. Message displayed: `Updated contact: Alice. New tag 'new-project' added.` | US-16 (Add tags to an existing contact), US-22 (Edit contact details) |
| 4 | Developer views the list to confirm the change: `:list` | The Contact List Panel shows Alice (index 3) with the new tag displayed. | US-06 (List all contacts) |


#### Use Case 2: Safely Deleting an Outdated Contact

**Goal:** A developer needs to safely delete an outdated contact entry while ensuring they don't accidentally remove the wrong person or lose valuable data.

| Step | Action | Outcome/System Response | Corresponding User Story (Reference) |
|------|--------|-------------------------|--------------------------------------|
| 1 | Developer issues the delete command using the currently displayed index: `:delete 7` | The Result Display area shows a preview of contact 7 (Name, Phone, Email, Tags). Message displayed: `Contact to be deleted: \[Kai Jie, p/92345678, e/kj@work.com, t/FormerColleague\]. Are you sure you want to delete?` (MVP simplifies confirmation in the UI). | US-24 (Preview contact before deletion) |
| 2 | Developer verifies the preview details (Name, Phone) are correct for Kai Jie. | N/A (verification step) | US-13 (View full details, implicitly used for verification) |
| 3 | Developer confirms the deletion (assume a future confirmation mechanism is added, or MVP immediate action): `:delete 7 confirm` | The contact is removed from the list. Message displayed: `Deleted contact: Kai Jie.` The Contact List Panel updates. | US-24 (Preview/Safer deletion) |
| 4 | Developer immediately realizes they deleted the wrong person and attempts to recover: `:undo` | The system restores the deleted contact. Message displayed: `Restored contact: \[Deleted Contact Name\]`. | US-25 (Undo last action) |


#### Use Case 3: Initial Setup and Categorization

**Goal:** A developer needs to quickly add new contacts and categorize them using tags for effective organization and later filtering.

| Step | Action | Outcome/System Response | Corresponding User Story (Reference) |
|------|--------|-------------------------|--------------------------------------|
| 1 | Developer adds a new contact and tags them simultaneously: `:add n/Benny p/98765432 e/benny@biz.com t/Marketing t/Partner` | Message displayed: `New contact added: Benny.` The new contact appears in the list view. | US-03 (Add contact), US-04 (Assign tags when adding) |
| 2 | Developer quickly adds another contact: `:add n/Chloe p/88887777 e/chloe@rnd.com t/Engineer` | Message displayed: `New contact added: Chloe.` | US-03 (Add contact) |
| 3 | Developer filters the list to view only their marketing contacts: `:filter t/Marketing` | The Contact List Panel is updated to show only contacts tagged 'Marketing' (e.g., Benny). Message displayed: `Filtered contacts by tag: Marketing.` | US-12 (Filter contacts by tag) |


### Non-Functional Requirements (NFRs)

#### 1. Performance Requirements (P)

| Requirement ID | Requirement | Description | Rationale |
|---------------:|-------------|-------------|-----------|
| P-01 | Search Speed | The application must return search results (e.g., using `:list n/NAME`) in less than 0.5 seconds for a contact list size of up to 500 entries. | Ensure a fast, keyboard-driven workflow, matching the "Vim-ify the experience" value proposition.
| P-02 | Startup Time | The application must fully load and be ready to accept commands within 1.0 second. | Maintain user flow efficiency, especially for a CLI tool used throughout the day.
| P-03 | CRUD Operation Speed | Basic operations (Add, Delete, Edit) must complete in less than 0.1 seconds after command execution. | Core contact management should be instantaneous.

#### 2. Usability Requirements (U)

| Requirement ID | Requirement | Description | Rationale |
|---------------:|-------------|-------------|-----------|
| U-01 | Vim-like UI Consistency | The CLI interface must maintain visual consistency across all command outputs (US-11). It should leverage color coding (US-05) to clearly distinguish fields (Name, Phone, Email, Tags). | Meet the core value proposition: "Vim-ify the experience." |
| U-02 | Error Clarity | All error messages must be clear, concise, and explicitly suggest the correct command format or parameter requirement. | Enhance developer experience by providing immediate, actionable feedback. |
| U-03 | Help Accessibility | A command (`:help`) must immediately display a comprehensive guide of all available commands and their formats (US-02). | Essential for initial usability and quick command lookup. |

#### 3. Scalability Requirements (S)

| Requirement ID | Requirement | Description | Rationale |
|---------------:|-------------|-------------|-----------|
| S-01 | Contact Capacity | The application must reliably handle and store a minimum of 500 unique contact entries without a noticeable degradation in performance (P-01). | Accommodate a lead developer's potentially large network (Scenario 20th use mentions 100+ contacts). |
| S-02 | Tag Capacity | The system must support a minimum of 50 unique tags and allow any single contact to have up to 10 tags. | Allow for rich categorization of contacts based on roles, projects, and personal context. |

#### 4. Reliability Requirements (R)

| Requirement ID | Requirement | Description | Rationale |
|---------------:|-------------|-------------|-----------|
| R-01 | Data Persistence | All contact data must be automatically saved/persisted upon successful execution of any modifying command (Add, Edit, Delete, Clear). | Prevent data loss in case of unexpected shutdown. |
| R-02 | Input Validation | The system must strictly enforce parameter validation rules (e.g., 8-digit phone, valid email format) as defined in the MVP specification for the `:add` command. | Ensure data integrity (US-03, US-39, US-40). |
| R-03 | Safe Clear/Delete | The system must require a distinct confirmation step before executing the `:clear` command (US-27) or any action that deletes data (US-24). | Prevent accidental loss of a large amount of data. |


### Glossary

| Term          | Definition                                                                                                                                                                                                                    |
|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 0rb1t         | The name of the contact management application.                                                                                                                                                                               |
| CLI           | Command Line Interface. The environment in which 0rb1t operates, taking text-based commands.                                                                                                                                  |
| Persona       | A concrete representation of the target user, used internally for design reference. (e.g., Brandon)                                                                                                                           |
| MVP           | Minimum Viable Product. The version of the product containing only the essential features (Add, List, View, Delete, Clear) required to satisfy initial user needs.                                                            |
| Index         | The sequential, positive integer displayed next to a contact entry in the Contact List Panel, used to reference the contact for commands like `:view` or `:delete`.                                                           |
| Tag           | A label or keyword assigned to a contact for categorization (e.g., "Developer", "Marketing", "Friend").                                                                                                                       |
| Browser Panel | The dedicated UI area in the CLI that displays the full, organized details of a single, currently selected contact.                                                                                                           |
| US-xx         | A specific User Story ID used to track product requirements.                                                                                                                                                                  |
| Developer     | The primary user of the 0rb1t application. The target user profile is defined as "Developers who prefer keyboard-driven workflows". |
