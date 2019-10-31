package seedu.duke.common.command;

import seedu.duke.common.model.Model;
import seedu.duke.email.EmailList;
import seedu.duke.email.entity.Email;
import seedu.duke.task.TaskList;
import seedu.duke.task.entity.Task;
import seedu.duke.ui.UI;

import java.util.ArrayList;

public class LinkCommand extends Command{
    private ArrayList<Integer> taskIndexList;
    private ArrayList<Integer> emailIndexList;

    /**
     * Instantiates link command with all the necessary variables.
     *
     * @param taskIndexList  the index of tasks that is to be linked together.
     * @param emailIndexList the index of emails that is to be linked.
     */
    public LinkCommand(ArrayList<Integer> taskIndexList, ArrayList<Integer> emailIndexList) {
        this.taskIndexList = taskIndexList;
        this.emailIndexList = emailIndexList;
    }

    /**
     * Executes the link command.
     *
     * @param model allows access the taskList and emailList
     * @return      a flag whether linking is done successfully. Returns false otherwise.
     */
    @Override
    public boolean execute(Model model) {
        try {
            TaskList taskList = model.getTaskList();
            EmailList emailList = model.getEmailList();
            StringBuilder msg = new StringBuilder();

            if (emailIndexList.isEmpty()) {
                ArrayList<String> linkedEmails = taskList.get(taskIndexList.get(0)).getLinkedEmails();
                if (linkedEmails.isEmpty()) {
                    msg.append("No linked emails currently.");
                } else {
                    msg.append("Here are your linked emails:" + System.lineSeparator());
                    int i = 1;
                    for (String filename : linkedEmails) {
                        String name = null;
                        for (int j = 0; j < emailList.size(); j++) {
                            if (filename.equals(emailList.get(j).getShaHash())) {
                                name = emailList.get(j).getSubject();
                                break;
                            }
                        }
                        msg.append(i + ". " + name + System.lineSeparator());
                        i++;
                    }
                }
                responseMsg = msg.toString();
                UI.getInstance().showResponse(msg.toString());
                return true;
            } else {
                msg.append("Linked task ");
                for (int i = 0; i < taskIndexList.size(); i++) {
                    Task task = taskList.get(taskIndexList.get(i));
                    msg.append(task.getName());
                    msg.append(" with email(s):" + System.lineSeparator());

                    for (int j = 0; j < emailIndexList.size(); j++) {
                        Email email = emailList.get(emailIndexList.get(j));
                        msg.append(email.getSubject() + System.lineSeparator());
                        if (task.getLinkedEmails().contains(email.getShaHash())) {
                            continue;
                        }
                        task.addLinkedEmails(email.getShaHash());
                    }
                }
                if (!silent) {
                    responseMsg = msg.toString();
                    UI.getInstance().showResponse(msg.toString());
                }
                return true;
            }
        } catch (NullPointerException e) {
            if (!silent) {
                UI.getInstance().showError("Email index out of bounds");
            }
            return false;
        } catch (Exception e) {
            if (!silent) {
                UI.getInstance().showError(e.getMessage());
            }
            return false;
        }
    }
}
