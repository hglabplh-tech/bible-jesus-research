package org.harry.jesus;

import generated.XMLBIBLE;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.codefx.libfx.control.webview.WebViews;
import org.harry.jesus.fxutils.AccordanceViewHyperListener;
import org.harry.jesus.fxutils.JScriptWebViewUtils;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;

import javax.swing.event.HyperlinkEvent;

public class AccordanceViewController {


    @FXML
    TextField searchInput;

    @FXML
    StackPane stackPane;

    @FXML
    WebView konkordanzViewer;

    @FXML
    public void initialize() {

        VBox.setVgrow(stackPane, Priority.ALWAYS);


    }

    public void setWebViewListener(BibleTextUtils utils, FoldableStyledArea area, String html, XMLBIBLE selected) {
        WebEngine engine = konkordanzViewer.getEngine();
        engine.setJavaScriptEnabled(true);
        engine.loadContent(html);
        area.setLinkedSearchTextField(searchInput);
        area.setLinkedWebEngine(konkordanzViewer.getEngine());
        WebViews.addHyperlinkListener(konkordanzViewer,
                new AccordanceViewHyperListener(utils, area, konkordanzViewer, selected), HyperlinkEvent.EventType.ACTIVATED);

    }

    public void closeWin(ActionEvent event) {

    }

    @FXML
    public void search(ActionEvent event) {
        String searchText = searchInput.getText();
        JScriptWebViewUtils.findString(konkordanzViewer.getEngine(), searchText);
    }

    public static class Controller {

    }
}
