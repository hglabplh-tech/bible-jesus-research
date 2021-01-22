package org.harry.jesus.fxutils;

import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;

/**
 * The type Jesus misc.
 */
public class JesusMisc {
    /**
     * Show save dialog output stream.
     *
     * @param node    the node
     * @param fileExt the file ext
     * @return the output stream
     */
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

    /**
     * Show save dialog file optional.
     *
     * @param node    the node
     * @param fileExt the file ext
     * @return the optional
     */
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


    /**
     * Show open dialog input stream.
     *
     * @param node    the node
     * @param fileExt the file ext
     * @return the input stream
     */
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

    /**
     * Show open dialog file optional.
     *
     * @param node    the node
     * @param fileExt the file ext
     * @return the optional
     */
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


    /**
     * Show open dialog string string.
     *
     * @param node    the node
     * @param fileExt the file ext
     * @return the string
     */
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

    /**
     * Show directory selector optional.
     *
     * @param node the node
     * @return the optional
     */
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

    /**
     * The enum File extension.
     */
    public enum FileExtension {
        /**
         * The Html ext.
         */
        HTML_EXT(new FileChooser.ExtensionFilter("HTML doc(*.html)", "*.html")),
        /**
         * The Pdf ext.
         */
        PDF_EXT(new FileChooser.ExtensionFilter("PDF doc(*.pdf)", "*.pdf")),
        /**
         * The Xml ext.
         */
        XML_EXT(new FileChooser.ExtensionFilter("XML doc(*.xml)", "*.xml")),
        /**
         * The All ext.
         */
        ALL_EXT(new FileChooser.ExtensionFilter("ALL graphic doc(*.*)", "*.*"));
        private final FileChooser.ExtensionFilter thisFilter;

        FileExtension(FileChooser.ExtensionFilter extFilter) {
            thisFilter = extFilter;
        }

        /**
         * Gets this filter.
         *
         * @return the this filter
         */
        public FileChooser.ExtensionFilter getThisFilter() {
            return thisFilter;
        }
    }
}
