package org.harry.jesus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.harry.jesus.synchjeremia.BibleThreadPool;
import org.harry.jesus.synchjeremia.SynchThread;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * JavaFX App
 */
public class BibleStudy extends Application {

    public static final Map<String, Node> windowsMap = new HashMap<>();
    private static Scene scene;

    /**
     * The constant fxmlLoader.
     */
    public static FXMLLoader fxmlLoader = null;


    @Override
    public void start(Stage stage) throws IOException {
        Configurator.defaultConfig().level(Level.TRACE)
                .locale(Locale.ENGLISH)
                .writer(new ConsoleWriter())
                .writer(
                        new FileWriter(
                                new File(SynchThread.appDir, "application.log")
                                        .getAbsolutePath()));


        stage.setOnCloseRequest(event -> {
            System.out.println("Stage is closing");
            SynchThread.storeRendering(BibleThreadPool.getContext());
            SynchThread.storeHistory(BibleThreadPool.getContext());
            SynchThread.storeNotes(BibleThreadPool.getContext());
            SynchThread.storeHighlights(BibleThreadPool.getContext());
            SynchThread.storeApplicationSettings(BibleThreadPool.getContext());
            System.exit(0);
        });
        scene = new Scene(loadFXML("main", CSS.BIBLE));
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Load fxml parent.
     *
     * @param fxml the fxml
     * @param css  the css
     * @return the parent
     * @throws IOException the io exception
     */
    public static Parent loadFXML(String fxml, CSS css) throws IOException {
        URL resourceURL = BibleStudy.class.getResource("/fxml/" + fxml + ".fxml");
        fxmlLoader = new FXMLLoader(resourceURL);
        Pane root = (Pane) fxmlLoader.load();
       // if (root.getStylesheets().size() > 0) {
        //    root.getStylesheets().remove(0);
        //}
       //root.getStylesheets().add(css.getUrl());
        root.getStylesheets().add(css.getUrl());


        return root;
    }


    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        launch();
    }


    /**
     * The enum Css.
     */
    public static enum CSS {

        /**
         * Bible css.
         */
        BIBLE(BibleStudy.class.getResource("/fxml/biblestudy.css").toExternalForm()),
        ;



        private String url;

        CSS(String url) {
            this.url = url;
        }

        /**
         * Gets url.
         *
         * @return the url
         */
        public String getUrl() {
            return url;
        }
    }

}