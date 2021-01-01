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
        Label bibleDirLab = new Label("Bible Source Bible Directory:");
        TextField bibleDirField = new TextField();
        Label mediaDirLab = new Label("Play Bible Directory:");
        TextField mediaDirField = new TextField();
        String path = context.getSettings().getProperty(BibleThreadPool.AUDIO_PATH,
                System.getProperty("user.home")
                        + File.separator
                        + "bibleStudyAudio");
        String biblePath = context.getSettings().getProperty(BibleThreadPool.BIBLE_XML_PATH,
                System.getProperty("user.home")
                        + File.separator
                        + "bibleStudyAudio");
        mediaDirField.setText(path);
        Button getMediaDirButton = new Button("Get Media Directory");
        Button getBibleDirButton = new Button("Get Media Directory");
        getBibleDirButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String biblePath = JesusMisc.showOpenDialogString(event, dialog.getDialogPane());
                biblePath = biblePath.substring(0, biblePath.lastIndexOf(File.separator));
                mediaDirField.setText(biblePath);
                context.addSetting(BibleThreadPool.BIBLE_XML_PATH, biblePath);
                SynchThread.storeApplicationProperties();
            }
        });
        getMediaDirButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String path = JesusMisc.showOpenDialogString(event, dialog.getDialogPane());
                path = path.substring(0, path.lastIndexOf(File.separator));
                mediaDirField.setText(path);
                context.addSetting(BibleThreadPool.AUDIO_PATH, path);
                SynchThread.storeApplicationProperties();
            }
        });
        grid.add(bibleDirLab, 0, 0);
        grid.add(bibleDirField, 1, 0);
        grid.add(getBibleDirButton, 2, 0);
        grid.add(mediaDirLab, 0, 1);
        grid.add(mediaDirField, 1, 1);
        grid.add(getMediaDirButton, 2, 1);

        dialog.getDialogPane().setContent(grid);

// Request focus on the passwordKey field by default.
        Platform.runLater(() -> mediaDirField.requestFocus());

        dialog.showAndWait();

    }
}
