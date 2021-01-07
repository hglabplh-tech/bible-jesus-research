package org.harry.jesus;

import javafx.application.Application;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.harry.jesus.fxutils.SetLinkEvent;
import org.harry.jesus.synchjeremia.ApplicationProperties;
import org.harry.jesus.synchjeremia.BibleThreadPool;
import org.harry.jesus.synchjeremia.SynchThread;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;


import java.io.IOException;
import java.net.URL;


/**
 * JavaFX App
 */
public class BibleStudy extends Application {

    public static EventType<SetLinkEvent> SET_LINK_EVENT = new EventType<>("SET_LINK_EVENT");
    private static Scene scene;

    public static FXMLLoader fxmlLoader = null;



    public static void setRoot(String fxml, CSS css) throws IOException {
        scene.setRoot(loadFXML(fxml, css));
}

    @Override
    public void start(Stage stage) throws IOException {
        Configurator.defaultConfig().level(Level.TRACE).writer(new ConsoleWriter()).activate();

        stage.setOnCloseRequest(event -> {
            System.out.println("Stage is closing");
            SynchThread.storeRendering(BibleThreadPool.getContext());
            SynchThread.storeNotes(BibleThreadPool.getContext());
            SynchThread.storeHighlights(BibleThreadPool.getContext());
            ApplicationProperties.storeApplicationProperties();
            System.exit(0);
        });
        scene = new Scene(loadFXML("main", CSS.BIBLE));
        stage.setScene(scene);
        stage.show();
    }


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




    public static void main(String[] args) {
        launch();
    }



    public static enum CSS {

        BIBLE(BibleStudy.class.getResource("/fxml/biblestudy.css").toExternalForm()),
        ;



        private String url;

        CSS(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

}