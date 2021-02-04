package org.harry.jesus;

import generated.XMLBIBLE;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import org.codefx.libfx.control.webview.WebViews;
import org.harry.jesus.fxutils.WebViewerHyperListener;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;

import javax.swing.event.HyperlinkEvent;

/**
 * The type Link reader controller.
 */
public class LinkReaderController {

    /**
     * The Link reader.
     */
    @FXML
    WebView linkReader;

    /**
     * Initialize.
     */
    @FXML
    public void initialize() {

    }

    /**
     * Sets web view listener.
     *
     * @param utils    the utils
     * @param area     the area
     * @param htmlText the html text
     */
    public void setWebViewListener(BibleTextUtils utils, FoldableStyledArea area, String htmlText) {
        XMLBIBLE selected = BibleTextUtils.getInstance().getSelected();
        WebViews.addHyperlinkListener(linkReader,
                new WebViewerHyperListener(area, linkReader,
                        null, selected), HyperlinkEvent.EventType.ACTIVATED);
        linkReader.getEngine().loadContent(htmlText);
    }

    /**
     * Close win.
     *
     * @param event the event
     */
    public void closeWin(ActionEvent event) {

    }

}
