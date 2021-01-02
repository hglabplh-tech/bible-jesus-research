package org.harry.jesus.fxutils;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import jesus.harry.org.versnotes._1.Note;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.jesajautils.browse.TextStyle;
import org.harry.jesus.synchjeremia.ApplicationProperties;
import org.harry.jesus.synchjeremia.BibleThreadPool;
import sun.nio.ch.AbstractPollArrayWrapper;

import java.io.File;
import java.util.Optional;

/**
 * This dialog is designed to set the main settings for this application
 *
 * @author Harald Glab-Plhak
 */
public class SettingsDialog {

    /**
     * This method creates and calls the dialog to define the settings for this application

     *
     */

    public SettingsDialog() {

    }

    public void showAppSettingsDialog() {
        // Create the custom dialog.
        BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();
        Dialog<Integer> dialog = new Dialog<>();
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

        String xmlPath = ApplicationProperties.getApplicationBiblesDir();
        bibleDirField.setText(xmlPath);

        String audioPath = ApplicationProperties.getApplicationMediaDir();
        mediaDirField.setText(audioPath);
        Button getMediaDirButton = new Button("Get Media Directory");
        Button getBibleDirButton = new Button("Get Media Directory");
        getBibleDirButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String biblePath = JesusMisc.showOpenDialogString(event, dialog.getDialogPane());
                biblePath = biblePath.substring(0, biblePath.lastIndexOf(File.separator));
                mediaDirField.setText(biblePath);
                ApplicationProperties.setApplicationBiblesDir(biblePath);
                ApplicationProperties.storeApplicationProperties();
            }
        });
        getMediaDirButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String path = JesusMisc.showOpenDialogString(event, dialog.getDialogPane());
                path = path.substring(0, path.lastIndexOf(File.separator));
                mediaDirField.setText(path);
                ApplicationProperties.setApplicationMediaDir(path);
                ApplicationProperties.storeApplicationProperties();
            }
        });

        /**
         * set the font box
         */
        Label fontLabel = new Label("Select default Font");
        ChoiceBox<String> fontBox = makeFontBox();
        ChoiceBox<Integer> fontSizeBox = new ChoiceBox<>();
        fontSizeBox.getItems().add(8);
        fontSizeBox.getItems().add(10);
        fontSizeBox.getItems().add(12);
        fontSizeBox.getItems().add(14);
        fontSizeBox.getItems().add(16);
        fontSizeBox.getItems().add(18);
        fontSizeBox.getSelectionModel().select(ApplicationProperties.getFontSize());
        Label shapeLabel = new Label("Select default Shape");
        ChoiceBox<String> shapeBox = makeShapeBox();
        FoldableStyledArea area = new FoldableStyledArea();
        area.setMaxSize(140, 35);
        area.replaceText("Jesus loves you");
        grid.add(bibleDirLab, 0, 0);
        grid.add(bibleDirField, 1, 0);
        grid.add(getBibleDirButton, 2, 0);
        grid.add(mediaDirLab, 0, 1);
        grid.add(mediaDirField, 1, 1);
        grid.add(getMediaDirButton, 2, 1);
        grid.add(fontLabel, 0, 2);
        grid.add(fontBox, 1, 2);
        grid.add(fontSizeBox, 2, 2);
        grid.add(shapeLabel, 0, 3);
        grid.add(shapeBox, 1, 3);
        grid.add(area, 2, 3);
        setPreview(area, ApplicationProperties.getShape(),
                ApplicationProperties.getFontFamily().get(),
                ApplicationProperties.getFontSize());

        dialog.getDialogPane().setContent(grid);

// Request focus on the passwordKey field by default.
        Platform.runLater(() -> mediaDirField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String font = fontBox.getSelectionModel().getSelectedItem();
                String shape = shapeBox.getSelectionModel().getSelectedItem();
                Integer fontSize = fontSizeBox.getValue();
                ApplicationProperties.setFontFamily(font);
                ApplicationProperties.setShape(shape);
                ApplicationProperties.setFontSize(fontSize);
                ApplicationProperties.storeApplicationProperties();
            }
            return 0;
        });

        shapeBox.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        String selected = shapeBox.getItems().get(newValue.intValue());
                        TextStyle baseShape = ApplicationProperties
                                .getShapeMap().get(selected);
                        setPreview(area, baseShape,
                                fontBox.getValue(), fontSizeBox.getValue());
                    }

                });

        fontBox.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        String shape = shapeBox.getValue();
                        String fontFamily = fontBox.getSelectionModel().getSelectedItem();
                        TextStyle baseShape = ApplicationProperties
                                .getShapeMap().get(shape);
                        setPreview(area, baseShape,
                                fontFamily, fontSizeBox.getValue());
                    }

                });

        fontSizeBox.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        String shape = shapeBox.getValue();
                        Integer fontSize = fontSizeBox.getSelectionModel().getSelectedItem();
                        TextStyle baseShape = ApplicationProperties
                                .getShapeMap().get(shape);
                        setPreview(area, baseShape,
                                fontBox.getValue(), fontSize);
                    }

                });

        dialog.showAndWait();

    }

    private ChoiceBox<String> makeFontBox() {
        ChoiceBox<String> box = new ChoiceBox<>();
        box.getItems().addAll(ApplicationProperties.fontFamilies);
        Optional<String> optFont = ApplicationProperties.getFontFamily();
        if (optFont.isPresent()) {
            box.getSelectionModel().select(optFont.get());
        }
        return box;
    }

    private ChoiceBox<String> makeShapeBox() {
        ChoiceBox<String> box = new ChoiceBox<>();
        box.getItems().addAll(ApplicationProperties.getShapeMap().keySet());
        box.getSelectionModel().select(ApplicationProperties.getShapeString());
        return box;
    }

    public static TextStyle setPreview(FoldableStyledArea area, TextStyle style,
                            String font, Integer fontSize) {
        String text = area.getText();
        TextStyle tempStyle = style;
        Color backColor = tempStyle.getBackgroundColor().orElse(Color.WHITE);
        BackgroundFill fill = new BackgroundFill(backColor, CornerRadii.EMPTY, Insets.EMPTY);
        Background backGround = new Background(fill);
        area.setBackground(backGround);
        tempStyle = tempStyle.updateFontFamily(font).updateFontSize(fontSize);
        area.setStyle(0, text.length(), tempStyle);
        return tempStyle;
    }
}
