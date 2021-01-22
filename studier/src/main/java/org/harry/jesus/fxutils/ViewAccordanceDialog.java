package org.harry.jesus.fxutils;


import com.google.common.io.LineReader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.harry.jesus.BibleStudy;
import org.harry.jesus.AccordanceViewController;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.synchjeremia.BibleThreadPool;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Class for a Store Password Diialog to store passwords for sites.
 *
 * @author Harald Glab-Plhak
 */
public class ViewAccordanceDialog {

    /**
     * The constant fxmlLoader.
     */
    public static FXMLLoader fxmlLoader = null;

    /**
     * This method creates and calls the dialog to define a production place of a signature
     *
     * @param area     the area control
     * @param accFName the file name of the dictionary
     */
    public static void showAccordanceDialog(FoldableStyledArea area,
                                            String accFName) {

        Stage stage = new Stage();
        stage.setTitle("Dictionary View");
        try {
            String accDir = BibleThreadPool.getContext()
                    .getAppSettings().getBaseConfig()
                    .getDictionariesDir();
            String fileName = accFName + ".html";
            File inFile = new File(accDir, fileName);
            StringBuffer htmlBuffer = new StringBuffer();
            if (inFile.exists()) {
                try {
                    FileInputStream inStream = new FileInputStream(inFile);
                    Readable readable = new InputStreamReader(inStream);
                    LineReader reader = new LineReader(readable);
                    while(true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        htmlBuffer.append(line);
                    }
                } catch (IOException ex) {
                    htmlBuffer.append("<p><h1>Accordance not available yet</h1></p>");
                }
            } else {
                GenDictHTMLScene.generateDictHTML(BibleTextUtils.getInstance(), new File(accDir));
                htmlBuffer.append("<p><h1>Accordance not available yet</h1></p>");
            }
            Scene secondScene = new Scene(loadFXML("accordanceViewer",
                    area, htmlBuffer.toString()));
            stage.setScene(secondScene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.trace("Cannot load reason", ex.getMessage());
        }
    }

    /**
     * Load fxml parent.
     *
     * @param fxml the fxml file
     * @param area the bible reader area
     * @param html the html content
     * @return the parent control
     * @throws IOException a I/O exception occured
     */
    public static Parent loadFXML(String fxml,
                                  FoldableStyledArea area,
                                  String html) throws IOException {
        URL resourceURL = BibleStudy.class.getResource("/fxml/" + fxml + ".fxml");
        fxmlLoader = new FXMLLoader(resourceURL);

        Pane root = (Pane) fxmlLoader.load();
        AccordanceViewController controller = (AccordanceViewController)fxmlLoader.getController();
        controller.setWebViewListener(area, html);


        return root;
    }
}
