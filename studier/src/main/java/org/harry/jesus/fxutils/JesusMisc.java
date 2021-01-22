package org.harry.jesus.fxutils;

import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;

public class JesusMisc {
    public static OutputStream showSaveDialog(Node node, FileExtension fileExt) {
        Optional<File> optFile = showSaveDialogFile(node, fileExt);
        if (optFile.isPresent()) {
            try {
                FileOutputStream stream = new FileOutputStream(optFile.get());
                return stream;
            } catch (IOException ex) {
                Logger.trace("Save file not there error : " + ex.getMessage());
                return null;
            }
        }
        return null;
    }

    public static Optional<File> showSaveDialogFile(Node node, FileExtension fileExt) {
        FileChooser fDialog = new FileChooser();
        fDialog.setTitle("Select Path");
        fDialog.getExtensionFilters()
                .addAll(Arrays.asList(fileExt.getThisFilter()));
        File currentDir = new File(System.getProperty("user.home", "C:\\")).getAbsoluteFile();

        fDialog.setInitialDirectory(currentDir);
        Window parent = node.getScene().getWindow();
        File file = fDialog.showSaveDialog(parent);
        if (file != null) {
            return Optional.of(file);
        }
        return Optional.empty();
    }


    public static InputStream showOpenDialog(Node node, FileExtension fileExt) {
        Optional<File> optFile = showOpenDialogFile(node,fileExt);
        try {
            if (optFile.isPresent()) {
                return new FileInputStream(optFile.get());
            }
        } catch (IOException ex) {
            Logger.trace("Save file not there error : " + ex.getMessage());
            return null;
        }
        return null;
    }

    public static Optional<File> showOpenDialogFile(Node node, FileExtension fileExt) {
        FileChooser fDialog = new FileChooser();
        fDialog.getExtensionFilters()
                .addAll(Arrays.asList(fileExt.getThisFilter()));
        fDialog.setTitle("Select Path");
        File currentDir = new File(System.getProperty("user.home", "C:\\")).getAbsoluteFile();

        fDialog.setInitialDirectory(currentDir);
        Window parent = node.getScene().getWindow();
        File file = fDialog.showOpenDialog(parent);
        if (file != null) {
            return Optional.of(file);
        }

        return Optional.empty();
    }


    public static String showOpenDialogString(Node node, FileExtension fileExt) {
        FileChooser fDialog = new FileChooser();
        fDialog.getExtensionFilters()
                .addAll(Arrays.asList(fileExt.getThisFilter()));
        fDialog.setTitle("Select Path");
        File currentDir = new File(System.getProperty("user.home", "C:\\")).getAbsoluteFile();

        fDialog.setInitialDirectory(currentDir);
        Window parent = node.getScene().getWindow();
        File file = fDialog.showOpenDialog(parent);
        if (file != null) {
            return file.getAbsolutePath();
        }
        try {
            File temp = File.createTempFile("temp", ".bin");
            return temp.getAbsolutePath();
        } catch (IOException ex) {
            return null;
        }

    }

    public static Optional<String> showDirectorySelector(Node node) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("JavaFX Projects");
        File defaultDirectory = new File(System.getProperty("user.home", "C:\\"));
        chooser.setInitialDirectory(defaultDirectory);
        Window parent = node.getScene().getWindow();
        File selectedDirectory = chooser.showDialog(parent);
        if (selectedDirectory != null) {
            Optional<String> selectedDirectoryOpt =
                    Optional.of(selectedDirectory.getAbsolutePath());
            return selectedDirectoryOpt;
        } else {
            return Optional.empty();
        }
    }

    public enum FileExtension {
        HTML_EXT(new FileChooser.ExtensionFilter("HTML doc(*.html)", "*.html")),
        PDF_EXT(new FileChooser.ExtensionFilter("PDF doc(*.pdf)", "*.pdf")),
        XML_EXT(new FileChooser.ExtensionFilter("XML doc(*.xml)", "*.xml")),
        ALL_EXT(new FileChooser.ExtensionFilter("ALL graphic doc(*.*)", "*.*"));
        private final FileChooser.ExtensionFilter thisFilter;

        FileExtension(FileChooser.ExtensionFilter extFilter) {
            thisFilter = extFilter;
        }

        public FileChooser.ExtensionFilter getThisFilter() {
            return thisFilter;
        }
    }
}
