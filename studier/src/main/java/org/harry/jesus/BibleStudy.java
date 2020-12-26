package org.harry.jesus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;



/**
 * JavaFX App
 */
public class BibleStudy extends Application {

    private static Scene scene;

    public static FXMLLoader fxmlLoader = null;

    public static ThreadLocal<Properties> bookmarkLocal = null;

    public static void setRoot(String fxml, CSS css) throws IOException {
        scene.setRoot(loadFXML(fxml, css));
}

    @Override
    public void start(Stage stage) throws IOException {
        Configurator.defaultConfig().level(Level.TRACE).writer(new ConsoleWriter()).activate();

        scene = new Scene(loadFXML("main", CSS.BIBLE));
        stage.setScene(scene);
        synchronized (BibleStudy.class)  {
            if (bookmarkLocal == null) {
                bookmarkLocal = new ThreadLocal<>();
                bookmarkLocal.set(new Properties());
            }
        }
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



        return root;
    }




    public static void main(String[] args) {
        launch();
    }



    public static enum CSS {

        BIBLE("for future use")
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