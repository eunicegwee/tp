package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SORT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.AddressContainsKeywordPredicate;
import seedu.address.model.person.EmailContainsKeywordPredicate;
import seedu.address.model.person.ListCommandPredicate;
import seedu.address.model.person.NameContainsSubstringPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonComparator;
import seedu.address.model.person.PersonComparator.SortCriteria;
import seedu.address.model.person.PersonComparator.SortField;
import seedu.address.model.person.PersonComparator.SortOrder;
import seedu.address.model.person.PhoneContainsKeywordPredicate;
import seedu.address.model.person.TagContainsKeywordPredicate;

/**
 * Parses input arguments and creates a new ListCommand object.
 */
public class ListCommandParser implements Parser<ListCommand> {


    /**
     * Parses the given {@code String} of arguments in the context of the ListCommand
     * and returns a ListCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public ListCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new ListCommand();
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_TAG, PREFIX_EMAIL, PREFIX_PHONE, PREFIX_ADDRESS, PREFIX_NAME, PREFIX_SORT);
        List<String> tagValues = argMultimap.getAllValues(PREFIX_TAG);
        List<String> emailValues = argMultimap.getAllValues(PREFIX_EMAIL);
        List<String> phoneValues = argMultimap.getAllValues(PREFIX_PHONE);
        List<String> addressValues = argMultimap.getAllValues(PREFIX_ADDRESS);
        List<String> nameValues = argMultimap.getAllValues(PREFIX_NAME);
        List<String> sortValues = argMultimap.getAllValues(PREFIX_SORT);

        // Filter out empty/blank values to prevent runtime errors and incorrect matches
        tagValues.removeIf(String::isBlank);
        emailValues.removeIf(String::isBlank);
        phoneValues.removeIf(String::isBlank);
        addressValues.removeIf(String::isBlank);
        nameValues.removeIf(String::isBlank);
        sortValues.removeIf(String::isBlank);

        boolean noFilters = tagValues.isEmpty() && emailValues.isEmpty() && phoneValues.isEmpty()
                && addressValues.isEmpty() && nameValues.isEmpty();

        if ((noFilters && sortValues.isEmpty()) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        TagContainsKeywordPredicate tagPredicate = tagValues.isEmpty()
                ? null
                : new TagContainsKeywordPredicate(tagValues);

        EmailContainsKeywordPredicate emailPredicate = emailValues.isEmpty()
                ? null
                : new EmailContainsKeywordPredicate(emailValues);

        PhoneContainsKeywordPredicate phonePredicate = phoneValues.isEmpty()
                ? null
                : new PhoneContainsKeywordPredicate(phoneValues);

        AddressContainsKeywordPredicate addressPredicate = addressValues.isEmpty()
                ? null
                : new AddressContainsKeywordPredicate(addressValues);

        NameContainsSubstringPredicate namePredicate = nameValues.isEmpty()
                ? null
                : new NameContainsSubstringPredicate(nameValues);

        ListCommandPredicate predicate = new ListCommandPredicate(
                tagPredicate, emailPredicate, phonePredicate, addressPredicate, namePredicate);

        List<SortCriteria> sortCriteriaList = new ArrayList<>();
        if (!sortValues.isEmpty()) {
            boolean hasFavorite = false;
            for (String sortArg : sortValues) {
                SortCriteria criteria = parseSortArg(sortArg);
                if (criteria.field == SortField.FAVORITE) {
                    if (hasFavorite) {
                        continue; // Skip duplicate favorite declarations
                    }
                    hasFavorite = true;
                }
                sortCriteriaList.add(criteria);
            }

            if (hasFavorite) {
                // Ensure favorite sort is always the first criteria
                sortCriteriaList.removeIf(c -> c.field == SortField.FAVORITE);
                sortCriteriaList.add(0, new SortCriteria(SortField.FAVORITE, SortOrder.DESCENDING));
            }
        }
        
        Comparator<Person> comparator = sortCriteriaList.isEmpty()
                ? null
                : new PersonComparator(sortCriteriaList);

        return new ListCommand(predicate, comparator);
    }

    private SortCriteria parseSortArg(String sortArg) throws ParseException {
        if ("*".equals(sortArg)) {
            return new SortCriteria(SortField.FAVORITE, SortOrder.DESCENDING);
        }

        if (sortArg.length() < 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        char orderChar = sortArg.charAt(0);
        String fieldChar = sortArg.substring(1).trim();

        if (orderChar != '+' && orderChar != '-') {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        SortOrder order = (orderChar == '+') ? SortOrder.ASCENDING : SortOrder.DESCENDING;
        SortField field;

        switch (fieldChar) {
        case "n":
            field = SortField.NAME;
            break;
        case "p":
            field = SortField.PHONE;
            break;
        default:
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        return new SortCriteria(field, order);
    }
}
