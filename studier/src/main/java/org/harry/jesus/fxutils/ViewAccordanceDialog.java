package org.harry.jesus.fxutils;


import com.google.common.io.LineReader;
import generated.Dictionary;
import generated.XMLBIBLE;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.harry.jesus.BibleStudy;
import org.harry.jesus.AccordanceViewController;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.HTMLRendering;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.synchjeremia.ApplicationProperties;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Class for a Store Password Diialog to store passwords for sites.
 * @author Harald Glab-Plhak
 */
public class ViewAccordanceDialog {

    public static FXMLLoader fxmlLoader = null;

    /**
     * This method creates and calls the dialog to define a production place of a signature

     *
     */
    public static void showAccordanceDialog(BibleTextUtils utils,
                                            FoldableStyledArea area,
                                            String accFName, XMLBIBLE selected) {

        Stage stage = new Stage();
        stage.setTitle("Second Stage");
        try {
            String accDir = ApplicationProperties.getApplicationAccordanceDir();
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
                        if (line == null || line.isEmpty()) {
                            break;
                        }
                        htmlBuffer.append(line);
                    }
                } catch (IOException ex) {
                    htmlBuffer.append("<p><h1>Accordance not available yet</h1></p>");
                }
            } else {
                htmlBuffer.append("<p><h1>Accordance not available yet</h1></p>");
            }
            Scene secondScene = new Scene(loadFXML("accordanceViewer", utils,
                    area, htmlBuffer.toString(), selected));
            stage.setScene(secondScene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.trace("Cannot load reason", ex.getMessage());
        }
    }

    public static Parent loadFXML(String fxml,
                                  BibleTextUtils utils,
                                  FoldableStyledArea area,
                                  String html,
                                  XMLBIBLE selected) throws IOException {
        URL resourceURL = BibleStudy.class.getResource("/fxml/" + fxml + ".fxml");
        fxmlLoader = new FXMLLoader(resourceURL);

        Pane root = (Pane) fxmlLoader.load();
        AccordanceViewController controller = (AccordanceViewController)fxmlLoader.getController();
        controller.setWebViewListener(utils, area, html, selected);


        return root;
    }
}
