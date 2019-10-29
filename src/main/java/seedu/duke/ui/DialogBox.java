package seedu.duke.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Collections;

/**
 * An example of a custom control using FXML. This control represents a dialog box consisting of an ImageView
 * to represent the speaker's face and a label containing text from the speaker.
 */
class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private HBox dialogBox;

    private DialogBox(String text, Image img, String actor) {
        try {
            FXMLLoader fxmlLoader;
            if ("user".equals(actor)) {
                fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/UserDialogBox"
                        + ".fxml"));
            } else {
                fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DukeDialogBox.fxml"));

            }
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //BackgroundFill background_fill = new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY);
        //Background background = new Background(background_fill);
        //dialogBox.setBackground(background);
        dialog.setText(text);
        displayPicture.setImage(img);
    }

    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img, "user");
    }

    /**
     * Constructs a dialog box from the text and img given.
     *
     * @param text text to be displayed
     * @param img  img to be displayed
     * @return dialogBox ready to be displayed
     */
    public static DialogBox getDukeDialog(String text, Image img) {
        var db = new DialogBox(text, img, "duke");
        db.flip();
        return db;
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }
}
