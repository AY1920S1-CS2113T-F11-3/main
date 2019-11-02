package seedu.duke.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import seedu.duke.common.parser.CommandParseHelper;
import seedu.duke.Duke;
import seedu.duke.common.command.Command;
import seedu.duke.common.model.Model;
import seedu.duke.common.storage.Storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class UI {
    private static Logger logger = Logger.getLogger("ui");

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
        String helloMsg = "Hello from Email Manager" + System.lineSeparator();
        helloMsg += "What can I do for you?";
        showMessage(helloMsg);
        mainWindow.setInputPrefix();
        setupLogger();
    }

    /**
     * Sets up logger with fileHandler to write log data to external text file.
     */
    public void setupLogger() {
        Path logPath = Storage.prepareLogFolderPath();
        File logDir = new File(logPath.toString());
        if (!(logDir.exists())) {
            logDir.mkdir();
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        String fileName = logPath + File.separator +  "log" + dateStr +  ".txt";

        FileHandler fh = null;
        try {
            fh = new FileHandler(fileName);
            fh.setFormatter(new SimpleFormatter());
            fh.setLevel(Level.ALL);
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, e.toString());
        }
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
            logger.log(Level.INFO, "[User Input] " + input);
            logger.log(Level.INFO, "[Command] " + command.toString());
            command.execute(Model.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, e.toString());
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
        if ("".equals(msg)) {
            return;
        }
        System.out.println(ANSI_RESET + msg);
        showGui(msg);
        logger.log(Level.INFO, "[Message] " + msg);
    }

    /**
     * Shows a message in the format of a response, which is in between two lines.
     *
     * @param msg the message that is to be shown
     */
    public void showResponse(String msg) {
        if ("".equals(msg)) {
            return;
        }
        this.responseMsg = msg;
        System.out.println(ANSI_RESET + "------------------------------");
        System.out.println(msg);
        System.out.println("------------------------------" + System.lineSeparator());
        showGui(msg);
        logger.log(Level.INFO, "[Response] " + msg);
    }

    /**
     * Shows an error message in the red color.
     *
     * @param msg the error message that is to be shown
     */
    public void showError(String msg) {
        if ("".equals(msg)) {
            return;
        }
        String errorMsg = ANSI_RED + msg + ANSI_RESET;
        System.out.println(errorMsg);
        showGui(msg);
        logger.log(Level.SEVERE, "[Error] " + errorMsg);
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
        logger.log(Level.FINE, "[Debug] " + msg);
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
        if (mainWindow == null || "".equals(msg)) {
            return;
        }
        mainWindow.showGuiMessage(msg, input);
        input = "";
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

    public String getPrefix() {
        return CommandParseHelper.getInputPrefix();
    }

    public void exit() {
        Duke.getInstance().exit();
    }
}