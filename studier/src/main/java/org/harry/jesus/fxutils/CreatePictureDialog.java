package org.harry.jesus.fxutils;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import jesus.harry.org.versnotes._1.Vers;
import org.harry.jesus.fxutils.graphics.ImageMaker;
import org.harry.jesus.jesajautils.Tuple;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Class for a Store Password Diialog to store passwords for sites.
 * @author Harald Glab-Plhak
 */
public class CreatePictureDialog {

    private ColorPicker picker = new ColorPicker();

    private ChoiceBox<Float> fontSizeBox = new ChoiceBox<>();

    private ChoiceBox<Font> fontsBox = new ChoiceBox<>();

    private ImageView imageArea = new ImageView();

    private GraphicsEnvironment ge;

    private List<Font> awtFonts = new ArrayList<>();


    public CreatePictureDialog() {
        fontSizeBox.getItems().add(10f);
        fontSizeBox.getItems().add(15f);
        fontSizeBox.getItems().add(20f);
        fontSizeBox.getItems().add(25f);
        fontSizeBox.getItems().add(30f);
        fontSizeBox.getItems().add(35f);
        fontSizeBox.getItems().add(40f);
        fontSizeBox.getItems().add(45f);
        fontSizeBox.getItems().add(50f);
        fontSizeBox.setValue(25f);
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        String[] names = ge.getAvailableFontFamilyNames();
        Font[] allFonts = ge.getAllFonts();
        awtFonts = Arrays.asList(allFonts);
        fontsBox.getItems().addAll(awtFonts);

    }

    /**
     * This method creates and calls the dialog to create verse images
     * @param theVers vers to be created as image
     *
     */
    public  Optional<Image> showPictureCreateDialog(Vers theVers) {
        // Create the custom dialog.
        Dialog<Image> dialog = new Dialog<>();
        dialog.setTitle("Create the Note");



// Set the button types.
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.OK);

        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, ButtonType.CANCEL);

// Create the passwordKey and password labels and fields.
        GridPane grid = new GridPane();
        grid.setMinHeight(700);
        grid.setMinWidth(1100);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));



        Image source;


        grid.add(imageArea, 0, 2, 2, 1);
// Enable/Disable login button depending on whether a passwordKey was entered.


        picker.setEditable(true);
        picker.setValue(javafx.scene.paint.Color.GREEN);
        grid.add(picker, 0, 1);

        grid.add(fontSizeBox, 1, 1);

        grid.add(fontsBox, 2, 1);
        InputStream imageIN = JesusMisc.showOpenDialog(dialog.getDialogPane());
        if (imageIN != null) {
            source = new Image(imageIN);
            BufferedImage bImage = SwingFXUtils.fromFXImage(source, null);
            Optional<BufferedImage> scaled = ImageMaker.createScaledImage(700, 500, bImage);
            if (scaled.isPresent()) {
                source = SwingFXUtils.toFXImage(scaled.get(), null);
                Tuple<Integer, Integer> widthHeightTuple = ImageMaker.getZoomValues(source, 1000);
                imageArea.setPreserveRatio(false);
                imageArea.setFitWidth(widthHeightTuple.getFirst());
                imageArea.setFitHeight(widthHeightTuple.getSecond());
                imageArea.setImage(source);
            }
        } else {
            source = new Image(
                    CreatePictureDialog.class.getResourceAsStream("/graphics/buddy.jpg"));
        }
        picker.setOnAction(new ShowButtonHandler(theVers, source));
        Image finalSource = source;
        fontSizeBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                fontSizeBox.setValue(fontSizeBox.getItems().get(newValue.intValue()));
                new ShowButtonHandler(theVers, finalSource).handle(new ActionEvent());
            }
        });
        fontsBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                fontsBox.setValue(fontsBox.getItems().get(newValue.intValue()));
                new ShowButtonHandler(theVers, finalSource).handle(new ActionEvent());
            }
        });

        dialog.getDialogPane().setContent(grid);

// Request focus on the passwordKey field by default.
        Platform.runLater(() -> imageArea.requestFocus());

// Convert the result to a passwordKey-password-pair when the login button is clicked.

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Image result = imageArea.getImage();
                return result;
            }
            return null;
        });

        fontsBox.getSelectionModel().select(awtFonts.get(0));

        return dialog.showAndWait();
    }

    private class ShowButtonHandler implements EventHandler<ActionEvent> {

        private final Image source;

        private final Vers theVers;

        public ShowButtonHandler(Vers theVers,Image source) {
            this.source = source;
            this.theVers = theVers;
        }
        @Override
        public void handle(ActionEvent event) {
            final Image image = this.source;
            if (image != null) {
                BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
                Optional<BufferedImage> result = ImageMaker
                        .createImage(theVers.getVtext(),
                                ImageMaker.convertColor(picker.getValue()),
                                fontSizeBox.getValue(),
                                fontsBox.getValue(),
                                bImage);
                if (result.isPresent()) {
                    Image fxResult = SwingFXUtils.toFXImage(result.get(), null);
                    imageArea.setImage(fxResult);
                }
            }
        }
    }


}
