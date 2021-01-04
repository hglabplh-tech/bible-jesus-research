package org.harry.jesus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.codefx.libfx.control.webview.WebViews;
import org.harry.jesus.fxutils.AccordanceViewHyperListener;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;

import javax.swing.event.HyperlinkEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class AccordanceViewController {



    @FXML
    StackPane stackPane;

    @FXML
    WebView konkordanzViewer;

    @FXML
    public void initialize() {

        VBox.setVgrow(stackPane, Priority.ALWAYS);


    }

    public void setWebViewListener(BibleTextUtils utils, FoldableStyledArea area, String html) {
        WebEngine engine = konkordanzViewer.getEngine();
        engine.setJavaScriptEnabled(true);
        engine.loadContent(html);

        WebViews.addHyperlinkListener(konkordanzViewer,
                new AccordanceViewHyperListener(utils, area, konkordanzViewer), HyperlinkEvent.EventType.ACTIVATED);

    }

    public void closeWin(ActionEvent event) {

    }

    public static class Controller {

    }
}
