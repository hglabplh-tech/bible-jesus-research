package org.harry.jesus;

import javafx.application.Application;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.harry.jesus.fxutils.SetLinkEvent;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.synchjeremia.ApplicationProperties;
import org.harry.jesus.synchjeremia.BibleThreadPool;
import org.harry.jesus.synchjeremia.SynchThread;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;


/**
 * JavaFX App
 */
public class BibleStudy extends Application {

    private static Scene scene;

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