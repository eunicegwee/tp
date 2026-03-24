package seedu.address.model.person;

import java.util.Objects;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person} matches all of the non-null field predicates (tags, names, emails, phone numbers, and
 * addresses). Each individual field predicate may match any of its own keywords, but this combined predicate requires
 * every non-null field predicate to pass.
 */
public class ListCommandPredicate implements Predicate<Person> {
    private final TagContainsKeywordPredicate tagPredicate;
    private final EmailContainsKeywordPredicate emailPredicate;
    private final PhoneContainsKeywordPredicate phonePredicate;
    private final AddressContainsKeywordPredicate addressPredicate;
    private final NameContainsSubstringPredicate namePredicate;

    /**
     * Creates a ListCommandPredicate checking for all the given predicates.
     */
    public ListCommandPredicate(TagContainsKeywordPredicate tagPredicate,
                                EmailContainsKeywordPredicate emailPredicate,
                                PhoneContainsKeywordPredicate phonePredicate,
                                AddressContainsKeywordPredicate addressPredicate,
                                NameContainsSubstringPredicate namePredicate) {
        this.tagPredicate = tagPredicate;
        this.emailPredicate = emailPredicate;
        this.phonePredicate = phonePredicate;
        this.addressPredicate = addressPredicate;
        this.namePredicate = namePredicate;
    }

    @Override
    public boolean test(Person person) {
        boolean tagMatch = tagPredicate == null || tagPredicate.test(person);
        boolean emailMatch = emailPredicate == null || emailPredicate.test(person);
        boolean phoneMatch = phonePredicate == null || phonePredicate.test(person);
        boolean addressMatch = addressPredicate == null || addressPredicate.test(person);
        boolean nameMatch = namePredicate == null || namePredicate.test(person);
        return tagMatch && emailMatch && phoneMatch && addressMatch && nameMatch;
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
                && Objects.equals(phonePredicate, otherPredicate.phonePredicate)
                && Objects.equals(addressPredicate, otherPredicate.addressPredicate)
                && Objects.equals(namePredicate, otherPredicate.namePredicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("tagPredicate", tagPredicate)
                .add("emailPredicate", emailPredicate)
                .add("phonePredicate", phonePredicate)
                .add("addressPredicate", addressPredicate)
                .add("namePredicate", namePredicate)
                .toString();
    }
}
