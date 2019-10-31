package seedu.duke.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import seedu.duke.CommandParseHelper;
import seedu.duke.Duke;
import seedu.duke.common.command.Command;
import seedu.duke.common.model.Model;

import java.util.ArrayList;

public class UI {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static boolean debug = false;
    private static UI ui;
    // to output result to GUI
    private MainWindow mainWindow;
    private String input = "";
    private String command = "";
    // variable returned to GUI
    private String emailContent = "";
    private String responseMsg = "";

    /**
     * Constructor with necessary configurations.
     */
    private UI() {
        debug = true;
    }

    /**
     * Gets singleton ui instance.
     *
     * @return ui instance
     */
    public static UI getInstance() {
        if (ui == null) {
            ui = new UI();
        }
        return ui;
    }

    /**
     * Initializes ui.
     */
    public void initUi() {
        String logo = " ____        _        " + System.lineSeparator()
                + "|  _ \\ _   _| | _____ "  + System.lineSeparator()
                + "| | | | | | | |/ / _ \\"  + System.lineSeparator()
                + "| |_| | |_| |   <  __/"  + System.lineSeparator()
                + "|____/ \\__,_|_|\\_\\___|"  + System.lineSeparator();
        logo = "Hello from" + System.lineSeparator() + logo + System.lineSeparator();
        logo += "What can I do for you?";
        showMessage(logo);
        mainWindow.setInputPrefix();
    }

    public void setKeyBinding(Scene scene) {
        mainWindow.setKeyBinding(scene);
    }

    /**
     * Links up command output with GUI display.
     *
     * @param input user input
     */
    public void respond(String input) {
        try {
            setInput(input);
            Command command = CommandParseHelper.parseCommand(input);
            command.execute(Model.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDebug(boolean flag) {
        debug = flag;
    }

    /**
     * Shows a simple message without any format.
     *
     * @param msg the message that is to be shown
     */
    public void showMessage(String msg) {
        System.out.println(msg);
        showGui(msg);
    }

    /**
     * Shows a message in the format of a response, which is in between two lines.
     *
     * @param msg the message that is to be shown
     */
    public void showResponse(String msg) {
        this.responseMsg = msg;
        System.out.println("------------------------------");
        System.out.println(msg);
        System.out.println("------------------------------" + System.lineSeparator());
        showGui(msg);
    }

    /**
     * Shows an error message in the red color.
     *
     * @param msg the error message that is to be shown
     */
    public void showError(String msg) {
        String errorMsg = ANSI_RED + msg + ANSI_RESET;
        System.out.println(errorMsg);
        showGui(errorMsg);
    }

    /**
     * Shows a debug message when debug flag is on in yellow color.
     *
     * @param msg the debug message that is to be shown
     */
    public void showDebug(String msg) {
        String debugMsg = ANSI_YELLOW + msg + ANSI_RESET;
        if (debug) {
            System.out.println(debugMsg);
        }
        //showGui(debugMsg);
    }

    public String getEmailContent() {
        return this.emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setMainStage(Stage stage) {
        this.mainWindow.setMainStage(stage);
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    /**
     * Updates the task displayed in GUI when read from file or after user input handled.
     *
     * @param taskStringList list of tasks in string form to be displayed
     */
    public void updateTaskList(ArrayList<String> taskStringList) {
        mainWindow.updateTasksList(taskStringList);
    }

    /**
     * Updates email content shown on gui.
     */
    public void updateHtml() {
        mainWindow.updateHtml();
    }

    /**
     * Updates the emails displayed in GUI when read from file/Outlook server or after user input handled.
     *
     * @param emailStringList list of emails in string form to be displayed
     */
    public void updateEmailList(ArrayList<String> emailStringList) {
        if (mainWindow != null) {
            mainWindow.updateEmailsList(emailStringList);
        }
    }

    /**
     * Synchronizes the gui display of tasks and emails with the model.
     */
    public void syncWithModel() {
        Model model = Model.getInstance();
        model.updateGuiTaskList();
        model.updateGuiEmailList();
        model.updateEmailTagList();
    }

    /**
     * Show input command and output response in GUI.
     *
     * @param msg input
     */
    public void showGui(String msg) {
        if (mainWindow == null) {
            return;
        }
        mainWindow.showGuiMessage(msg, input, command);
        input = "";
        command = "";
    }

    /**
     * Shows a popup displaying long text message.
     *
     * @param text the text that is to be displayed in the popup
     */
    public void showTextPopup(String text) {
        mainWindow.showTextPopup(text);
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getPrefix() {
        return CommandParseHelper.getInputPrefix();
    }

    public void exit() {
        Duke.getInstance().exit();
    }
}