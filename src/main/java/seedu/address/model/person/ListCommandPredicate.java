package seedu.address.model.person;

import java.util.Objects;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person} matches any of the given keywords for tags, emails, and phone numbers.
 */
public class ListCommandPredicate implements Predicate<Person> {
    private final TagContainsKeywordPredicate tagPredicate;
    private final EmailContainsKeywordPredicate emailPredicate;
    private final PhoneContainsKeywordPredicate phonePredicate;

    public ListCommandPredicate(TagContainsKeywordPredicate tagPredicate,
                                EmailContainsKeywordPredicate emailPredicate,
                                PhoneContainsKeywordPredicate phonePredicate) {
        this.tagPredicate = tagPredicate;
        this.emailPredicate = emailPredicate;
        this.phonePredicate = phonePredicate;
    }

    @Override
    public boolean test(Person person) {
        boolean tagMatch = tagPredicate == null || tagPredicate.test(person);
        boolean emailMatch = emailPredicate == null || emailPredicate.test(person);
        boolean phoneMatch = phonePredicate == null || phonePredicate.test(person);
        return tagMatch && emailMatch && phoneMatch;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ListCommandPredicate)) {
            return false;
        }

        ListCommandPredicate otherPredicate = (ListCommandPredicate) other;
        return Objects.equals(tagPredicate, otherPredicate.tagPredicate)
                && Objects.equals(emailPredicate, otherPredicate.emailPredicate)
                && Objects.equals(phonePredicate, otherPredicate.phonePredicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("tagPredicate", tagPredicate)
                .add("emailPredicate", emailPredicate)
                .add("phonePredicate", phonePredicate)
                .toString();
    }
}
