package org.harry.jesus;


import generated.XMLBIBLE;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.codefx.libfx.control.webview.WebViews;

import org.harry.jesus.fxutils.AccordanceViewHyperListener;
import org.harry.jesus.fxutils.JScriptWebViewUtils;
import org.harry.jesus.fxutils.event.SearchDictEvent;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;


import javax.swing.event.HyperlinkEvent;

/**
 * The type Accordance view controller.
 */
public class AccordanceViewController {


    /**
     * The Search input.
     */
    @FXML
    TextField searchInput;


    /**
     * The Dictionary viewer.
     */
    @FXML
    WebView dictionaryViewer;

    /**
     * The Chapter viewer.
     */
    @FXML
    WebView chapterViewer;

    private XMLBIBLE selected = null;


    /**
     * Initialize.
     */
    @FXML
    public void initialize() {

        selected = BibleTextUtils.getInstance().getSelected();

        searchInput.addEventHandler(SearchDictEvent.SEARCH_DICT_EVENT,
                event -> {
                    searchInput.setText(event.getQuery());
                    JScriptWebViewUtils.findString(dictionaryViewer.getEngine(),
                            event.getQuery());
                });


    }


    /**
     * Sets web view listener.
     *
     * @param area the area
     * @param html the html
     */
    public void setWebViewListener(FoldableStyledArea area, String html) {
        WebEngine engine = dictionaryViewer.getEngine();
        engine.setJavaScriptEnabled(true);
        engine.loadContent(html);
        area.addLinkedSearchTextField(searchInput);
        WebViews.addHyperlinkListener(dictionaryViewer,
                new AccordanceViewHyperListener(area, dictionaryViewer,
                        chapterViewer, selected), HyperlinkEvent.EventType.ACTIVATED);

    }

    /**
     * Close win.
     *
     * @param event the event
     */
    public void closeWin(ActionEvent event) {

    }

    /**
     * Search.
     *
     * @param event the event
     */
    @FXML
    public void search(ActionEvent event) {
        String searchText = searchInput.getText();
        JScriptWebViewUtils.findString(dictionaryViewer.getEngine(), searchText);
    }

    /**
     * The type Controller.
     */
    public static class Controller {

    }
}
