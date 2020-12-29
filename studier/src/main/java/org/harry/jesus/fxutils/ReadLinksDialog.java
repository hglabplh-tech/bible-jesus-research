package org.harry.jesus.fxutils;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import jesus.harry.org.versnotes._1.Note;
import jesus.harry.org.versnotes._1.Vers;

import java.util.Optional;

/**
 * Class for a Store Password Diialog to store passwords for sites.
 * @author Harald Glab-Plhak
 */
public class ReadLinksDialog {

    /**
     * This method creates and calls the dialog to define a production place of a signature

     *
     */
    public static void showReadLinkDialog(String htmlText) {
        // Create the custom dialog.
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Create the Note");



// Set the button types.
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.OK);

        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, ButtonType.CANCEL);

// Create the passwordKey and password labels and fields.
        AnchorPane pane = new AnchorPane();
        WebView linkArea = new WebView();
        linkArea.getEngine().loadContent(htmlText);
        pane.getChildren().add(linkArea);

// Enable/Disable login button depending on whether a passwordKey was entered.



        dialog.getDialogPane().setContent(pane);

// Request focus on the passwordKey field by default.


// Convert the result to a passwordKey-password-pair when the login button is clicked.






        dialog.showAndWait();
    }
}
