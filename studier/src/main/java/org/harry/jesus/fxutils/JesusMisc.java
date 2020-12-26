package org.harry.jesus.fxutils;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.pmw.tinylog.Logger;

import java.io.*;

public class JesusMisc {
    public static OutputStream showSaveDialog(ActionEvent event, Node node) {
        FileChooser fDialog = new FileChooser();
        fDialog.setTitle("Select Path");
        File currentDir = new File(System.getProperty("user.home", "C:\\")).getAbsoluteFile();

        fDialog.setInitialDirectory(currentDir);
        Window parent = node.getScene().getWindow();
        File file = fDialog.showSaveDialog(parent);
        if (file != null) {
            try {
                FileOutputStream stream = new FileOutputStream(file);
                return stream;
            } catch (IOException ex) {
                Logger.trace("Save file not there error : " + ex.getMessage());
                return null;
            }
        }
        return null;
    }

    public static InputStream showOpenDialog(ActionEvent event, Node node) {
        FileChooser fDialog = new FileChooser();
        fDialog.setTitle("Select Path");
        File currentDir = new File(System.getProperty("user.home", "C:\\")).getAbsoluteFile();

        fDialog.setInitialDirectory(currentDir);
        Window parent = node.getScene().getWindow();
        File file = fDialog.showOpenDialog(parent);
        try {
            if (file != null) {
                return new FileInputStream(file);
            }
        } catch (IOException ex) {
            Logger.trace("Save file not there error : " + ex.getMessage());
            return null;
        }
        return null;
    }
}
