package org.harry.jesus.fxutils;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import jesus.harry.org.versnotes._1.Note;
import jesus.harry.org.versnotes._1.Vers;

import java.util.Optional;

/**
 * Class for a Store Password Diialog to store passwords for sites.
 * @author Harald Glab-Plhak
 */
public class CreateNoteDialog {

    /**
     * This method creates and calls the dialog to define a production place of a signature
     * @param note the note to add
     * @return the ready edited note
     */
    public static Optional<Note> showNoteCreateDialog(Note note) {
        // Create the custom dialog.
        Dialog<Note> dialog = new Dialog<>();
        dialog.setTitle("Create the Note");



// Set the button types.
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.OK);

        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, ButtonType.CANCEL);

// Create the passwordKey and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextArea versArea = new TextArea();
        StringBuffer buffer = new StringBuffer();
        for (Vers vers: note.getVerslink()) {
            buffer.append(vers.getVtext() + "\n");
        }
        versArea.setText(buffer.toString());
        grid.add(versArea, 0, 0);
        TextField noteField = new TextField();
        grid.add(noteField,0,1);
// Enable/Disable login button depending on whether a passwordKey was entered.

        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Color.CORAL);
        colorPicker.autosize();
        colorPicker.setEditable(true);
        colorPicker.setCenterShape(true);
        grid.add(colorPicker,0,2);


        dialog.getDialogPane().setContent(grid);

// Request focus on the passwordKey field by default.
        Platform.runLater(() -> noteField.requestFocus());

// Convert the result to a passwordKey-password-pair when the login button is clicked.

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Note result = new Note();
                result.getVerslink().addAll(note.getVerslink());
                result.setNote(noteField.getText());
                for (Vers verse: result.getVerslink()) {
                    verse.setBackcolor(colorPicker.getValue().toString());
                }

                return result;
            }
            return null;
        });


        return dialog.showAndWait();
    }
}
