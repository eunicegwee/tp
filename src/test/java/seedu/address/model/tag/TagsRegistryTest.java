package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

public class TagsRegistryTest {

    private TagsRegistry tagsRegistry;

    @BeforeEach
    public void setUp() {
        tagsRegistry = new TagsRegistry();

        Set<Tag> tags1 = new HashSet<>();
        tags1.add(new Tag("neighbours"));
        Person p1 = new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"),
                new Email("charlotte@example.com"), new Address("Blk 11 Ang Mo Kio Street 74, #11-04"), tags1);

        Set<Tag> tags2 = new HashSet<>();
        tags2.add(new Tag("family"));
        Person p2 = new Person(new Name("David"), new Phone("91031282"),
                new Email("lidavid@example.com"), new Address("Blk 436 Serangoon Gardens Street 26, #16-43"), tags2);

        Set<Tag> tags3 = new HashSet<>();
        tags3.add(new Tag("classmates"));
        Person p3 = new Person(new Name("Irfan Ibrahim"), new Phone("92492021"),
                new Email("irfan@example.com"), new Address("Blk 47 Tampines Street 20, #17-35"), tags3);

        Set<Tag> tags4 = new HashSet<>();
        tags4.add(new Tag("colleagues"));
        Person p4 = new Person(new Name("Roy Balakrishnan"), new Phone("92624417"),
                new Email("royb@example.com"), new Address("Blk 45 Aljunied Street 85, #11-31"), tags4);

        tagsRegistry.initialize(Set.of(p1, p2, p3, p4));
    }

    @Test
    public void initialize_countsAllTagsCorrectly() {
        Set<Tag> allTags = tagsRegistry.getAllTags();

        assertTrue(allTags.contains(new Tag("neighbours")));
        assertTrue(allTags.contains(new Tag("family")));
        assertTrue(allTags.contains(new Tag("classmates")));
        assertTrue(allTags.contains(new Tag("colleagues")));

        assertEquals(1, tagsRegistry.getCount(new Tag("neighbours")));
        assertEquals(1, tagsRegistry.getCount(new Tag("family")));
        assertEquals(1, tagsRegistry.getCount(new Tag("classmates")));
        assertEquals(1, tagsRegistry.getCount(new Tag("colleagues")));
    }

    @Test
    public void addPerson_incrementsTagCounts() {
        Set<Tag> newTags = new HashSet<>();
        newTags.add(new Tag("family"));
        newTags.add(new Tag("friend"));

        Person newPerson = new Person(new Name("Alice"), new Phone("12345678"),
                new Email("alice@example.com"), new Address("Blk 123 Example St"), newTags);

        tagsRegistry.addPerson(newPerson);

        assertEquals(2, tagsRegistry.getCount(new Tag("family")));
        assertEquals(1, tagsRegistry.getCount(new Tag("friend")));
    }

    @Test
    public void removePerson_decrementsTagCounts() {
        Person removePerson = new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"),
                new Email("charlotte@example.com"), new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                Set.of(new Tag("neighbours")));

        tagsRegistry.removePerson(removePerson);

        assertEquals(0, tagsRegistry.getCount(new Tag("neighbours")));
    }
}
