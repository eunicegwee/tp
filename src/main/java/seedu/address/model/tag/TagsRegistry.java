package seedu.address.model.tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.person.Person;

/**
 * Keeps track of all tags in the address book and their counts.
 * This is derived data and should be rebuilt from the AddressBook on startup.
 */
public class TagsRegistry {

    private final Map<Tag, Integer> tagCounts = new HashMap<>();

    /**
     * Rebuilds the registry from the given persons.
     */
    public void initialize(Iterable<Person> persons) {
        tagCounts.clear();
        for (Person person : persons) {
            addTags(person.getTags());
        }
    }

    /**
     * Call when a new person is added.
     */
    public void addPerson(Person person) {
        addTags(person.getTags());
    }

    /**
     * Call when a person is removed.
     */
    public void removePerson(Person person) {
        removeTags(person.getTags());
    }

    /**
     * Call when a person is edited.
     */
    public void updatePerson(Person oldPerson, Person newPerson) {
        removeTags(oldPerson.getTags());
        addTags(newPerson.getTags());
    }

    /**
     * Returns all unique tags currently in use.
     */
    public Set<Tag> getAllTags() {
        return Set.copyOf(tagCounts.keySet());
    }

    /**
     * Internal helper to add a set of tags.
     */
    private void addTags(Set<Tag> tags) {
        for (Tag tag : tags) {
            tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
        }
    }

    /**
     * Clears all tags from the registry.
     */
    public void clear() {
        tagCounts.clear();
    }

    /**
     * Internal helper to remove a set of tags.
     */
    private void removeTags(Set<Tag> tags) {
        for (Tag tag : tags) {
            int count = tagCounts.getOrDefault(tag, 0);
            if (count <= 1) {
                tagCounts.remove(tag);
            } else {
                tagCounts.put(tag, count - 1);
            }
        }
    }

    /**
     * Returns the number of persons associated with the given {@code tag}.
     */
    public int getCount(Tag tag) {
        return tagCounts.getOrDefault(tag, 0);
    }

    /**
     * Returns true if the TagsRegistry currently has no tags.
     */
    public boolean isEmpty() {
        return tagCounts.isEmpty();
    }

    /**
     * Returns a formatted string of all tags, sorted alphabetically.
     */
    public String getFormattedTags() {
        if (tagCounts.isEmpty()) {
            return "";
        }

        return tagCounts.keySet().stream()
                .map(tag -> tag.tagName)
                .sorted()
                .collect(Collectors.joining(", "));
    }
}
