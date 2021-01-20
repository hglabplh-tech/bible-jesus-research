package org.harry.jesus.fxutils.controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import org.harry.jesus.fxutils.graphics.ImageMaker;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.jesajautils.graphicsjaxb.VerseImageData;
import org.harry.jesus.synchjeremia.BibleThreadPool;
import org.harry.jesus.synchjeremia.SynchThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowPictureControl extends StackPane {

    ListView<VerseImageData> graphicsList  = null;

    List<Image> imageList = new ArrayList<>();

    public ShowPictureControl() {
        this.setMinHeight(700);
        this.setMinWidth(900);
        createAndSetPictureList();
        if (graphicsList != null) {
            BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();

            for (VerseImageData data: context.getVerseImages().getVerseImageMapping().values()) {
                graphicsList.getItems().add(0, data);
                imageList.add(0, getImageByName(data.getImagePath()));
            }
        }
    }

    private void createAndSetPictureList() {
        graphicsList = new ListView<>();
        ContextMenu contMenu = new ContextMenu();
        MenuItem mItem = new MenuItem();
        mItem.setText("Copy verse-image");
        contMenu.getItems().add(mItem);
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = graphicsList.getSelectionModel().getSelectedIndex();
                if ((index >= 0) && (index < graphicsList.getItems().size())) {
                    Image image = imageList.get(index);
                    Clipboard clipBoard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(image);
                    clipBoard.setContent(content);
                }

            }
        });

        graphicsList.setContextMenu(contMenu);
        graphicsList.setCellFactory(param -> new ListCell<VerseImageData>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(VerseImageData data, boolean empty) {
                super.updateItem(data, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Image image = getImageByName(data.getImagePath());
                    Tuple<Integer, Integer> widthHeightTuple =
                            ImageMaker.getZoomValues(image, 400);
                    imageView.setPreserveRatio(false);
                    imageView.setFitWidth(widthHeightTuple.getFirst());
                    imageView.setFitHeight(widthHeightTuple.getSecond());
                    imageView.setImage(image);
                    setText(data.getVerseLink());
                    setGraphic(imageView);
                }
            }
        });
        this.getChildren().add(graphicsList);
    }

    private Image getImageByName(String fPath) {
        try {
            File imgFile = new File(fPath);
            FileInputStream stream= new FileInputStream(imgFile);
            return new Image(stream);
        } catch (IOException ex) {
            return null;
        }
    }
}
