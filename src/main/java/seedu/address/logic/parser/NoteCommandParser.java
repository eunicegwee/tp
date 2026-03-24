package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new NotesCommand object.
 */
public class NoteCommandParser implements Parser<NoteCommand> {

    @Override
    public NoteCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE));
        }

        String[] splitArgs = trimmedArgs.split("\\s+", 2);

        Index index;
        try {
            index = ParserUtil.parseIndex(splitArgs[0]);
        } catch (ParseException pe) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE), pe);
        }

        if (splitArgs.length < 2 || splitArgs[1].isBlank()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE));
        }

        String note = splitArgs[1].trim();

        return new NoteCommand(index, note);
    }
}
