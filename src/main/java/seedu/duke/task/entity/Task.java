package seedu.duke.task.entity;

import seedu.duke.common.parser.CommandParseHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Super class of all kinds of tasks, with the basic functionality that all tasks share.
 */
public class Task {

    /**
     * The enumeration of priority level.
     */
    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    /**
     * The enumeration of all task type.
     */
    public enum TaskType {
        TODO, DEADLINE, EVENT
    }

    /**
     * A date format that is shared by all tasks to parse and out the date involved in the task.
     */
    protected static DateTimeFormatter format = DateTimeFormatter
            .ofPattern("dd/MM/uuuu HHmm", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    /**
     * The name of the task.
     */
    protected String name;

    /**
     * The flag whether is task is already done. Can only be set from false to true.
     */
    protected boolean isDone;

    /**
     * The type of the task, following the enumeration declared.
     */
    protected TaskType taskType;

    protected String doAfterDescription;

    /**
     * The tag list that the task has.
     */
    protected ArrayList<String> tags;

    /**
     * The list of linked emails that the task has.
     */
    protected ArrayList<String> linkedEmails;

    /**
     * The priority assigned to the task.
     */
    protected String priority;

    /**
     * The level of priority.
     */
    protected Priority level;

    /**
     * Instantiation of a task with the name and the default false value if isDone attribute.
     *
     * @param name the name of the task
     */
    public Task(String name) {
        this.name = name;
        this.isDone = false;
        this.doAfterDescription = null;
        this.tags = new ArrayList<>();
        this.priority = null;
        this.linkedEmails = new ArrayList<>();
        this.level = null;
    }

    /**
     * The function is used to parse the input string to a Date that is used by the tasks with time involved.
     * The function can be called before the initialization of a Task so that the Data can be directly passed
     * to the constructor.
     *
     * @param dateString an input string to be parsed
     * @return parsed result from the input string
     * @throws CommandParseHelper.CommandParseException an exception when the parsing is failed, most likely
     *                                                  due to a wrong format
     */
    public static LocalDateTime parseDate(String dateString) throws CommandParseHelper.CommandParseException {
        try {
            return LocalDateTime.parse(dateString, format);
        } catch (DateTimeParseException e) {
            throw new CommandParseHelper.CommandParseException("Wrong Date Time format");
        }
    }

    /**
     * Marks the isDone as true.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Gets the status whether the task is done.
     *
     * @return the isDone attribute
     */
    public boolean getDone() {
        return this.isDone;
    }

    /**
     * Snoozes task for the duration inputted.
     *
     * @param duration the duration to snooze the task by
     */
    public void snooze(int duration) {
        if (taskType == TaskType.TODO) {
            return;
        }
    }

    /**
     * The function that returns a human readable string of the basic information of the task.
     *
     * @return the human readable string of the basic information the task.
     */
    protected String getStatus() {
        if (this.isDone) {
            return "[/] " + this.name;
        } else {
            return "[X] " + this.name;
        }
    }

    public TaskType getTaskType() {
        return this.taskType;
    }

    /**
     * Converts the task to a human readable string. This will return the same string as the get status
     * function for a basic task, but is overridden by more advanced task class to add more information.
     *
     * @return a human readable string that contains all important information of a task.
     */
    public String toString() {
        return this.getStatus();
    }

    /**
     * The function returns a string that contains full information of the task which is used to be stored in
     * a file for future usage.
     *
     * @return a string containing full information of the task.
     */
    public String toFileString() {
        return this.toString();
    }

    /**
     * The function checks whether this task, when converted to string, contains the keyword specified.
     *
     * @param keyword search target string
     * @return a flag whether the keyword is found in the task string
     */
    public boolean matchKeyword(String keyword) {
        return this.toString().contains(keyword);
    }

    /**
     * The default function determining whether the deadline or event is near enough. It will be overridden in
     * deadline or event, but not todo.
     *
     * @param dayLimit the maximum number of days from now for a task to be considered as near
     * @return whether the task is near enough
     */
    public boolean isNear(int dayLimit) {
        return false;
    }

    public void addLinkedEmails(String email) {
        linkedEmails.add(email);
    }

    public ArrayList<String> getLinkedEmails() {
        return linkedEmails;
    }

    public boolean isClash(Task task) {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setDoAfterDescription(String description) {
        this.doAfterDescription = description;
    }

    /**
     * Set priority of task.
     *
     * @param priority priority level of task.
     */
    public void setPriorityTo(Priority priority) {
        if (priority == null) {
            this.priority = "";
        } else {
            this.priority = priority.name();
        }
    }

    public void setPriorityLevelTo(Priority level) {
        this.level = level;
    }
}
