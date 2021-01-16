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
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.jesajautils.browse.TextStyle;
import org.harry.jesus.jesajautils.configjaxbser.BaseConfig;
import org.harry.jesus.jesajautils.configjaxbser.BibleAppConfig;
import org.harry.jesus.synchjeremia.ApplicationProperties;
import org.harry.jesus.synchjeremia.BibleThreadPool;
import org.harry.jesus.synchjeremia.SynchThread;

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

    BibleAppConfig config = null;

    public SettingsDialog() {

    }

    public void showAppSettingsDialog() {
        // Create the custom dialog.
        BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();
        config = context.getAppSettings();
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
        Label accordanceDirLab = new Label("Bible Dictionary Source Directory:");
        TextField accordanceField = new TextField();
        Label mediaDirLab = new Label("Play Bible Directory:");
        TextField mediaDirField = new TextField();

        String xmlPath = config.getBaseConfig().getBiblesDir();
        bibleDirField.setText(xmlPath);

        String audioPath = config.getBaseConfig().getMediaPath();
        mediaDirField.setText(audioPath);
        String dictPath = config.getBaseConfig().getMediaPath();
        accordanceField.setText(dictPath);
        Button getMediaDirButton = new Button("Get Media Directory");
        Button getBibleDirButton = new Button("Get Bible Directory");
        Button getAccordanceDirButton = new Button("Get Dictionary Directory");
        getBibleDirButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Optional<String> path = JesusMisc.showDirectorySelector(dialog.getDialogPane());
                if (path.isPresent()) {
                    String biblePath = path.get();
                    bibleDirField.setText(biblePath);
                    BibleThreadPool.getContext().setAppSettings(config);
                    SynchThread.storeApplicationSettings(context);
                }
            }
        });
        getAccordanceDirButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Optional<String> path = JesusMisc.showDirectorySelector(dialog.getDialogPane());
                if (path.isPresent()) {
                    String biblePath = path.get();
                    accordanceField.setText(biblePath);
                    BibleThreadPool.getContext().setAppSettings(config);
                    SynchThread.storeApplicationSettings(context);
                }
            }
        });
        getMediaDirButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Optional<String> path = JesusMisc.showDirectorySelector(dialog.getDialogPane());
                if (path.isPresent()) {
                    String selectedPath = path.get();
                    mediaDirField.setText(selectedPath);
                    BibleThreadPool.getContext().setAppSettings(config);
                    SynchThread.storeApplicationSettings(context);
                }
            }
        });

        /**
         * set the font box
         */
        Label fontLabel = new Label("Select default Font");
        ChoiceBox<String> fontBox = makeFontBox();
        ChoiceBox<Integer> fontSizeBox = getFontSizeChoiceBox();
        fontSizeBox.getSelectionModel().select(ApplicationProperties.getFontSize());
        Label shapeLabel = new Label("Select default Shape");
        ChoiceBox<BaseConfig.ShapeEnum> shapeBox = makeShapeBox();
        FoldableStyledArea area = new FoldableStyledArea();
        area.setMaxSize(140, 35);
        area.replaceText("Jesus loves you");
        grid.add(bibleDirLab, 0, 0);
        grid.add(bibleDirField, 1, 0);
        grid.add(getBibleDirButton, 2, 0);
        grid.add(accordanceDirLab, 0, 1);
        grid.add(accordanceField, 1, 1);
        grid.add(getAccordanceDirButton, 2, 1);
        grid.add(mediaDirLab, 0, 2);
        grid.add(mediaDirField, 1, 2);
        grid.add(getMediaDirButton, 2, 2);
        grid.add(fontLabel, 0, 3);
        grid.add(fontBox, 1, 3);
        grid.add(fontSizeBox, 2, 3);
        grid.add(shapeLabel, 0, 4);
        grid.add(shapeBox, 1, 4);
        grid.add(area, 2, 4);
        setPreview(area, ApplicationProperties.getShape(),
                ApplicationProperties.getFontFamily().get(),
                ApplicationProperties.getFontSize());

        dialog.getDialogPane().setContent(grid);

// Request focus on the passwordKey field by default.
        Platform.runLater(() -> mediaDirField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String font = fontBox.getSelectionModel().getSelectedItem();
                BaseConfig.ShapeEnum shape = shapeBox.getSelectionModel().getSelectedItem();
                Integer fontSize = fontSizeBox.getValue();
                config.getBaseConfig().setFontFamily(font);
                config.getBaseConfig().setReaderShape(shape);
                config.getBaseConfig().setFontSize(fontSize);
                context.setAppSettings(config);
                SynchThread.storeApplicationSettings(context);
            }
            return 0;
        });

        shapeBox.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        BaseConfig.ShapeEnum selected = shapeBox.getItems().get(newValue.intValue());
                        TextStyle baseShape = BaseConfig.getShapeMap().get(selected);
                        setPreview(area, baseShape,
                                fontBox.getValue(), fontSizeBox.getValue());
                    }

                });

        fontBox.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        BaseConfig.ShapeEnum shape = shapeBox.getValue();
                        String fontFamily = fontBox.getSelectionModel().getSelectedItem();
                        TextStyle baseShape = BaseConfig
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
                        BaseConfig.ShapeEnum shape = shapeBox.getValue();
                        Integer fontSize = fontSizeBox.getSelectionModel().getSelectedItem();
                        TextStyle baseShape = BaseConfig
                                .getShapeMap().get(shape);
                        setPreview(area, baseShape,
                                fontBox.getValue(), fontSize);
                    }

                });

        dialog.showAndWait();

    }

    private ChoiceBox<Integer> getFontSizeChoiceBox() {
        ChoiceBox<Integer> fontSizeBox = new ChoiceBox<>();
        fontSizeBox.getItems().add(8);
        fontSizeBox.getItems().add(10);
        fontSizeBox.getItems().add(12);
        fontSizeBox.getItems().add(14);
        fontSizeBox.getItems().add(16);
        fontSizeBox.getItems().add(18);
        return fontSizeBox;
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

    private ChoiceBox<BaseConfig.ShapeEnum> makeShapeBox() {
        ChoiceBox<BaseConfig.ShapeEnum> box = new ChoiceBox<>();
        box.getItems().addAll(BaseConfig.ShapeEnum.values());
        box.getSelectionModel().select(config.getBaseConfig().getReaderShape());
        return box;
    }

    public static TextStyle setPreview(FoldableStyledArea area, TextStyle style,
                            String font, Integer fontSize) {

        return setPreview(area, style, font, fontSize, null);
    }

    public static TextStyle setPreview(FoldableStyledArea area, TextStyle style,
                                       String font, Integer fontSize, IndexRange range) {
        String text = area.getText();
        TextStyle tempStyle = style;
        Color backColor = tempStyle.getBackgroundColor().orElse(Color.WHITE);
        BackgroundFill fill = new BackgroundFill(backColor, CornerRadii.EMPTY, Insets.EMPTY);
        Background backGround = new Background(fill);
        area.setBackground(backGround);
        tempStyle = tempStyle.updateFontFamily(font).updateFontSize(fontSize);
        if (range == null) {
            area.setStyle(0, text.length(), tempStyle);
        } else {
            area.setStyle(range.getStart(), range.getEnd(), tempStyle);
        }
        return tempStyle;
    }
}
