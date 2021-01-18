package org.harry.jesus.fxutils;

import javafx.application.Platform;
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
import org.harry.jesus.synchjeremia.SynchThread;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;


/**
 * Class for a Store Password Diialog to store passwords for sites.
 * @author Harald Glab-Plhak
 */
public class CreatePictureDialog {

    private ColorPicker picker = new ColorPicker();

    private ChoiceBox<Float> fontSizeBox = new ChoiceBox<>();

    private ImageView imageArea = new ImageView();

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
        fontSizeBox.getSelectionModel().select(25f);
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


        Button setTextButton = new Button ("Show Result");
        grid.add(setTextButton,0,0);

        picker.setEditable(true);
        picker.setValue(javafx.scene.paint.Color.GREEN);
        grid.add(picker, 0, 1);

        grid.add(fontSizeBox, 1, 1);
        InputStream imageIN = JesusMisc.showOpenDialog(dialog.getDialogPane());
        if (imageIN != null) {
            source = new Image(imageIN);
            double ratio = source.getWidth() / source.getHeight();
            int width = 0;
            int height = 0;
            if (1000 / ratio < 1000) {
                width = 1000;
                height = (int) (1000 / ratio);
            } else if (1000 * ratio < 1000) {
                height = 1000;
                width = (int) (1000 * ratio);
            } else {
                height = 1000;
                width = 1000;
            }
            imageArea.setPreserveRatio(false);
            imageArea.setFitWidth(width);
            imageArea.setFitHeight(height);
            imageArea.setImage(source);
        } else {
            source = new Image(
                    CreatePictureDialog.class.getResourceAsStream("/graphics/buddy.jpg"));
        }
        setTextButton.setOnAction(new ShowButtonHandler(theVers, source));


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


        return dialog.showAndWait();
    }

    private static Color convertColor(javafx.scene.paint.Color fx) {
        return  new Color((float) fx.getRed(),
                (float) fx.getGreen(),
                (float) fx.getBlue(),
                (float) fx.getOpacity());
    }

    public static void saveToFile(Image image) {
        File outputFile = new File(SynchThread.appDir, UUID.randomUUID().toString() + ".png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                                convertColor(picker.getValue()),
                                fontSizeBox.getValue(),
                                bImage);
                if (result.isPresent()) {
                    Image fxResult = SwingFXUtils.toFXImage(result.get(), null);
                    imageArea.setImage(fxResult);
                }
            }
        }
    }
}
