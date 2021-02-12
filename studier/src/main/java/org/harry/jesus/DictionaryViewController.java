package org.harry.jesus;


import generated.XMLBIBLE;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.codefx.libfx.control.webview.WebViews;

import org.harry.jesus.fxutils.WebViewerHyperListener;
import org.harry.jesus.fxutils.JScriptWebViewUtils;
import org.harry.jesus.fxutils.event.SearchDictEvent;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;


import javax.swing.event.HyperlinkEvent;
import javax.swing.plaf.nimbus.State;

/**
 * The type Accordance view controller.
 */
public class DictionaryViewController {


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
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    // note next classes are from org.w3c.dom domain
                    EventListener listener = new EventListener() {
                        public void handleEvent(Event ev) {

                            String href = ((Element)ev.getTarget()).getAttribute("href");
                            if (!href.startsWith("#")) {
                                ev.preventDefault();
                                WebEngine chapterEngine = chapterViewer.getEngine();
                                chapterEngine.load(href);
                            } else {
                                String htmlLink = href.substring(1);
                                engine.setJavaScriptEnabled(true);
                                JScriptWebViewUtils.findString(engine, "(" + htmlLink + ")");
                                searchInput.setText(htmlLink);
                                searchInput.fireEvent(new ActionEvent(searchInput, null));
                            }
                        }
                    };

                    Document doc = engine.getDocument();
                    NodeList lista = doc.getElementsByTagName("a");
                    for (int i=0; i<lista.getLength(); i++)
                        ((EventTarget)lista.item(i)).addEventListener("click",  listener, false);
                }
            }
            });

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
