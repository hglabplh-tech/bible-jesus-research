package org.harry.jesus.jesajautils.judaerrmsg;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionAlert extends Alert {


    public ExceptionAlert(Exception exception) {
        super(AlertType.ERROR);
        initialize(exception);

    }

    private void initialize(Exception exception) {
        this.setTitle("Exception Dialog");
        this.setHeaderText("Error occurred");
        this.setContentText("");
        String exceptionText;
        if (exception instanceof BibleStudyException) {
            exceptionText = exception.getMessage();
        } else {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println(exception.getMessage());
            exception.printStackTrace(pw);
            exceptionText = sw.toString();
        }

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

// Set expandable Exception into the dialog pane.
        this.getDialogPane().setExpandableContent(expContent);
    }

}
