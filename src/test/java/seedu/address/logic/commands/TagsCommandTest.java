package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;

/**
 * Contains integration tests (interaction with the Model) for {@code TagsCommand}.
 */
public class TagsCommandTest {

    @Test
    public void execute_noTags_returnsNoTagsMessage() {
        Model model = new ModelManager(); // empty model

        TagsCommand command = new TagsCommand();
        CommandResult result = command.execute(model);

        assertEquals(TagsCommand.MESSAGE_NO_TAGS, result.getFeedbackToUser());
    }

    @Test
    public void execute_withTags_returnsFormattedTags() {
        ModelManager model = new ModelManager();

        model.addTags(seedu.address.testutil.TypicalPersons.ALICE);

        TagsCommand command = new TagsCommand();
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.startsWith("Tags:"));
    }
}
