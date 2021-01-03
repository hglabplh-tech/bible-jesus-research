package org.harry.jesus.fxutils;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.harry.jesus.BibleStudy;
import org.harry.jesus.KonkordanzViewController;
import org.harry.jesus.LinkReaderController;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * Class for a Store Password Diialog to store passwords for sites.
 * @author Harald Glab-Plhak
 */
public class ViewKonkordanzDialog {

    public static FXMLLoader fxmlLoader = null;

    /**
     * This method creates and calls the dialog to define a production place of a signature

     *
     */
    public static void showKonkordanzDialog(BibleTextUtils utils, FoldableStyledArea area) {

        Stage stage = new Stage();
        stage.setTitle("Second Stage");
        try {
            Scene secondScene = new Scene(loadFXML("konkordanzViewer", utils, area));
            stage.setScene(secondScene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.trace("Cannot load reason", ex.getMessage());
        }
    }

    public static Parent loadFXML(String fxml, BibleTextUtils utils, FoldableStyledArea area) throws IOException {
        URL resourceURL = BibleStudy.class.getResource("/fxml/" + fxml + ".fxml");
        fxmlLoader = new FXMLLoader(resourceURL);

        Pane root = (Pane) fxmlLoader.load();
        KonkordanzViewController controller = (KonkordanzViewController)fxmlLoader.getController();
        controller.setWebViewListener(utils, area);


        return root;
    }
}
