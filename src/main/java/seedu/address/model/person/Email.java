package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's email in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 */
public class Email {

    private static final String SPECIAL_CHARACTERS = "+_.-";
    private static final int LOCAL_PART_MAX_LENGTH = 64;
    private static final int DOMAIN_MAX_LENGTH = 253;
    private static final int EMAIL_MAX_LENGTH = 254;
    public static final String MESSAGE_CONSTRAINTS = "Emails should be of the format local-part@domain "
            + "and adhere to the following constraints:\n"
            + "1. The local-part should only contain alphanumeric characters and these special characters, excluding "
            + "the parentheses, (" + SPECIAL_CHARACTERS + ").\n The local-part may not start or end with any special "
            + "characters and has a length limit of " + LOCAL_PART_MAX_LENGTH + " characters.\n"
            + "2. This is followed by a '@' and then a domain name. The domain name is made up of domain labels "
            + "separated by periods and has a length limit of " + DOMAIN_MAX_LENGTH + " characters.\n"
            + "3. The entire email has a length limit of " + EMAIL_MAX_LENGTH + " characters.\n"
            + "The domain name must:\n"
            + "    - end with a domain label at least 2 characters long\n"
            + "    - have each domain label start and end with alphanumeric characters\n"
            + "    - have each domain label consist of alphanumeric characters, separated only by hyphens, if any.";
    private static final String LOCAL_PART_REGEX = "[A-Za-z0-9]+(?:[" + SPECIAL_CHARACTERS + "][A-Za-z0-9]+)*";
    private static final String DOMAIN_LABEL_REGEX = "[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*";
    public static final String VALIDATION_REGEX = LOCAL_PART_REGEX + "@" + DOMAIN_LABEL_REGEX
            + "(?:\\." + DOMAIN_LABEL_REGEX + ")+";

    public final String value;

    /**
     * Constructs an {@code Email}.
     *
     * @param email A valid email address.
     */
    public Email(String email) {
        requireNonNull(email);
        checkArgument(isValidEmail(email), MESSAGE_CONSTRAINTS);
        value = email;
    }

    /**
     * Returns if a given string is a valid email.
     */
    public static boolean isValidEmail(String test) {
        requireNonNull(test);

        if (!test.matches(VALIDATION_REGEX)) {
            return false;
        }

        int atIndex = test.indexOf('@');
        String localPart = test.substring(0, atIndex);
        String domainPart = test.substring(atIndex + 1);

        if (localPart.length() > LOCAL_PART_MAX_LENGTH || domainPart.length() > DOMAIN_MAX_LENGTH) {
            return false;
        }

        if (test.length() > EMAIL_MAX_LENGTH) {
            return false;
        }

        String[] domainLabels = domainPart.split("\\.");
        return domainLabels[domainLabels.length - 1].length() >= 2;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Email otherEmail)) {
            return false;
        }

        return value.equals(otherEmail.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
