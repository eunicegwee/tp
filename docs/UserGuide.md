# 0rb1t User Guide

**0rb1t** is a desktop application designed for developers who prefer keyboard-driven workflows.

- It brings a **Vim-inspired interface** to contact and task management, so you never have to reach for the mouse.
- Built for developers who feel at home in Vim: 0rb1t lets you navigate, edit, and manage with the keybindings you already know.

The app is **written in Object-Oriented Programming (OOP) fashion**, based on a ~6 KLoC codebase with solid user and developer documentation.

For detailed documentation, see the [**0rb1t Product Website**](https://ay2526s2-cs2103t-t15-4.github.io/tp/).

This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org/).

# Table of Contents

- [Command List](#command-list)
    - [Accessing Command History](#accessing-command-history)
    - [Adding Contacts](#adding-contacts)
    - [Adding Notes to Contacts](#adding-notes-to-contacts)
    - [Clearing 0rb1t](#clearing-0rb1t)
    - [Deleting Contacts](#deleting-contacts)
    - [Editing Contacts](#editing-contacts)
    - [Exiting 0rb1t](#exiting-0rb1t)
    - [Favouriting Contacts](#favouriting-contacts)
    - [Finding Contacts](#finding-contacts)
    - [Accessing Help in 0rb1t](#accessing-help-in-0rb1t)
    - [Listing Contacts](#listing-contacts)
    - [Sorting Contacts](#sorting-contacts)
    - [Listing Tags](#listing-tags)
    - [Viewing Contacts](#viewing-contacts)
- [Storage](#storage)
    - [Saving the data](#saving-the-data)
    - [Editing the data file](#editing-the-data-file)
- [Tips and Examples](#tips-and-examples)
- [Frequently Asked Questions (FAQ)](#frequently-asked-questions-faq)
- [Known Issues](#known-issues)
- [Command Summary](#command-summary)

## Quick Start

Follow these steps to get 0rb1t running on your computer:

1. **Ensure you have Java 17 or above installed.**
    - **Mac users:** Make sure you have the exact JDK version required.
2. **Download the latest `.jar` file** from [here](https://github.com/AY2526S2-CS2103T-T15-4/tp/releases/tag/v1.0.0).
3. **Copy the `.jar` file** to the folder you want to use as the home folder for 0rb1t.
4. **Open a terminal** and navigate to the folder containing the `.jar` file.
5. **Run the application** using the following command:

```
java -jar 0rb1t.jar
```

---

## Command List

### Accessing Command History

To navigate previously used commands, use the `UP` and `DOWN` arrow keys in the command box. `UP` to recall older commands, `DOWN` to recall newer commands.

Note: Navigating past the most recent command clears the command box. Up to 64 commands are stored.

Format: `UP` or `DOWN`

Expected: The command box will display the selected command from the history, depending on whether you press the `UP` or `DOWN` key.

### Adding Contacts

To add a contact, simply type `:add` followed by the details of the contact you wish to add. The parameters required are:

- The contact’s name, typed after `n/`.
- The contact’s phone number, typed after `p/`.
- The contact’s email, typed after `e/`.
- The contact’s house address, typed after `a/`.
- Any tags you wish to identify the contact with, typed after `t/`, and each additional tag after the first one separated by `t/`.

Note: All parameters are required except for tags. A contact can have any number of tags (including 0).

Format: `:add n/NAME p/PHONE e/EMAIL a/ADDRESS [t/TAG]...`

Examples:

`:add n/John Doe p/98765432 e/johnd@example.com a/John Street, Block 123, #01-01`

`:add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

Expected: The new contact will be added to 0rb1t, and it can be viewed at the sidebar.

### Adding Notes to Contacts

To add a note to a contact, type `:note` followed by the index of the contact and the note you wish to add.

Note: Notes are appended (not overwritten) to the existing ones.

Format: `:note <INDEX> note`

Examples: 

`:note 1 This is an important contact.`

`:note 5 Don't forget to follow up with him/her.`

Expected: The note will be added/appended to the contact in 0rb1t.

### Clearing 0rb1t

To clear the entire 0rb1t, type `:clear`. 0rb1t will ask you whether you wish to clear the entire 0rb1t (in case you mistyped). Typing `yes` will clear 0rb1t, while typing anything else will cancel the command.

Format: `:clear` + `yes`

Example:

`:clear`

Are you sure you want to clear the entire 0rb1t?
Type 'yes' to confirm. Any other input will be taken as no.

`yes`

Expected: The entire 0rb1t will be cleared, and the sidebar will be empty.

### Deleting Contacts

To delete a contact, type `:delete` followed by the index of the contact you wish to delete. The index of each individual contact can be found at the sidebar.

Format: `:delete <INDEX>` + `yes`

Example:

`:delete 1`

Are you sure you want to delete this contact?
<Contact details>
Type 'yes' to confirm. Any other input will be taken as no.

`yes`

Expected: The contact corresponding to the entered index will be deleted from 0rb1t. 0rb1t confirms that the chosen contact has been deleted, and shows the details of the contact deleted.

### Editing Contacts

To edit the details of a contact, type `:edit` followed by the index of the contact you wish to edit, 
then the field prefixes of the fields you wish to change, and then the new details. The fields that can be edited are:

- The contact’s name, typed after `n/`.
- The contact’s phone number, typed after `p/`.
- The contact’s email, typed after `e/`.
- The contact’s house address, typed after `a/`.
- Any tags you wish to identify the contact with, typed after `t/`, and each additional tag after the first one separated by `t/`.
- Any tags you wish to remove from the contact, typed after `dt/`, and each additional tag to be removed separated by `dt/`.

Note: If you wish to leave some fields unchanged, you do not have to include them in the `:edit` command.

Format: `:edit <INDEX> n/NAME p/PHONE e/EMAIL a/ADDRESS [t/TAG] [dt/TAG] ...`

Examples:

`:edit 2 n/Adam Wong a/NUS PGP`

`:edit 5 p/13572468 t/school t/friend`

`:edit 1 e/jane_doe@example.com dt/school`

Expected: 0rb1t will display a confirmation message and show the updated details of the contact.

### Exiting 0rb1t

To exit the application, type `:exit` and the application will automatically close.

Format: `:exit`

Expected: 0rb1t will close. No goodbye message is shown.

### Favouriting Contacts

To make a contact your favourite, type `:favourite` followed by the index of the contact.
To un-favourite, type `:unfavourite` followed by the index of the contact.

Note: Favourites are indicated by a star next to the contact name. Favourites are persisted and stored in the contact's data.

Format: `:favourite <INDEX>` or `:unfavourite <INDEX>`

Examples:

`:favourite 2`

`:unfavourite 7`

Expected: The contact at the given index will have a star icon next to their name, indicating that they are a favourite.

### Finding Contacts

To find a particular contact, type `:list` followed by the field you wish to use. The fields you can use are:

- The contact’s name, typed after `n/`.
- The contact’s phone number, typed after `p/`.
- The contact’s email, typed after `e/`.
- The contact’s house address, typed after `a/`.

Note: Multiple filters can be combined with `and`, and multiple values for a field can be used with `or`.

Format: `:list <FIELD_PREFIX + KEYWORD>`

Examples:

`:list n/John and p/12345678`

`:list n/Bernice or n/Sally`

Expected: 0rb1t will display a list of contacts in the sidebar whose details match the search criteria.

### Accessing Help in 0rb1t

To find help content for using this application, type `:help`.

Format: `:help`

Expected: 0rb1t will open a separate help window, showing the link to the User Guide of 0rb1t.

### Listing Contacts

To list all contacts stored in 0rb1t, type `:list` and all contacts will appear on the sidebar.

Format: `:list <TAG>`

Examples:

`:list`

`:list t/friend`

`:list t/friend t/colleague`

Expected: 0rb1t will state that it listed all contacts, and the entire list will be made available in the sidebar.

If tags are added, all contacts with the relevant tags will be made available in the sidebar.

### Sorting Contacts

To sort contact by specific fields, type `:list s/` followed by the field you wish to sort by. Use `+`or `-` to sort in either ascending or descending order respectively.
Typing `s/*` ensures favourites are always at the top. The fields you can sort by are:

- The contact’s name, typed after `n/`.
- The contact’s phone number, typed after `p/`.
- The contact’s email, typed after `e/`.
- The contact’s house address, typed after `a/`.

Format: `:list s/<FIELD_PREFIX + SIGN>`

Examples:

`:list s/+n`

`:list s/* s/-a`

Expected: The list of contacts will be sorted based on the paramter and in the order specified. If s/* was used, favourited contacts will be pinned at the top.

### Listing Tags

To display all the tags that you have added in 0rb1t, type `:tags` and all the tags you have added will be shown, with each tag separated by a comma.

Note: Tags are displayed in alphabetical order, and each tag is shown only once even if multiple contacts have the same tag. Tags are case-sensitive; for example, “friend” and “Friend” are treated as two distinct tags.

Format: `:tags`

Expected: 0rb1t will display all the tags that have been added to 0rb1t.

### Viewing Contacts

To view the details of a contact, type `:view` followed by the index of the contact you wish to view.

Format: `:view <INDEX>`

Examples:

`:view 2`

`:view 10`

Expected: 0rb1t will state which contact is being shown by stating the name of the contact. The corresponding contact will be highlighted in the sidebar, and the contact details can be viewed in the main window.

## Storage

### Saving the data

All data in 0rb1t is saved to the hard disk automatically after any command that changes the data. There is no need to save manually.

### Editing the data file

All data is saved automatically as a JSON file [JAR file location]/data/0rb1t.json.
Advanced users are welcome to update data directly by editing that data file.

Caution: If your changes to the data file make its format invalid, 0rb1t will discard all data and start with an empty data file at the next run. Hence, it is recommended to take a backup of the file before editing it.
Furthermore, certain edits can cause 0rb1t to behave in unexpected ways (e.g., if a value entered is outside the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.

## Tips and Examples

- Use `:list n/<NAME>` to narrow down the right contact before any other action to avoid changing/deleting the wrong contact.

- Example:
`:list n/adam`
`:edit 1 p/12345678`
`:delete 1`

## Frequently Asked Questions (FAQ)

**Q**: How do I transfer my data to another computer?

**A**: Install the app on the other computer and overwrite the empty data file it creates with the file that contains the data of your previous AddressBook home folder.

## Known Issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimise the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimised, and no new Help Window will appear. The remedy is to manually restore the minimised Help Window.

## Command Summary

| Command        | Format                                             | Description                                            | Example                                                                                              |
|----------------|----------------------------------------------------|--------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| Access History | `UP` or `DOWN`                                     | Navigates to previously used commands.                 | `UP` or `DOWN`                                                                                       |
| Add Contact    | `:add n/NAME p/PHONE e/EMAIL a/ADDRESS [t/TAG]...` | Adds a contact to 0rb1t.                               | `:add n/John Doe p/98765432 e/johnd@example.com a/311, Clementi Ave 2, #02-25 t/friends t/owesMoney` |
| Add Note       | `:note <INDEX> note`                               | Adds a note to the contact.                            | `:note 2 This is an important contact.`                                                              |
| Clear          | `:clear` + `yes`                                   | Clears the entire 0rb1t.                               | `:clear`<br/>`...`<br/>`yes`                                                                         |
| Delete         | `:delete <INDEX>` + `yes`                          | Deletes a contact from 0rb1t.                          | `:delete 2`<br/>`...`<br/>`yes`                                                                      |
| Edit           | `:edit <INDEX> ...`                                | Edits a contact’s details in 0rb1t.                    | `:edit 3`                                                                                            |
| Exit           | `:exit`                                            | Exits 0rb1t.                                           | `:exit`                                                                                              |
| Favourite      | `:favourite` or `:unfavourite`                     | Favourites/unfavourites a contact.                     | `:favourite 5`<br/>`unfavourite 8`                                                                   |
| Help           | `:help`                                            | Opens the help page.                                   | `:help`                                                                                              |
| List Contacts  | `:list` or `:list <TAG>`                           | Lists all contacts stored in 0rb1t.                    | `:list`<br/>`:list t/friend`                                                                         |
| Sorting        | `list s/<FIELD_PREFIX + SIGN>`                     | Sorts all contacts based on the field and the order.   | `:list s/+n`                                                                                         |
| List Tags      | `:tags`                                            | Lists all the tags used in 0rb1t.                      | `:tags`                                                                                              |
| View           | `:view <INDEX>`                                    | Views a contact’s details in 0rb1t based on the index. | `:view 4`                                                                                            |
