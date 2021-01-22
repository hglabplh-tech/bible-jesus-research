package org.harry.jesus.fxutils;

import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.RectangularShape;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;
import jesus.harry.org.versnotes._1.Note;

import java.util.Optional;

/**
 * The type Color dialog.
 */
public class ColorDialog {

    /**
     * Call color dialog optional.
     *
     * @return the optional
     */
    public static Optional<Color> callColorDialog() {
        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Color.CORAL);
        colorPicker.autosize();
        colorPicker.setEditable(true);
        colorPicker.setCenterShape(true);

        Dialog<Color> dialog = new Dialog<>();

        DialogPane pane = new DialogPane();
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().addAll(saveButtonType, ButtonType.OK);

        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().addAll(cancelButtonType, ButtonType.CANCEL);
        AnchorPane anchorPane = new AnchorPane();
        Border border = Border.EMPTY;
        anchorPane.setBorder(border);
     
        anchorPane.getChildren().add(colorPicker);
        pane.setContent(anchorPane);
        dialog.setDialogPane(pane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Color color = colorPicker.getValue();
                return color;
            }
            return null;
        });

        Platform.runLater(() -> colorPicker.requestFocus());

        return dialog.showAndWait();

    }
}
