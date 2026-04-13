---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# 0rb1t Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

---

## **Acknowledgements**

- The overall project structure and parts of the original codebase were adapted from [AddressBook-Level3](https://github.com/se-edu/addressbook-level3).
- The structure of this developer guide was adapted from the [AddressBook-Level4 Developer Guide](https://se-education.org/addressbook-level4/DeveloperGuide.html).
- The team also used AI-assisted development tools during the project:
  - Eunice used Copilot to assist with code writing, especially for writing test cases.
  - Jaeden used Codex to update `DarkTheme.css` for UI improvements and to brainstorm additional tests from written test cases, and used Copilot autocomplete to assist with code writing.
  - Kavish used Codex to add more extensive test cases and polish the developer guide, and used Copilot autocomplete to assist with code writing.
  - Harron used ChatGPT to assist with merge conflicts.

---

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

---

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The _Architecture Diagram_ above shows the high-level structure of 0rb1t.

0rb1t follows the same broad component split as AB3:

- [**`UI`**](#ui-component): Renders the interface and reacts to user interaction.
- [**`Logic`**](#logic-component): Parses and executes colon-prefixed commands.
- [**`Model`**](#model-component): Holds in-memory contact data, query state, and other derived state.
- [**`Storage`**](#storage-component): Reads and writes application data to disk.
- [**`Commons`**](#common-classes): Shared utilities used across components.

`MainApp` initializes these components in startup order. It loads configuration and preferences, constructs
`StorageManager`, `ModelManager`, `LogicManager`, and `UiManager`, and starts the JavaFX application.

Unlike a plain CRUD address book, 0rb1t also has a few architectural behaviors worth calling out up front:

- command handling is fully colon-driven through `AddressBookParser`
- destructive commands such as `:delete` and `:clear` use a logic-level confirmation mechanism
- the model owns both base data and derived/query state, including a `TagsRegistry`, a filtered list, a sorted list,
  and a single stored undo action
- the UI uses a master-detail layout, where the list panel and details panel stay synchronized through selection and
  command results

The following sequence diagram shows a representative command flow for `:delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

At a high level, the interaction works as follows:

1. The user enters a command into the UI.
1. `LogicManager` passes the text to `AddressBookParser`, which returns the matching `Command`.
1. The command executes against `Model`.
1. If the command requires confirmation, the first execution returns a `CommandResult` containing a deferred action
   instead of mutating immediately.
1. `LogicManager` executes that deferred action only when the next user input is `yes`.
1. After successful execution, `Storage` persists the updated address book and the UI updates based on the returned
   `CommandResult`.

This separation keeps responsibilities clear:

- `UI` is responsible for presentation and user interaction.
- `Logic` is responsible for parsing, command dispatch, and confirmation workflow.
- `Model` is responsible for data mutation, query state, and derived state such as tags and undo.
- `Storage` is responsible for persistence only.

Each main component defines an interface and is implemented by a corresponding manager class.
This reduces coupling between components and keeps the concrete implementation behind a small API surface.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The next few sections describe each component in more detail.

### UI component

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The `UI` component is responsible for rendering the application and forwarding user input to `Logic`.
Its top-level container is `MainWindow`, which assembles the main visible parts of the application:

- `CommandBox`, where users enter colon-prefixed commands
- `ResultDisplay`, which shows command feedback and error messages
- `PersonListPanel`, which displays the current filtered and sorted list of contacts
- `PersonDetailsPanel`, which shows the full details of the currently selected contact
- `StatusBarFooter`, which displays the address book file path

The UI uses JavaFX, and each major UI part is backed by a matching `.fxml` file in `src/main/resources/view`.

0rb1t uses a master-detail layout. `PersonListPanel` owns the current selection in the list view, while
`PersonDetailsPanel` renders the selected contact's name, phone, email, address and notes. 
This means the details panel can be updated in two ways:

- when the user changes the selected contact in the list
- when a command returns a `CommandResult` containing a `Person` to focus, such as `:view`, `:star`, or `:unstar`

This behavior is coordinated in two places:

- `PersonListPanel` listens to list selection changes and updates `PersonDetailsPanel`
- `MainWindow` handles `CommandResult` values after command execution and selects the returned contact in the list

The list panel also maintains a sensible default selection policy:

- if the list is non-empty on startup, the first contact is selected
- if the displayed list becomes empty, the details panel is cleared
- if a contact is added and no selection exists, the newly added contact is selected

As a result, the UI stays synchronized with both user-driven navigation and command-driven state changes without
embedding business logic in the UI layer.

### Logic component

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The `Logic` component is responsible for turning raw user input into executable commands and returning
`CommandResult` objects to the UI.

The main entry point is `LogicManager`. When the UI submits a command string, `LogicManager`:

1. receives the raw command text
2. parses it through `AddressBookParser`
3. executes the resulting `Command`
4. persists the updated address book through `Storage`
5. returns a `CommandResult` to the UI

Unlike the original AB3 flow, command parsing in 0rb1t is fully colon-based. All commands must begin with `:`,
for example `:add`, `:list`, or `:delete`.

`AddressBookParser` performs the first-stage split between command word and arguments, validates that the command
starts with `:`, and dispatches to a command-specific parser where needed.

The delete flow is split into two stages: the initial preview returned by `:delete 1`, and the follow-up confirmation
handled by `yes`.

<puml src="diagrams/DeleteSequenceDiagram-Preview.puml" alt="Delete preview sequence for the `:delete 1` command" />

<puml src="diagrams/DeleteSequenceDiagram-Confirmation.puml" alt="Delete confirmation sequence for the `yes` command" />

`CommandResult` is the contract between `Logic` and `UI`. Besides feedback text, it can also carry extra information
that affects UI or workflow behavior:

- whether the help window should be shown
- whether the application should exit
- whether a confirmation is pending
- which `Person` should be focused in the UI

One of the most important logic-level customizations in 0rb1t is its confirmation flow for destructive commands.
`LogicManager` owns a pending confirmation callback. Commands such as `:delete` and `:clear` return a
`CommandResult` containing a deferred action instead of mutating the model immediately.

While a confirmation is pending:

- `yes` executes the stored action
- `no` cancels it
- any other input keeps confirmation pending and reminds the user that confirmation is required

This design keeps confirmation handling centralized in `LogicManager` rather than spreading the workflow across
multiple UI classes or individual commands.

The parser subsystem contains one parser per command where argument parsing is non-trivial, such as
`AddCommandParser`, `EditCommandParser`, `ListCommandParser`, and `NoteCommandParser`.
These classes implement the shared `Parser` interface so they can be handled consistently in tests and in the parser
dispatch flow.

### Model component

<puml src="diagrams/ModelClassDiagram.puml" width="450" />

The `Model` component holds the application's in-memory state.

At its core, `ModelManager` owns the `AddressBook`, which stores the current set of `Person` objects.
However, the model in 0rb1t also owns several kinds of derived or query-related state that are important to the
product's behavior:

- a `FilteredList<Person>` representing the currently visible subset of contacts
- a `SortedList<Person>` layered on top of the filtered list to support ordering
- a derived `TagsRegistry` that tracks all tags currently in use
- a single stored undo callback for reversing the most recent mutating command

As a result, the model does more than store raw data. It also owns the current query state and some lightweight
workflow state.

The `Person` model has also been extended beyond the original AB3 shape. A `Person` in 0rb1t contains:

- identity fields such as name, phone, and email
- address data
- a set of tags
- a `NoteList`
- a starred flag

The filtered and sorted person lists are exposed to the rest of the application as observable views.
This allows the UI to react automatically when the visible set of contacts changes due to filtering, sorting,
addition, deletion, or editing.

`TagsRegistry` is an example of derived model state. It is not the source of truth for tags; instead, it is rebuilt
from the address book on startup and maintained incrementally as persons are added, deleted, or edited.
This makes it efficient to support commands such as `:tags` without rescanning the full address book each time.

Unlike `UI`, `Logic`, and `Storage`, the `Model` does not depend on the other main components.
It remains the central owner of domain data and related in-memory state.

### Storage component

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component is responsible for persistence.

`StorageManager` coordinates two storage concerns:

- address book data, through `AddressBookStorage`
- user preferences, through `UserPrefsStorage`

The current implementation uses JSON-backed storage classes. Address book data is stored through
`JsonAddressBookStorage`, while user preferences are stored through `JsonUserPrefsStorage`.

Person persistence has been extended to support 0rb1t-specific fields. `JsonAdaptedPerson` stores and restores:

- name
- phone
- email
- address
- tags
- notes
- starred state

This means features such as notes and favourite contacts survive application restarts without needing separate storage
mechanisms.

Not all model state is persisted directly. In particular, `TagsRegistry` is not saved as its own structure.
Instead, it is reconstructed from the persisted list of persons during model initialization.
This keeps the persisted format simpler and avoids storing redundant derived data.

The `Storage` component depends on parts of the model because its job is to serialize and deserialize model objects,
but it does not contain business logic for command execution or query behavior.

### Common classes

Classes shared across multiple components are kept in the `seedu.address.commons` package.

These include utility classes, configuration support, logging support, and general-purpose helpers used by the rest of
the application. Keeping them in a separate package prevents unrelated cross-component dependencies from accumulating in
the main feature packages.

---

## **Implementation**

This section describes noteworthy implementation details of features and workflows that are important to understanding
or extending the codebase.

### Confirmation flow

0rb1t uses an explicit confirmation workflow for destructive commands such as `:delete` and `:clear`.
Instead of mutating the model immediately, these commands first return a preview or warning message and wait for a
follow-up confirmation input.

This behavior is implemented centrally in `LogicManager`, which stores at most one pending confirmation action as a
`Supplier<CommandResult>`.

#### How it works

When a destructive command is executed, the command does not perform the mutation immediately.
Instead, it returns a `CommandResult` containing:

- feedback text to show the user
- a deferred confirmation action that performs the actual mutation

`LogicManager` inspects the returned `CommandResult`. If a confirmation action is present, it stores that action and
enters a pending-confirmation state.

While that state is active:

- `yes` executes the stored action
- `no` cancels the stored action
- any other input leaves the confirmation pending and returns a reminder message

Only after a confirmed action runs does the model actually change and the updated state get persisted.

#### Example: `:delete`

When the user runs `:delete 1`, the flow is:

1. `AddressBookParser` parses the command and returns a `DeleteCommand`.
1. `DeleteCommand` validates the index and prepares a preview of the target person.
1. `DeleteCommand` returns a `CommandResult` containing a deferred action instead of deleting immediately.
1. `LogicManager` stores the deferred action as the current pending confirmation.
1. If the next user input is `yes`, `LogicManager` runs the stored action, which deletes the person, updates tags,
   stores the undo action, and returns a success result.

This design keeps confirmation logic out of the UI layer and avoids duplicating the same yes/no handling across
multiple command classes.

### Tags Management

#### Registry Structure

The application maintains a derived `TagsRegistry` in the `ModelManager` to keep track of every tag currently in use.
This registry is separate from the `AddressBook` itself and is rebuilt from the person list during initialization.
As a result, the `:tags` command can list all active tags directly without scanning the full address book on every request.

<puml src="diagrams/TagsManagementClassDiagram.puml" width="320" />

The class diagram above focuses on the model-side structure behind tag management.

- `ModelManager` owns both the `AddressBook` and the `TagsRegistry`.
- `TagsRegistry` stores tag usage counts as `Map<Tag, Integer>`, so it can remove a tag only when the last person using that tag no longer has that tag.
- Commands interact with tag management through the `Model` API, which keeps the tag-maintenance logic centralized in the model layer.
- Read-only access such as `:tags` is handled through `Model#getFormattedTags()`.

#### Command-Level Tag Updates

The registry is updated by the following command flows:

- `:add` adds the person into the address book, then adds that person's tags into the registry.
- `:delete` removes the person and decrements tag counts for that person's tags after confirmation.
- `:edit` computes the edited tag set by starting from the current tags, removing any `dt/` tags, and then adding any `t/` tags.
- `:clear` resets the address book and clears the registry entirely.

#### Tag Edit Sequence

The following sequence diagram illustrates the main update path for tag edits at a high level.

<puml src="diagrams/TagsManagementEditSequenceDiagram.puml" width="800" />

When the user executes an edit command with tag changes, such as `:edit 3 t/backend dt/frontend`, the flow is as follows:

1. `LogicManager` passes the command text to `AddressBookParser`, which returns an `EditCommand`.
1. `EditCommand` updates the edited person in `ModelManager`.
1. `EditCommand` then asks `ModelManager` to refresh tag data for the edited person.
1. `ModelManager#updateEditedTags(...)` delegates to `TagsRegistry#updatePerson(...)` so the registry stays consistent with the latest person state.
1. A `CommandResult` is returned to the user after both the person record and the tag registry have been updated.

This design keeps the `TagsRegistry` consistent with the address book while avoiding duplicated tag-maintenance logic across commands.

### Undo feature

#### Implementation

0rb1t supports single-level undo for the most recent mutating command.

The undo mechanism uses a callback-based approach. Instead of storing full snapshots after every mutation, each
mutating command stores a `Runnable` in the `Model` that reverses its own effect.

The `Model` interface exposes three methods for this:

- `Model#setUndoAction(Runnable)` stores the undo action
- `Model#getUndoAction()` retrieves the stored undo action, or `null` if none exists
- `Model#clearUndoAction()` clears the stored undo action after it has been used

Only one undo action is stored at a time. When a new mutating command succeeds, it overwrites the previous undo
action. As a result, only the most recent successful mutating command can be undone.

Typical examples include:

- `:add` stores an undo action that deletes the newly added person
- confirmed `:delete` stores an undo action that re-adds the deleted person and restores their tags
- `:edit` stores an undo action that restores the original person and updates tag state accordingly
- `:note`, `:star`, and `:unstar` store undo actions that restore the previous `Person`
- confirmed `:clear` stores an undo action that restores the saved `AddressBook`

When the user executes `:undo`, `UndoCommand` retrieves the stored undo action from the model, runs it, and then
clears it. If no undo action is available, `:undo` fails with an error message instead of attempting to run a
non-existent action.

Commands that do not mutate application state, such as `:list` or `:view`, do not replace the stored undo action.

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

The following sequence diagram shows how a mutating command sets up its undo action during execution:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

#### Design considerations

**Aspect: How undo executes**

- **Alternative 1:** Store full snapshots of the address book after each mutation.
  - Pros: Simpler conceptual model and easier support for multi-level undo/redo.
  - Cons: Higher memory cost and more copying work on each mutating command.
- **Alternative 2 (current choice):** Store a command-specific callback that reverses the last mutation.
  - Pros: Lower memory usage and no need to snapshot the full model for every mutating command. The same callback-based
    design can also be extended in the future to support multiple undo levels without requiring a full history of model snapshots.
  - Cons: Each mutating command must implement its own reversal logic, and the design supports only single-level undo.

### List filtering and sorting

`:list` serves as the main query command for contacts.
It supports filtering by tag, email, phone, address, and name, and it can also apply sorting.

Filtering behavior is implemented through `ListCommandPredicate`:

- repeated values of the same prefix use OR logic
- different prefix groups combine with AND logic

Sorting behavior is implemented through `PersonComparator`, which supports multiple sort criteria.
The current sort fields are name, phone, and starred state.

The model applies these behaviors through a `FilteredList<Person>` wrapped by a `SortedList<Person>`.
This keeps query behavior in the model layer and allows the UI to react automatically to changes in the visible list.

The `s/*` sort is handled specially: it is always moved to the front of the sort criteria so starred contacts remain
pinned above non-starred contacts.

---

## **Documentation**

- [User Guide](UserGuide.md)
- [Setting up and getting started](SettingUp.md)
- [Logging guide](Logging.md)

---

## **Testing**

- [Testing guide](Testing.md)

---

## **Dev Ops**

- [DevOps guide](DevOps.md)

---

## **Appendix: Requirements**

### Product scope

**Target user profile**:

Developers who:

- manage a moderate to large number of contacts
- prefer keyboard-driven workflows over mouse-heavy interaction
- can type quickly
- are comfortable using command-based desktop applications
- want fast access to contact details, tags, and lightweight notes
- often need to organize contacts by role, project, or context

**Value proposition**: Vim-ify the experience for developers who are more used to the Vim interface — provide a keyboard-first interaction model that lets developers navigate and edit contacts without leaving the keyboard so they feel comfortable and at home.

### User stories

Priorities: High (must have) - `***`, Medium (nice to have) - `**`

| Priority | As a ...                                                | I want to ...                                                  | So that I can...                                             |
| -------- | ------------------------------------------------------- | -------------------------------------------------------------- | ------------------------------------------------------------ |
| `***`    | new developer user                                      | see sample contacts when I first launch the app                | understand how entries are structured                        |
| `***`    | new developer user                                      | access a help guide                                            | refer to command usage when I forget the syntax              |
| `***`    | developer                                               | add a contact with a name, phone number, email, and address    | store essential contact details                              |
| `***`    | developer                                               | list all contacts                                              | review everyone I have added                                 |
| `***`    | developer                                               | search for contacts by name                                    | find someone without scanning the whole list                 |
| `***`    | developer                                               | select a contact                                               | view the full details of a contact                           |
| `***`    | developer                                               | edit a contact's details                                       | keep information up to date                                  |
| `***`    | developer                                               | preview a contact and receive confirmation before deleting them | avoid deleting the wrong person                              |
| `***`    | developer                                               | receive confirmation before clearing the entire list           | avoid accidental mass deletion                               |
| `***`    | developer                                               | undo my last mutating action                                   | recover from mistakes quickly                                |
| `***`    | developer                                               | retrieve a phone number quickly                                | call someone immediately                                     |
| `***`    | developer                                               | retrieve an email address quickly                              | message someone immediately                                  |
| `***`    | developer                                               | retrieve an address quickly                                    | visit someone immediately                                    |
| `**`     | developer organizing contacts by role or project        | assign tags when adding a contact                              | categorize contacts immediately                              |
| `**`     | developer organizing contacts by role or project        | filter contacts by tag                                         | focus on a relevant group of contacts                        |
| `**`     | developer with many contacts                            | search using multiple filters                                  | narrow down results more precisely                           |
| `**`     | developer with many contacts                            | sort contacts meaningfully                                     | find contacts faster                                         |
| `**`     | developer maintaining a shortlist of important contacts | mark certain contacts as favourites                            | access frequently used contacts more quickly                 |
| `**`     | developer organizing contacts by role or project        | add tags to an existing contact without overwriting the others | update roles over time without losing information            |
| `**`     | developer organizing contacts by role or project        | remove specific tags from a contact                            | keep tag data accurate                                       |
| `**`     | developer organizing contacts by role or project        | view all tags currently in use                                 | understand how my contacts are organized                     |

### Use cases

For all use cases below, the **System** is 0rb1t and the **Actor** is the user, unless specified otherwise.

#### Use case: Add contact

**MSS**

1. User requests to add a contact with valid details.
2. 0rb1t adds the contact.
3. 0rb1t shows the updated contact list.

   Use case ends.

**Extensions**

- 1a. The command contains invalid input.

  - 1a1. 0rb1t shows an error message.

    Use case ends.

- 1b. A contact with the same name already exists.

  - 1b1. 0rb1t displays an error message if a contact with an existing name match (case-sensitive) is added.

    Use case ends.

#### Use case: Delete contact

**MSS**

1. User requests to list contacts.
2. 0rb1t shows a list of contacts.
3. User requests to delete a specific contact in the list.
4. 0rb1t asks for confirmation.
5. User inputs `yes`.
6. 0rb1t deletes the contact.

   Use case ends.

**Extensions**

- 2a. The list is empty.

  Use case ends.

- 3a. The given index is invalid.

  - 3a1. 0rb1t shows an error message.

    Use case resumes from step 2.

- 5a. The user inputs `no`.

  - 5a1. 0rb1t cancels the deletion.

    Use case ends.

- 5b. The user inputs any input other than `yes` and `no`.

  Use case resumes from step 4.


#### Use case: Filter contacts by criterion

**MSS**

1. User requests to list contacts that fulfill a given criterion.
2. 0rb1t shows a list of all contacts that fulfill the given criterion.

   Use case ends.

**Extensions**

- 1a. The given filter is invalid.

  - 1a1. 0rb1t shows an error message.

    Use case ends.

- 2a. No contacts fulfill the given criterion.

  Use case ends.

#### Use case: Sort contacts by criterion

**MSS**

1. User requests to list contacts sorted by a given criterion.
2. 0rb1t shows a list of all contacts in the requested order.

   Use case ends.

**Extensions**

- 1a. The given sort criterion is invalid.

  - 1a1. 0rb1t shows an error message.

    Use case ends.

#### Use case: Undo last action

**MSS**

1. User requests to undo the last action.
2. 0rb1t reverses the last mutating action.

   Use case ends.

**Extensions**

- 2a. There is no action to undo.

  - 2a1. 0rb1t shows an error message.

    Use case ends.

#### Use case: Edit a contact

**MSS**

1. User requests to list contacts.
2. 0rb1t shows a list of contacts.
3. User requests to edit a specific contact in the list.
4. 0rb1t updates the contact.

   Use case ends.

**Extensions**

- 2a. The list is empty.

  Use case ends.

- 3a. The given index is invalid.

  - 3a1. 0rb1t shows an error message.

    Use case resumes from step 2.

- 3b. The edit command contains invalid input.

  - 3b1. 0rb1t shows an error message.

    Use case ends.

- 3c. The edited contact would have the same name as an existing contact.

  - 3c1. 0rb1t displays an error message if a contact with an existing name match (case-sensitive) is added.

    Use case ends.

### Non-Functional Requirements

1. Should work on any mainstream OS as long as it has Java 17 or above installed.
2. Should be able to hold up to 1000 contacts without noticeable sluggishness in typical usage.
3. Should automatically persist contact data after every successful mutating command.
4. Should display an informative error message within 1 second when a command fails due to invalid syntax, invalid indices, or invalid field values.
5. Should complete common commands such as `:list`, `:view`, and `:tags` within 1 second under normal usage conditions.
6. Should be ready for use within 2 seconds after launch under normal usage conditions.
7. Should remain usable at screen resolutions of 1280x720 and above.
8. Should allow the main contact-management workflows to be completed without requiring mouse interaction.
9. The packaged application should not exceed 100 MB as a single distributable JAR file.
10. Should preserve contacts, notes, tags, and starred state across application restarts unless the user explicitly removes them.

### Glossary

- 0rb1t: The contact-management application developed in this project.
- Contact: A single person entry managed by 0rb1t.
- Tag: A label attached to a contact for categorization, such as a role, project, or context.
- TagsRegistry: A derived model structure that tracks all tags currently in use and their usage counts.
- Note: A short piece of text attached to a contact to preserve lightweight context.
- Starred contact: A contact marked as important or frequently used.
- Index: The positive integer shown beside a contact in the currently displayed list and used by commands such as `:view`, `:edit`, and `:delete`.
- Filtered list: The currently visible subset of contacts after applying list filters.
- Sorted list: The ordering applied to the currently visible contacts after applying sort criteria.
- Mutating command: A command that changes application state, such as `:add`, `:edit`, `:delete`, `:note`, `:star`, `:unstar`, or `:clear`.

---

## **Appendix: Instructions for Manual Testing**

Given below are instructions to test the app manually. These instructions only provide a starting point for testers to
work from; testers are expected to do more exploratory testing.

### Launch and shutdown

**Initial launch**

Prerequisites: Download the JAR file and copy it into an empty folder.

Test case: Run `java -jar <jar-file-name>.jar`.  
Expected: The GUI appears with a set of sample contacts. The initial window size may not be optimal.

**Saving window preferences**

Prerequisites: The app has been launched successfully at least once.

Test case: Resize the window, move it to a different location, then close the app. Launch it again.  
Expected: The most recent window size and location are retained.

### Starring and unstarring contacts

Prerequisites: There is at least one contact in the currently displayed list.

Test case: `:star 1`  
Expected: The first displayed contact is starred if it is not starred.

Test case: `:unstar 1`  
Expected: The first displayed contact is unstarred if it is starred.

Test case: `:star 0`  
Expected: No contact is starred. An error message is shown.

### Listing, filtering, and sorting contacts

**Listing all contacts**

Test case: `:list`  
Expected: All contacts are shown.

**Filtering by a single criterion**

Test case: `:list t/friends`  
Expected: Only contacts tagged `friends` are shown.

Test case: `:list n/Alex`  
Expected: Only contacts whose names contain `Alex` are shown.

**Filtering by multiple criteria**

Test case: `:list t/friends n/Bernice`  
Expected: Only contacts tagged `friends` whose names contain `Bernice` are shown.

**Filtering with multiple values for the same criterion**

Test case: `:list n/Alex n/Roy`  
Expected: Only contacts whose names contain `Alex` or `Roy` are shown.

**Sorting contacts**

Test case: `:list s/+n`  
Expected: Contacts are listed in ascending name order.

Test case: `:list s/-p`  
Expected: Contacts are ordered first by increasing length of the phone number, and then by lexicographical order in descending order.

Test case: `:list s/* s/+n`\
Expected: Starred contacts are pinned above non-starred contacts, and each group is ordered by name.

**Invalid list command**

Test case: `:list hello`  
Expected: No contacts are changed. An error message is shown.

### Viewing a contact

Prerequisites: There is at least one contact in the currently displayed list.

Test case: `:view 1`  
Expected: The first contact in the displayed list is selected and their name, phone, email, address and notes appear in the details panel.

Test case: `:view 0`  
Expected: The command fails, an error message is shown, and the current selection remains unchanged.

### Adding notes

Prerequisites: There is at least one contact in the currently displayed list.

Test case: `:note 1 First met during CS2103T.`  
Expected: A new note is appended to the first displayed contact.

Test case: `:note 1`  
Expected: No note is added. An error message is shown.

### Editing a contact

Prerequisites: There is at least one contact in the currently displayed list.

Test case: `:edit 1 p/91234567`  
Expected: The first displayed contact's phone number is updated.

Test case: `:edit 1 t/backend`  
Expected: The `backend` tag is added without removing the contact's existing tags.

Test case: `:edit 1 dt/backend`  
Expected: The `backend` tag is removed from the contact if it exists.

Test case: `:edit 1 dt/`  
Expected: All tags are removed from the contact.

Test case: `:edit 0 n/Amy`  
Expected: No contact is edited. An error message is shown.

### Listing all tags

Test case: `:tags`  
Expected: All tags currently in use are shown once each in alphabetical order.

### Deleting a contact

Prerequisites: List all contacts using `:list`. There are multiple contacts in the displayed list.

Test case: `:delete 1`  
Expected: A preview of the first displayed contact is shown in the result box and the app asks for confirmation.

Test case: After `:delete 1`, enter `no`.  
Expected: The deletion is cancelled.

Test case: After `:delete 1`, enter any other input, such as `maybe`.  
Expected: The contact is not deleted. The confirmation remains pending and a reminder is shown.

Test case: After `:delete 1`, enter `yes`.  
Expected: The contact is deleted from the list.

Test case: `:delete 0`  
Expected: No contact is deleted. An error message is shown.

### Clearing all contacts

Prerequisites: There is at least one contact in the address book.

Test case: `:clear`  
Expected: A confirmation prompt is shown.

Test case: After `:clear`, enter `no`.  
Expected: The clear action is cancelled.

Test case: After `:clear`, enter any other input, such as `maybe`.  
Expected: The address book is not cleared. The confirmation remains pending and a reminder is shown.

Test case: After `:clear`, enter `yes`.  
Expected: All contacts are removed.

### Undoing the last action

Prerequisites: A successful mutating command has just been executed in the same session.

Test case: `:undo`  
Expected: The most recent mutating action is reversed.

### Saving data

**Saving after a change**

Prerequisites: The app has been launched successfully.

Test case: Add, edit, note, star, or delete a contact, then close and relaunch the app.  
Expected: The last successful change is still present after relaunch.

**Missing data file**

Prerequisites: Close the app and delete the existing address book data file.

Test case: Launch the app again.  
Expected: The app starts successfully with sample data.

**Corrupted data file**

Prerequisites: Close the app and manually corrupt the address book data file contents.

Test case: Launch the app again.  
Expected: The app starts without crashing and falls back to an empty address book if the data cannot be loaded.

## **Appendix: Effort**

This project was moderately to highly challenging. Compared with AB3, 0rb1t required more effort because it goes beyond a single contact-management flow and supports richer contact data, multiple derived states, and more interactive workflows. In AB3, most features revolve around one basic `Person` entity and straightforward CRUD operations. In 0rb1t, we had to extend that foundation to support notes, tags, starred contacts, filtered and sorted views, undo, and confirmation-based destructive commands while keeping the UI, logic, model, and storage layers synchronized.

The most difficult parts were maintaining consistency across the model and derived state, especially when commands could affect several pieces of data at once. For example, editing a contact could change fields, tags, filtered results, the selected contact, and undo history. Confirmation handling for `:delete` and `:clear` also added complexity because the application had to preserve a pending action across user inputs without breaking normal command flow. Another challenge was keeping the UI responsive and predictable while supporting both selection-driven updates and command-driven focus changes.

The amount of effort required was significant because many features had to be adapted across several layers instead of being implemented in isolation. We spent time designing and testing command behavior, updating parsers, making the model maintain derived information correctly, and ensuring storage could persist the extended contact structure. Debugging also took considerable effort because small changes in one component could affect filtering, sorting, undo, or confirmation behavior elsewhere.

One area where reuse saved effort was the AB3 codebase itself. We reused the overall application structure, including the high-level separation into `UI`, `Logic`, `Model`, and `Storage`, along with the command parsing and JSON persistence foundation. This reduced the time needed to build the project skeleton and allowed us to focus our work on adapting the code for 0rb1t’s extra features rather than starting from scratch. Even so, most of the project effort still went into extending and integrating the reused code for our new requirements.

Despite the complexity, we achieved a cohesive contact manager with richer functionality than AB3: keyboard-first workflows, contact notes, tag management, starred contacts, undo support, confirmation prompts for destructive actions, and synchronized list/detail views. The final system preserves the core usability of AB3 while adding substantially more behavior and state management, which made the project both demanding and rewarding.
