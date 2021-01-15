package org.harry.jesus;


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
import org.harry.jesus.fxutils.SearchDictEvent;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;


import javax.swing.event.HyperlinkEvent;

public class AccordanceViewController {


    @FXML
    TextField searchInput;



    @FXML
    WebView dictionaryViewer;

    @FXML
    WebView chapterViewer;


    @FXML
    public void initialize() {



        searchInput.addEventHandler(SearchDictEvent.SEARCH_DICT_EVENT,
                event -> {
                    searchInput.setText(event.getQuery());
                    JScriptWebViewUtils.findString(dictionaryViewer.getEngine(),
                            event.getQuery());
                });


    }



    public void setWebViewListener(BibleTextUtils utils, FoldableStyledArea area, String html) {
        WebEngine engine = dictionaryViewer.getEngine();
        engine.setJavaScriptEnabled(true);
        engine.loadContent(html);
        area.addLinkedSearchTextField(searchInput);
        WebViews.addHyperlinkListener(dictionaryViewer,
                new AccordanceViewHyperListener(utils, area, dictionaryViewer, chapterViewer), HyperlinkEvent.EventType.ACTIVATED);

    }

    public void closeWin(ActionEvent event) {

    }

    @FXML
    public void search(ActionEvent event) {
        String searchText = searchInput.getText();
        JScriptWebViewUtils.findString(dictionaryViewer.getEngine(), searchText);
    }

    public static class Controller {

    }
}
