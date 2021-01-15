package org.harry.jesus.fxutils;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import org.harry.jesus.BibleStudy;
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
public class ReadLinksDialog {

    public static FXMLLoader fxmlLoader = null;

    /**
     * This method creates and calls the dialog to define a production place of a signature

     *
     */
    public static void showReadLinkDialog(BibleTextUtils utils,
                                          FoldableStyledArea area, String htmlText) {

        Stage stage = new Stage();
        stage.setTitle("Read Links");
        try {
            Scene secondScene = new Scene(loadFXML("linkReader", utils, area, htmlText));
            stage.setScene(secondScene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.trace("Cannot load reason", ex.getMessage());
        }
    }

    public static Parent loadFXML(String fxml, BibleTextUtils utils, FoldableStyledArea area,
                                  String htmlText) throws IOException {
        URL resourceURL = BibleStudy.class.getResource("/fxml/" + fxml + ".fxml");
        fxmlLoader = new FXMLLoader(resourceURL);

        Pane root = (Pane) fxmlLoader.load();
        LinkReaderController controller = (LinkReaderController)fxmlLoader.getController();
        controller.setWebViewListener(utils, area, htmlText);
        return root;
    }
}
