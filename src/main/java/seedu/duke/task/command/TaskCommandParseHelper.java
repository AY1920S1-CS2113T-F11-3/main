package seedu.duke.task.command;

import seedu.duke.CommandParseHelper;
import seedu.duke.common.command.Command;
import seedu.duke.common.command.ExitCommand;
import seedu.duke.common.command.FlipCommand;
import seedu.duke.common.command.HelpCommand;
import seedu.duke.common.command.InvalidCommand;
import seedu.duke.common.model.Model;
import seedu.duke.task.entity.Task;
import seedu.duke.ui.UI;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.duke.CommandParseHelper.extractTags;
import static seedu.duke.CommandParseHelper.extractTime;

public class TaskCommandParseHelper {
    private static UI ui = UI.getInstance();

    /**
     * Parses a task command based on user input.
     *
     * @param rawInput   the raw user input without the options
     * @param optionList the options that are extracted from the raw input
     * @return the command parsed
     */
    public static Command parseTaskCommand(String rawInput,
                                           ArrayList<Command.Option> optionList) {
        if (rawInput.length() <= 5) {
            return new InvalidCommand();
        }
        String input = rawInput.split("task ", 2)[1].strip();
        if ("flip".equals(input)) {
            return new FlipCommand();
        } else if ("bye".equals(input)) {
            return new ExitCommand();
        } else if ("list".equals(input)) {
            return new TaskListCommand();
        } else if ("help".equals(input)) {
            return new HelpCommand();
        } else if (input.startsWith("done")) {
            return parseDoneCommand(input);
        } else if (input.startsWith("delete")) {
            return parseDeleteCommand(input);
        } else if (input.startsWith("find")) {
            return parseFindCommand(input);
        } else if (input.startsWith("reminder")) {
            return parseReminderCommand(input);
        } else if (input.startsWith("doafter")) {
            return parseDoAfterCommand(input, optionList);
        } else if (input.startsWith("snooze")) {
            return parseSnoozeCommand(input, optionList);
        } else if (input.startsWith("todo") | input.startsWith("deadline") | input.startsWith("event")) {
            return parseAddTaskCommand(input, optionList);
        } else if (input.startsWith("update")) {
            return parseUpdateCommand(input, optionList);
        } else if (input.startsWith("set")) {
            return parsePriorityCommand(input, optionList);
        }
        return new InvalidCommand();
    }

    private static Matcher prepareCommandMatcher(String input, String s) {
        Pattern commandPattern = Pattern.compile(s);
        return commandPattern.matcher(input);
    }

    private static Command parseDoneCommand(String input) {
        Matcher doneCommandMatcher = prepareCommandMatcher(input, "^done\\s+(?<index>\\d+)\\s*$");
        if (!doneCommandMatcher.matches()) {
            showError("Please enter a valid index of task after \'done\'");
            return new InvalidCommand();
        }
        try {
            int index = parseTaskIndex(doneCommandMatcher.group("index"));
            return new TaskDoneCommand(index);
        } catch (TaskParseException e) {
            showError(e.getMessage());
        }
        return new InvalidCommand();
    }

    private static Command parseDeleteCommand(String input) {
        Matcher deleteCommandMatcher = prepareCommandMatcher(input, "^delete\\s+(?<index>\\d+)\\s*$");
        if (!deleteCommandMatcher.matches()) {
            showError("Please enter a valid index of task after \'delete\'");
            return new InvalidCommand();
        } else {
            try {
                int index = parseTaskIndex(deleteCommandMatcher.group("index"));
                return new TaskDeleteCommand(index);
            } catch (TaskParseException e) {
                showError(e.getMessage());
            }
        }
        return new InvalidCommand();
    }

    private static int parseTaskIndex(String input) throws TaskParseException {
        if (input.length() >= 6) {
            throw new TaskParseException("Invalid index. Index should be of range 1 ~ 99999.");
        }
        int index = Integer.parseInt(input) - 1;
        if (index < 0 || index >= Model.getInstance().getTaskListLength()) {
            throw new TaskParseException("Index out of bounds. ");
        }
        return index;
    }

    private static Command parseFindCommand(String input) {
        Matcher findCommandMatcher = prepareCommandMatcher(input, "^find\\s+(?<keyword>[\\w]+[\\s|\\w]*)\\s*$");
        if (!findCommandMatcher.matches()) {
            showError("Please enter keyword for searching after \'find\'");
        } else {
            String keyword = findCommandMatcher.group("keyword").strip();
            return new TaskFindCommand(keyword);
        }
        return new InvalidCommand();
    }

    private static Command parseReminderCommand(String input) {
        Matcher reminderCommandMatcher = prepareCommandMatcher(input, "^reminder(?:\\s+(?<dayLimit>[\\d]*)\\s*)?");
        if (!reminderCommandMatcher.matches()) {
            showError("Please enter reminder with or without a number, which is the maximum number "
                    + "of days from now for a task to be considered as near");
            return new InvalidCommand();
        }
        try {
            int dayLimit = extractDayLimit(reminderCommandMatcher);
            if (dayLimit < 0) {
                showError("Reminder day limit cannot be negative. Default is used.");
                return new TaskReminderCommand();
            } else {
                return new TaskReminderCommand(dayLimit);
            }
        } catch (NumberFormatException e) {
            showError("Reminder day limit in wrong format. Default is used.");
            return new TaskReminderCommand();
        }
    }

    private static int extractDayLimit(Matcher reminderCommandMatcher) {
        int dayLimit = -1;
        String dayLimitString = reminderCommandMatcher.group("dayLimit");
        if (dayLimitString.length() > 6) {
            showError("Reminder day limit too large. Default is used.");
        } else {
            dayLimit = Integer.parseInt(dayLimitString);
        }
        return dayLimit;
    }

    private static Command parseDoAfterCommand(String input, ArrayList<Command.Option> optionList) {
        Matcher doAfterCommandMatcher = prepareCommandMatcher(input, "^do[a|A]fter\\s+(?<index>[\\d]+)\\s*$");
        if (!doAfterCommandMatcher.matches()) {
            showError("Please enter doAfter command in the correct format with index and description"
                    + " in -msg option");
            return new InvalidCommand();
        }
        String description = extractMsg(optionList);
        if ("".equals(description)) {
            showError("Please enter a description of doAfter command after \'-msg \' option");
            return new InvalidCommand();
        }
        try {
            int index = parseTaskIndex(doAfterCommandMatcher.group("index"));
            return new TaskDoAfterCommand(index, description);
        } catch (TaskParseException e) {
            showError(e.getMessage());
            return new InvalidCommand();
        }
    }

    private static String extractMsg(ArrayList<Command.Option> optionList) {
        String description = "";
        for (Command.Option option : optionList) {
            if (option.getKey().equals("msg")) {
                description = option.getValue();
                break;
            }
        }
        return description;
    }

    private static Command parsePriorityCommand(String input, ArrayList<Command.Option> optionList) {
        Matcher priorityCommandMatcher = prepareCommandMatcher(input, "^set\\s+(?<index>[\\d]+)\\s*$");
        if (!priorityCommandMatcher.matches()) {
            showError("Please enter task index after 'set' and priority level after '-priority' "
                    + "option");
            return new InvalidCommand();
        }
        try {
            String priority = extractPriority(optionList);
            if (priority.equals("")) {
                showError("Please enter a priority level to set for the task after \'-priority\' option");
                return new InvalidCommand();
            }
            int index = parseTaskIndex(priorityCommandMatcher.group("index"));
            return new TaskSetPriorityCommand(index, priority);
        } catch (TaskParseException e) {
            showError(e.getMessage());
            return new InvalidCommand();
        } catch (NumberFormatException e) {
            showError("Please enter a valid task index after \'set\'");
            return new InvalidCommand();
        }
    }

    private static Command parseSnoozeCommand(String input, ArrayList<Command.Option> optionList) {
        Matcher snoozeCommandMatcher = prepareCommandMatcher(input, "^snooze\\s+(?<index>[\\d]+)\\s*$");
        if (!snoozeCommandMatcher.matches()) {
            showError("Please enter task index after 'snooze' and duration to snooze after '-by' ");
            return new InvalidCommand();
        }
        try {
            String snooze = extractSnooze(optionList);
            if (snooze.equals("")) {
                snooze = "3";
            }
            int index = parseTaskIndex(snoozeCommandMatcher.group("index"));
            int duration = Integer.parseInt(snooze);
            return new TaskSnoozeCommand(index, duration);
        } catch (TaskParseException e) {
            showError(e.getMessage());
            return new InvalidCommand();
        }
    }

    private static Command parseUpdateCommand(String input, ArrayList<Command.Option> optionList) {
        ArrayList<TaskUpdateCommand.Attributes> attributes = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        Matcher editMatcher = prepareCommandMatcher(input, "^update\\s+(?<index>\\d+)\\s*$");
        if (!editMatcher.matches()) {
            showError("Please enter an index after \'update\'");
            return new InvalidCommand();
        }
        try {
            final int index = parseTaskIndex(editMatcher.group("index"));
            addTimeToUpdateCommand(optionList, attributes, descriptions);
            addDoAfterToUpdateCommand(optionList, attributes, descriptions);
            addPriorityToUpdateCommand(optionList, attributes, descriptions);
            addTagsToUpdateCommand(optionList, attributes, descriptions);
            return new TaskUpdateCommand(index, descriptions, attributes);
        } catch (NumberFormatException e) {
            showError("Please enter correct task index: " + editMatcher.group(
                    "index"));
            return new InvalidCommand();
        } catch (CommandParseHelper.CommandParseException e) {
            return new InvalidCommand();
        }
    }

    private static void addTimeToUpdateCommand(ArrayList<Command.Option> optionList,
                                               ArrayList<TaskUpdateCommand.Attributes> attributes,
                                               ArrayList<String> descriptions)
            throws CommandParseHelper.CommandParseException {
        if (!"".equals(CommandParseHelper.extractTime(optionList))) {
            descriptions.add(CommandParseHelper.extractTime(optionList));
            attributes.add(TaskUpdateCommand.Attributes.TIME);
        }
    }

    private static void addDoAfterToUpdateCommand(ArrayList<Command.Option> optionList,
                                                  ArrayList<TaskUpdateCommand.Attributes> attributes,
                                                  ArrayList<String> descriptions)
            throws TaskParseException {
        if (!extractDoAfter(optionList).equals("")) {
            descriptions.add(extractDoAfter(optionList));
            attributes.add(TaskUpdateCommand.Attributes.DO_AFTER);
        }
    }

    private static void addPriorityToUpdateCommand(ArrayList<Command.Option> optionList,
                                                   ArrayList<TaskUpdateCommand.Attributes> attributes,
                                                   ArrayList<String> descriptions)
            throws TaskParseException {
        if (!extractPriority(optionList).equals("")) {
            descriptions.add(extractPriority(optionList));
            attributes.add(TaskUpdateCommand.Attributes.PRIORITY);
        }
    }

    private static void addTagsToUpdateCommand(ArrayList<Command.Option> optionList,
                                               ArrayList<TaskUpdateCommand.Attributes> attributes,
                                               ArrayList<String> descriptions)
            throws TaskParseException {
        ArrayList<String> tags = extractTags(optionList);
        if (!tags.isEmpty()) {
            for (String tag : tags) {
                descriptions.add(tag);
                attributes.add(TaskUpdateCommand.Attributes.TAG);
            }

        }
    }

    private static String extractDoAfter(ArrayList<Command.Option> optionList)
            throws TaskParseException {
        String doafter = "";
        for (Command.Option option : optionList) {
            if (option.getKey().equals("doafter")) {
                if (doafter.equals("")) {
                    doafter = option.getValue();
                } else {
                    throw new TaskParseException("Each task can have only one doafter option");
                }
            }
        }
        return doafter;
    }

    private static String extractPriority(ArrayList<Command.Option> optionList)
            throws TaskParseException {
        String priority = "";
        for (Command.Option option : optionList) {
            if (option.getKey().equals("priority")) {
                if (priority.equals("")) {
                    priority = option.getValue();
                } else {
                    throw new TaskParseException("Each task can have only one priority");
                }
            }
        }
        return priority;
    }

    private static String extractSnooze(ArrayList<Command.Option> optionList) {
        String snooze = "";
        for (Command.Option option : optionList) {
            if (option.getKey().equals("by") && snooze.equals("")) {
                snooze = option.getValue();
            }
        }
        return snooze;
    }

    /**
     * Parses the specific part of a user/file input that is relevant to a task. A successful parsing always
     * returns an AddCommand, as it is assumed that an input starting with a task name is an add command.
     *
     * @param input      user/file input ready to be parsed
     * @param optionList contains all options specified in input command
     * @return an AddCommand of the task parsed from the input
     */
    private static Command parseAddTaskCommand(String input,
                                               ArrayList<Command.Option> optionList) {
        try {
            String doAfter = extractDoAfter(optionList);
            LocalDateTime time = parseTaskTime(optionList);
            ArrayList<String> tags = extractTags(optionList);
            String priority = extractPriority(optionList);
            return constructAddCommandByType(input, doAfter, time, tags, priority);
        } catch (TaskParseException e) {
            showError(e.getMessage());
            return new InvalidCommand();
        }
    }

    /**
     * Gets time in LocalDateTime format from string extracted.
     *
     * @param optionList contains all options specified in input command
     * @return time in LocalDateTime format
     */
    public static LocalDateTime parseTaskTime(ArrayList<Command.Option> optionList) {
        try {
            String timeString = extractTime(optionList);
            return TaskParseNaturalDateHelper.getDate(timeString);
        } catch (CommandParseHelper.CommandParseException e) {
            return null;
        }
    }

    private static Command constructAddCommandByType(String input, String doAfter, LocalDateTime time,
                                                     ArrayList<String> tags, String priority) {
        if (input.startsWith("todo")) {
            return parseAddToDoCommand(input, doAfter, tags, priority);
        } else if (input.startsWith("deadline")) {
            return parseAddDeadlineCommand(input, time, doAfter, tags, priority);
        } else if (input.startsWith("event")) {
            return parseEventCommand(input, time, doAfter, tags, priority);
        } else {
            return new InvalidCommand();
        }
    }

    private static Command parseAddToDoCommand(String input, String doAfter,
                                               ArrayList<String> tags, String priority) {
        Task.TaskType taskType = Task.TaskType.ToDo;
        Matcher toDoMatcher = prepareCommandMatcher(input, "todo\\s+(?<name>\\w+[\\s+\\w+]*)\\s*");
        if (!toDoMatcher.matches()) {
            showError("Please enter a name after todo");
            return new InvalidCommand();
        }
        String name = toDoMatcher.group("name");
        return new TaskAddCommand(taskType, name, null, doAfter, tags, priority);
    }

    private static Command parseAddDeadlineCommand(String input,
                                                   LocalDateTime time, String doAfter,
                                                   ArrayList<String> tags, String priority) {
        Task.TaskType taskType = Task.TaskType.Deadline;
        Matcher deadlineMatcher = prepareCommandMatcher(input, "deadline\\s+(?<name>\\w+[\\s+\\w+]*)\\s*");
        if (!deadlineMatcher.matches()) {
            showError("Please enter a name after \'deadline\'");
            return new InvalidCommand();
        }
        if (time == null) {
            showError("Please enter a time of correct format after \'-time\'");
            return new InvalidCommand();
        }
        String name = deadlineMatcher.group("name");
        return new TaskAddCommand(taskType, name, time, doAfter, tags, priority);
    }

    private static Command parseEventCommand(String input, LocalDateTime time,
                                             String doAfter, ArrayList<String> tags, String priority) {
        Task.TaskType taskType = Task.TaskType.Event;
        Matcher eventMatcher = prepareCommandMatcher(input, "event\\s+(?<name>\\w+[\\s+\\w+]*)\\s*");
        if (!eventMatcher.matches()) {
            showError("Please enter a name after \'event\'");
            return new InvalidCommand();
        }
        if (time == null) {
            showError("Please enter a time of correct format after \'-time\'");
            return new InvalidCommand();
        }
        String name = eventMatcher.group("name");
        return new TaskAddCommand(taskType, name, time, doAfter, tags, priority);
    }

    /**
     * Wraps around ui.showError by checking whether ui is null. This avoids the problem that ui is not
     * initialized during unit test.
     *
     * @param errorMessage the error message to be shown
     */
    private static void showError(String errorMessage) {
        if (ui != null) {
            ui.showError(errorMessage);
        }
    }

    private static class TaskParseException extends CommandParseHelper.CommandParseException {

        /**
         * Instantiates the exception with a message, which is ready to be displayed by the UI.
         *
         * @param msg the message that is ready to be displayed by UI.
         */
        public TaskParseException(String msg) {
            super(msg);
        }
    }
}
