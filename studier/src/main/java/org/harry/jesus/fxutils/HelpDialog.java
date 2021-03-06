package org.harry.jesus.fxutils;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import jesus.harry.org.versnotes._1.Note;
import org.pmw.tinylog.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * The type Help dialog.
 */
public class HelpDialog {

    /**
     * Show help.
     *
     * @param resource the resource
     */
    public static void showHelp (String resource)  {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Application Help");


        try {
            InputStream helpContent = HelpDialog.class.getResourceAsStream(resource);
            if (helpContent != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int read = helpContent.read(buffer);
                while (read > -1) {
                    outStream.write(buffer, 0, read);
                    read = helpContent.read(buffer);
                }
                String htmlString = new String(outStream.toByteArray());
// Set the button types.
                ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.OK);

                DialogPane pane = dialog.getDialogPane();
                HBox inner = new HBox();
                inner.setMinWidth(980);
                inner.setMinHeight(680);
                pane.setContent(inner);
                pane.setMinWidth(1000);
                pane.setMinHeight(700);
                WebView viewer = new WebView();
                viewer.setMinWidth(980);
                viewer.setMinHeight(650);
                viewer.getEngine().loadContent(htmlString);
                inner.getChildren().add(viewer);


                dialog.showAndWait();
            }
        } catch (IOException ex) {
            Logger.trace(ex);
            Logger.trace("Cannot read help: " + ex.getMessage());
        }
    }

}
