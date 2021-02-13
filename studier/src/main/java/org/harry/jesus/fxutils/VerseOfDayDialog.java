package org.harry.jesus.fxutils;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

import java.util.UUID;

/**
 * The type Verse of day dialog.
 */
public class VerseOfDayDialog {

    /**
     * Show verse.
     */
    public static void showVerse() {
        Dialog<Void> verseDialog = new Dialog<>();
        verseDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        StackPane stack = new StackPane();
        WebView view = new WebView();
        stack.getChildren().add(view);
        verseDialog.getDialogPane().setContent(stack);
        view.getEngine().load("http://localhost:8980/dayVerse");
        verseDialog.showAndWait();
    }
}
