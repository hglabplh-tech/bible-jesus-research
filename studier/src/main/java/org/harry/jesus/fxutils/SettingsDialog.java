package org.harry.jesus.fxutils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import jesus.harry.org.versnotes._1.Note;
import org.harry.jesus.synchjeremia.BibleThreadPool;
import org.harry.jesus.synchjeremia.SynchThread;

import javax.swing.event.HyperlinkEvent;
import java.io.File;
import java.util.Optional;

public class SettingsDialog {

    /**
     * This method creates and calls the dialog to define a production place of a signature

     *
     */
    public static void showAppSettingsDialog() {
        // Create the custom dialog.
        BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();
        Dialog<Void> dialog = new Dialog<>();
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
        Label mediaDirLab = new Label("Play Bible Directory:");
        TextField mediaDirField = new TextField();
        String path = context.getSettings().getProperty(BibleThreadPool.AUDIO_PATH,
                System.getProperty("user.home")
                        + File.separator
                        + "bibleStudyAudio");
        mediaDirField.setText(path);
        Button getDirButton = new Button("Get Directory");
        getDirButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String path = JesusMisc.showOpenDialogString(event, dialog.getDialogPane());
                path = path.substring(0, path.lastIndexOf(File.separator));
                mediaDirField.setText(path);
                context.addSetting(BibleThreadPool.AUDIO_PATH, path);
                SynchThread.storeApplicationProperties();
            }
        });
        grid.add(mediaDirLab, 0, 0);
        grid.add(mediaDirField, 1, 0);
        grid.add(getDirButton, 2, 0);

        dialog.getDialogPane().setContent(grid);

// Request focus on the passwordKey field by default.
        Platform.runLater(() -> mediaDirField.requestFocus());

        dialog.showAndWait();

    }
}
