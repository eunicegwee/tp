package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.EmailContainsKeywordPredicate;
import seedu.address.model.person.ListCommandPredicate;
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

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TAG, PREFIX_EMAIL, PREFIX_PHONE);
        List<String> tagValues = argMultimap.getAllValues(PREFIX_TAG);
        List<String> emailValues = argMultimap.getAllValues(PREFIX_EMAIL);
        List<String> phoneValues = argMultimap.getAllValues(PREFIX_PHONE);

        if ((tagValues.isEmpty() && emailValues.isEmpty() && phoneValues.isEmpty())
                || !argMultimap.getPreamble().isEmpty()) {
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

        return new ListCommand(new ListCommandPredicate(tagPredicate, emailPredicate, phonePredicate));
    }
}
