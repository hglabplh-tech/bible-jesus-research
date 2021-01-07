package org.harry.jesus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import org.codefx.libfx.control.webview.WebViews;
import org.harry.jesus.fxutils.WebViewHyperListener;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;

import javax.swing.event.HyperlinkEvent;

public class LinkReaderController {

    @FXML
    WebView linkReader;

    @FXML
    public void initialize() {

    }

    public void setWebViewListener(BibleTextUtils utils, FoldableStyledArea area, String htmlText) {
        WebViews.addHyperlinkListener(linkReader,
                new WebViewHyperListener(utils, area), HyperlinkEvent.EventType.ACTIVATED);
        linkReader.getEngine().loadContent(htmlText);
    }

    public void closeWin(ActionEvent event) {

    }

    public static class Controller {

    }
}
