package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class TagContainsKeywordPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        TagContainsKeywordPredicate firstPredicate = new TagContainsKeywordPredicate(firstPredicateKeywordList);
        TagContainsKeywordPredicate secondPredicate = new TagContainsKeywordPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        TagContainsKeywordPredicate firstPredicateCopy = new TagContainsKeywordPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_tagContainsKeyword_returnsTrue() {
        // One keyword matching one tag
        TagContainsKeywordPredicate predicate =
                new TagContainsKeywordPredicate(Collections.singletonList("friends"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friends").build()));

        // One keyword matching one of multiple tags
        assertTrue(predicate.test(new PersonBuilder().withTags("friends", "colleagues").build()));

        // Multiple keywords, one matching (OR relationship)
        predicate = new TagContainsKeywordPredicate(Arrays.asList("friends", "family"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friends").build()));

        // Multiple keywords, multiple matching
        predicate = new TagContainsKeywordPredicate(Arrays.asList("friends", "colleagues"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friends", "colleagues").build()));

        // Case-insensitive matching
        predicate = new TagContainsKeywordPredicate(Collections.singletonList("FRIENDS"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friends").build()));

        // Mixed-case keyword
        predicate = new TagContainsKeywordPredicate(Collections.singletonList("fRiEnDs"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friends").build()));
    }

    @Test
    public void test_tagDoesNotContainKeyword_returnsFalse() {
        // Zero keywords
        TagContainsKeywordPredicate predicate = new TagContainsKeywordPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withTags("friends").build()));

        // Non-matching keyword
        predicate = new TagContainsKeywordPredicate(Collections.singletonList("family"));
        assertFalse(predicate.test(new PersonBuilder().withTags("friends").build()));

        // Person has no tags
        predicate = new TagContainsKeywordPredicate(Collections.singletonList("friends"));
        assertFalse(predicate.test(new PersonBuilder().build()));

        // Keyword matches name but not tag
        predicate = new TagContainsKeywordPredicate(Collections.singletonList("Alice"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withTags("friends").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        TagContainsKeywordPredicate predicate = new TagContainsKeywordPredicate(keywords);

        String expected = TagContainsKeywordPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
