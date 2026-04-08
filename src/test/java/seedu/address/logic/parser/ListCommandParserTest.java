package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;
import seedu.address.model.person.AddressContainsKeywordPredicate;
import seedu.address.model.person.EmailContainsKeywordPredicate;
import seedu.address.model.person.ListCommandPredicate;
import seedu.address.model.person.NameContainsSubstringPredicate;
import seedu.address.model.person.PersonComparator;
import seedu.address.model.person.PersonComparator.SortCriteria;
import seedu.address.model.person.PersonComparator.SortField;
import seedu.address.model.person.PersonComparator.SortOrder;
import seedu.address.model.person.PhoneContainsKeywordPredicate;
import seedu.address.model.person.TagContainsKeywordPredicate;

public class ListCommandParserTest {

    private ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_emptyArg_returnsListAllCommand() {
        assertParseSuccess(parser, "", new ListCommand());
        assertParseSuccess(parser, "   ", new ListCommand());
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // no t/ prefix
        assertParseFailure(parser, "friend",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validSingleTag_returnsListCommand() {
        TagContainsKeywordPredicate tagPredicate =
                new TagContainsKeywordPredicate(Collections.singletonList("friends"));
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                tagPredicate, null, null, null, null));
        assertParseSuccess(parser, " t/friends", expectedCommand);
    }

    @Test
    public void parse_validMultipleTags_returnsListCommand() {
        TagContainsKeywordPredicate tagPredicate =
                new TagContainsKeywordPredicate(Arrays.asList("friends", "owesMoney"));
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                tagPredicate, null, null, null, null));
        assertParseSuccess(parser, " t/friends t/owesMoney", expectedCommand);
    }

    @Test
    public void parse_validSingleEmail_returnsListCommand() {
        EmailContainsKeywordPredicate emailPredicate =
                new EmailContainsKeywordPredicate(Collections.singletonList("gmail"));
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                null, emailPredicate, null, null, null));
        assertParseSuccess(parser, " e/gmail", expectedCommand);
    }

    @Test
    public void parse_validSinglePhone_returnsListCommand() {
        PhoneContainsKeywordPredicate phonePredicate =
                new PhoneContainsKeywordPredicate(Collections.singletonList("9123"));
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                null, null, phonePredicate, null, null));
        assertParseSuccess(parser, " p/9123", expectedCommand);
    }

    @Test
    public void parse_validSingleAddress_returnsListCommand() {
        AddressContainsKeywordPredicate addressPredicate =
                new AddressContainsKeywordPredicate(Collections.singletonList("clementi"));
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                null, null, null, addressPredicate, null));
        assertParseSuccess(parser, " a/clementi", expectedCommand);
    }

    @Test
    public void parse_validSingleName_returnsListCommand() {
        NameContainsSubstringPredicate namePredicate =
                new NameContainsSubstringPredicate(Collections.singletonList("Alice"));
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                null, null, null, null, namePredicate));
        assertParseSuccess(parser, " n/Alice", expectedCommand);
    }

    @Test
    public void parse_validTagAndEmail_returnsListCommand() {
        TagContainsKeywordPredicate tagPredicate =
                new TagContainsKeywordPredicate(Collections.singletonList("friends"));
        EmailContainsKeywordPredicate emailPredicate =
                new EmailContainsKeywordPredicate(Collections.singletonList("gmail"));
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                tagPredicate, emailPredicate, null, null, null));
        assertParseSuccess(parser, " t/friends e/gmail", expectedCommand);
    }

    @Test
    public void parse_validAllFilters_returnsListCommand() {
        TagContainsKeywordPredicate tagPredicate =
                new TagContainsKeywordPredicate(Collections.singletonList("friends"));
        EmailContainsKeywordPredicate emailPredicate =
                new EmailContainsKeywordPredicate(Collections.singletonList("gmail"));
        PhoneContainsKeywordPredicate phonePredicate =
                new PhoneContainsKeywordPredicate(Collections.singletonList("9123"));
        AddressContainsKeywordPredicate addressPredicate =
                new AddressContainsKeywordPredicate(Collections.singletonList("clementi"));
        NameContainsSubstringPredicate namePredicate =
                new NameContainsSubstringPredicate(Collections.singletonList("Alice"));
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                tagPredicate, emailPredicate, phonePredicate, addressPredicate, namePredicate));
        assertParseSuccess(parser, " t/friends e/gmail p/9123 a/clementi n/Alice", expectedCommand);
    }

    @Test
    public void parse_emptyPrefixArgs_throwsParseException() {
        // empty n/
        assertParseFailure(parser, " n/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));

        // empty e/
        assertParseFailure(parser, " e/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));

        // empty p/
        assertParseFailure(parser, " p/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));

        // empty a/
        assertParseFailure(parser, " a/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));

        // empty t/
        assertParseFailure(parser, " t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));

        // all empty
        assertParseFailure(parser, " n/ e/ p/ a/ t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_mixedEmptyAndValidArgs_success() {
        // mixed n/
        NameContainsSubstringPredicate namePredicate =
                new NameContainsSubstringPredicate(Collections.singletonList("Alice"));
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                null, null, null, null, namePredicate));
        assertParseSuccess(parser, " n/Alice n/", expectedCommand);
    }

    @Test
    public void parse_validSubstringName_returnsListCommand() {
        NameContainsSubstringPredicate namePredicate =
                new NameContainsSubstringPredicate(Collections.singletonList("Ali"));
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                null, null, null, null, namePredicate));
        assertParseSuccess(parser, " n/Ali", expectedCommand);
    }

    @Test
    public void parse_validSortNameAscending_returnsListCommand() {
        List<SortCriteria> criteria = Collections.singletonList(
                new SortCriteria(SortField.NAME, SortOrder.ASCENDING));
        PersonComparator comparator = new PersonComparator(criteria);
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                null, null, null, null, null), comparator);
        assertParseSuccess(parser, " s/+n", expectedCommand);
    }

    @Test
    public void parse_validSortPhoneDescending_returnsListCommand() {
        List<SortCriteria> criteria = Collections.singletonList(
                new SortCriteria(SortField.PHONE, SortOrder.DESCENDING));
        PersonComparator comparator = new PersonComparator(criteria);
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                null, null, null, null, null), comparator);
        assertParseSuccess(parser, " s/-p", expectedCommand);
    }

    @Test
    public void parse_validSortMultiple_returnsListCommand() {
        List<SortCriteria> criteria = Arrays.asList(
                new SortCriteria(SortField.NAME, SortOrder.ASCENDING),
                new SortCriteria(SortField.PHONE, SortOrder.DESCENDING));
        PersonComparator comparator = new PersonComparator(criteria);
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                null, null, null, null, null), comparator);
        assertParseSuccess(parser, " s/+n s/-p", expectedCommand);
    }

    @Test
    public void parse_validSortFavorites_returnsListCommand() {
        List<SortCriteria> criteria = Collections.singletonList(
                new SortCriteria(SortField.STARRED, SortOrder.DESCENDING));
        PersonComparator comparator = new PersonComparator(criteria);
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                null, null, null, null, null), comparator);
        assertParseSuccess(parser, " s/*", expectedCommand);
    }

    @Test
    public void parse_validSortFavoritesPriority_returnsListCommand() {
        List<SortCriteria> criteria = Arrays.asList(
                new SortCriteria(SortField.STARRED, SortOrder.DESCENDING),
                new SortCriteria(SortField.NAME, SortOrder.ASCENDING));
        PersonComparator comparator = new PersonComparator(criteria);
        ListCommand expectedCommand = new ListCommand(new ListCommandPredicate(
                null, null, null, null, null), comparator);
        // User inputs name first, then favorites. Parser should reorder.
        assertParseSuccess(parser, " s/+n s/*", expectedCommand);
    }

    @Test
    public void parse_invalidSort_throwsParseException() {
        assertParseFailure(parser, " s/invalid",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " s/+invalid",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " s/n",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE)); // missing order

        // invalid format for favorites
        assertParseFailure(parser, " s/+*",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }
}
