package seedu.duke.email.command;

import seedu.duke.common.command.Command;
import seedu.duke.common.model.Model;
import seedu.duke.email.EmailTags;
import seedu.duke.ui.UI;

import java.util.ArrayList;

public class EmailListTagCommand extends Command {
    private ArrayList<String> tags;

    public EmailListTagCommand(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean execute(Model model) {
        responseMsg = EmailTags.displayEmailTagList(tags);
        UI.getInstance().showResponse(responseMsg);
        return true;
    }

}