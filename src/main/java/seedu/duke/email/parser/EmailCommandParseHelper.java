package seedu.duke.email.parser;

import seedu.duke.common.command.Command;
import seedu.duke.common.command.ExitCommand;
import seedu.duke.common.command.FlipCommand;
import seedu.duke.common.command.HelpCommand;
import seedu.duke.common.command.InvalidCommand;
import seedu.duke.common.command.Command.Option;
import seedu.duke.common.model.Model;
import seedu.duke.common.parser.CommandParseHelper;
import seedu.duke.email.EmailKeywordPairList;
import seedu.duke.email.command.EmailAddKeywordCommand;
import seedu.duke.email.command.EmailClearCommand;
import seedu.duke.email.command.EmailDeleteCommand;
import seedu.duke.email.command.EmailFetchCommand;
import seedu.duke.email.command.EmailFilterByTagCommand;
import seedu.duke.email.command.EmailFuzzySearchCommand;
import seedu.duke.email.command.EmailListAllTagsCommand;
import seedu.duke.email.command.EmailListCommand;
import seedu.duke.email.command.EmailListKeywordCommand;
import seedu.duke.email.command.EmailShowCommand;
import seedu.duke.email.command.EmailTagCommand;
import seedu.duke.email.entity.KeywordPair;
import seedu.duke.ui.UI;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.duke.common.parser.CommandParseHelper.isNumberTooLarge;

public class EmailCommandParseHelper {
    private static UI ui = UI.getInstance();

    /**
     * Parses the specific part of a user/file input that is relevant to email. A successful parsing always
     * returns an email-relevant Command.
     *
     * @param rawInput user/file input ready to be parsed.
     * @return an email-relevant Command.
     * @throws EmailParseException an exception when the parsing is failed, probably due to the wrong format
     *                             of input
     */
    public static Command parseEmailCommand(String rawInput, ArrayList<Option> optionList)
            throws EmailParseException {
        String strippedPrefixInput = stripPrefix(rawInput);
        if (strippedPrefixInput == null) {
            return new InvalidCommand("Command must not be empty. ");
        }
        String emailCommand = extractCommandWord(strippedPrefixInput);
        return parseByCommandType(optionList, strippedPrefixInput, emailCommand);
    }

    private static Command parseByCommandType(ArrayList<Option> optionList, String strippedPrefixInput,
                                              String emailCommand) throws EmailParseException {
        //generic commands shared with tasks
        switch (strippedPrefixInput) {
        case "flip":
            return new FlipCommand();
        case "bye":
            return new ExitCommand();
        case "help":
            return new HelpCommand();
        case "fetch":
            return new EmailFetchCommand();
        case "listKeyword":
            return new EmailListKeywordCommand();
        case "listTag":
            return new EmailListAllTagsCommand();
        case "clear":
            return new EmailClearCommand();
        default:
        }

        //email commands
        switch (emailCommand) {
        case "delete":
            return parseDeleteCommand(strippedPrefixInput);
        case "list":
            return parseEmailListCommand(optionList, strippedPrefixInput);
        case "show":
            return parseShowEmailCommand(strippedPrefixInput);
        case "update":
            return parseEmailTagCommand(optionList, strippedPrefixInput);
        case "addKeyword":
            return parseEmailAddKeywordCommand(optionList, strippedPrefixInput);
        case "fuzzySearch":
            return parseEmailFuzzySearchCommand(optionList, strippedPrefixInput);
        default:
            throw new EmailParseException("Invalid command word. Please enter \'help\' for more information");
        }
    }

    private static String extractCommandWord(String input) {
        return input.split(" ")[0];
    }

    /**
     * Removes prefix "email" from the input command.
     *
     * @param rawInput input command form user input text field.
     * @return stripped prefix command
     */
    private static String stripPrefix(String rawInput) {
        if (!rawInput.contains("email ")) {
            return null;
        }
        return rawInput.split("email ", 2)[1].strip();
    }

    private static Command parseDeleteCommand(String input) throws EmailParseException {
        Pattern deleteCommandPattern = Pattern.compile("^delete\\s+(?<index>\\d+)\\s*$");
        Matcher deleteCommandMatcher = deleteCommandPattern.matcher(input);
        if (!deleteCommandMatcher.matches()) {
            return new InvalidCommand("Please enter a valid index (positive integer equal or less than the "
                    + "number of emails) after \'delete\'");
        }
        try {
            int index = parseEmailIndex(deleteCommandMatcher.group("index"));
            return new EmailDeleteCommand(index);
        } catch (EmailParseException e) {
            throw new EmailParseException(e.getMessage());
        }
    }

    private static Command parseEmailListCommand(ArrayList<Option> optionList, String input)
            throws EmailParseException {
        if (optionList.size() == 0 && "list".equals(input)) {
            return new EmailListCommand();
        }
        ArrayList<String> tags = CommandParseHelper.extractTags(optionList);
        if (!tagsNotEmpty(tags)) {
            return new InvalidCommand("Please enter a tag name after \'-tag\' option");
        }
        // only a maximum of 2 input tags is allowed
        if (tags.size() > 2) {
            throw new EmailParseException("[Input format error] Maximum of 2 tag names are allowed for email tag-"
                    + "searching.");
        }
        return new EmailFilterByTagCommand(tags);
    }

    private static Command parseShowEmailCommand(String input) throws EmailParseException {
        Pattern showCommandPattern = Pattern.compile("^show\\s+(?<index>\\d+)\\s*$");
        Matcher showCommandMatcher = showCommandPattern.matcher(input);
        if (!showCommandMatcher.matches()) {
            return new InvalidCommand("Please enter a valid index (positive integer equal or less than the "
                    + "number of emails) after \'show\'");
        }
        try {
            int index = parseEmailIndex(showCommandMatcher.group("index"));
            return new EmailShowCommand(index);
        } catch (EmailParseException e) {
            throw new EmailParseException(e.getMessage());
        }
    }

    private static Command parseEmailTagCommand(ArrayList<Option> optionList,
                                                String input) throws EmailParseException {
        Pattern emailTagCommandPattern = Pattern.compile("^update\\s+(?<index>\\d+)\\s*$");
        Matcher emailTagCommandMatcher = emailTagCommandPattern.matcher(input);
        if (!emailTagCommandMatcher.matches()) {
            return new InvalidCommand("Please enter a valid email index (positive integer equal or lss "
                    + "than the number of emails) after \'update\'");
        }
        ArrayList<String> tags = CommandParseHelper.extractTags(optionList);
        if (!tagsNotEmpty(tags)) {
            return new InvalidCommand("Please enter a tag name after \'-tag\' option");
        }
        try {
            int index = parseEmailIndex(emailTagCommandMatcher.group("index"));
            return new EmailTagCommand(index, tags);
        } catch (EmailParseException e) {
            throw new EmailParseException(e.getMessage());
        }
    }

    private static Command parseEmailAddKeywordCommand(ArrayList<Option> optionList,
                                                       String input) {
        Pattern emailAddKeywordCommandPattern = Pattern.compile("^addKeyword\\s+(?<keyword>[\\w\\s]+)\\s*$");
        Matcher emailAddKeywordCommandMatcher = emailAddKeywordCommandPattern.matcher(input);
        if (!emailAddKeywordCommandMatcher.matches()) {
            return new InvalidCommand("Please enter a keyword after \'addKeyword\'");
        }
        String keyword = emailAddKeywordCommandMatcher.group("keyword");
        ArrayList<String> expressionList = extractExpressions(optionList);
        if (expressionList.size() < 1) {
            return new InvalidCommand("Please enter at least one expression option with \'-exp "
                    + "EXPRESSION\' format");
        }
        KeywordPair addedKeywordPair = new KeywordPair(keyword, expressionList);
        EmailKeywordPairList newKeywordPairList =
                Model.getInstance().getKeywordPairList().addAndCopy(addedKeywordPair);
        return new EmailAddKeywordCommand(newKeywordPairList);
    }

    private static Command parseEmailFuzzySearchCommand(ArrayList<Option> optionList,
                                                                        String input) {
        Pattern emailFuzzySearchCommandPattern = Pattern.compile("^fuzzySearch\\s+(?<target>\\w+)\\s*$");
        Matcher emailFuzzySearchCommandMatcher = emailFuzzySearchCommandPattern.matcher(input);
        if (!emailFuzzySearchCommandMatcher.matches()) {
            return new InvalidCommand("Please enter a search target string after \'fuzzySearch\'");
        }
        String target = emailFuzzySearchCommandMatcher.group("target");
        return new EmailFuzzySearchCommand(target);
    }

    private static boolean tagsNotEmpty(ArrayList<String> tags) {
        return tags.size() != 0;
    }

    private static int parseEmailIndex(String input) throws EmailParseException {
        if (isNumberTooLarge(input)) {
            throw new EmailParseException("Invalid index. Index of range 1 ~ 99999 is accepted.");
        }
        int index = Integer.parseInt(input) - 1;
        int emailListLength = Model.getInstance().getEmailListLength();
        if (emailListLength == 0) {
            throw new EmailParseException("email list is empty");
        }
        if (index < 0 || index >= emailListLength) {
            throw new EmailParseException("Index " + (index + 1) + " out of bounds of 1 to "
                    + emailListLength);
        }
        return index;
    }

    private static ArrayList<String> extractExpressions(ArrayList<Option> optionList) {
        ArrayList<String> expressionList = new ArrayList<>();
        for (Option option : optionList) {
            if (option.getKey().equals("exp")) {
                expressionList.add(option.getValue());
            }
        }
        return expressionList;
    }

    public static class EmailParseException extends CommandParseHelper.CommandParseException {
        /**
         * Instantiates the exception with a message, which is ready to be displayed by the UI.
         *
         * @param msg the message that is ready to be displayed by UI.
         */
        public EmailParseException(String msg) {
            super(msg);
        }
    }
}
