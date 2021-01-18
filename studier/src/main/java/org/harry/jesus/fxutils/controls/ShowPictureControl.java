package org.harry.jesus.fxutils.controls;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.harry.jesus.fxutils.graphics.ImageMaker;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.synchjeremia.SynchThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

public class ShowPictureControl extends StackPane {

    ListView<String> graphicsList  = null;

    public ShowPictureControl() {
        this.setMinHeight(700);
        this.setMinWidth(900);
        createAndSetPictureList();
        if (graphicsList != null) {
            File dir = SynchThread.appDir;
            File[] paths = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".png")) {
                        return true;
                    }
                    return false;
                }
            });
            for (File path : paths) {
                graphicsList.getItems().add(path.getAbsolutePath());
            }
        }
    }

    private void createAndSetPictureList() {
        graphicsList = new ListView<>();
        graphicsList.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Image image = getImageByName(name);
                    Tuple<Integer, Integer> widthHeightTuple =
                            ImageMaker.getZoomValues(image, 200);
                    imageView.setPreserveRatio(false);
                    imageView.setFitWidth(widthHeightTuple.getFirst());
                    imageView.setFitHeight(widthHeightTuple.getSecond());
                    imageView.setImage(image);
                    setText(name);
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
