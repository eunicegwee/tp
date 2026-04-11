package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {
    public static final int MAX_TAGS = 10;
    public static final String MESSAGE_MAX_TAGS = "Each contact can have at most " + MAX_TAGS + " tags.";

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final NoteList noteList;
    private final boolean isStarred;

    /**
     * Every field must be present and not null.
     * Initializes with an empty NoteList and non-starred state by default.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, phone, email, address, tags, new NoteList(), false);
    }

    /**
     * Every field must be present and not null.
     * Initializes with a non-starred state by default.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, NoteList noteList) {
        this(name, phone, email, address, tags, noteList, false);
    }

    /**
     * Every field must be present and not null.
     * Initializes with an empty NoteList by default.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, boolean isStarred) {
        this(name, phone, email, address, tags, new NoteList(), isStarred);
    }

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags,
            NoteList noteList, boolean isStarred) {
        requireAllNonNull(name, phone, email, address, tags, noteList);
        if (tags.size() > MAX_TAGS) {
            throw new IllegalArgumentException(MESSAGE_MAX_TAGS);
        }
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.noteList = noteList;
        this.isStarred = isStarred;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public Person withStar(boolean isStarred) {
        return new Person(name, phone, email, address, tags, noteList, isStarred);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public List<Note> getListOfNotes() {
        return noteList.getAll();
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags)
                && noteList.equals(otherPerson.noteList)
                && isStarred == otherPerson.isStarred();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, email, address, tags, noteList, isStarred);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("notes", noteList)
                .add("isStarred", isStarred)
                .toString();
    }

    public NoteList appendNote(String note) {
        return noteList.append(note);
    }
}
