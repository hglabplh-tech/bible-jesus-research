package org.harry.jesus.fxutils;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.harry.jesus.fxutils.controls.ShowPictureControl;

/**
 * The type Show pic gallery.
 */
public class ShowPicGallery {

    /**
     * Show the verse pictures.
     */
    public static void showTheVersePictures() {
        Stage stage = new Stage();
        stage.setTitle("Verse Picture View");
        VBox topWin = new VBox();
        topWin.getChildren().add(new ShowPictureControl());
        Scene secondScene = new Scene(topWin);
        stage.setScene(secondScene);
        stage.show();
    }
}
