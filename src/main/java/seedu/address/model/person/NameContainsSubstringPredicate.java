package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} contains any of the keywords given as a substring.
 */
public class NameContainsSubstringPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public NameContainsSubstringPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        return keywords.stream()
                .anyMatch(keyword -> person.getName().fullName.toLowerCase().contains(keyword.toLowerCase()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsSubstringPredicate)) {
            return false;
        }

        NameContainsSubstringPredicate otherNameContainsSubstringPredicate = (NameContainsSubstringPredicate) other;
        return keywords.equals(otherNameContainsSubstringPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
