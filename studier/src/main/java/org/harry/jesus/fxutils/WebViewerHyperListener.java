package org.harry.jesus.fxutils;

import generated.XMLBIBLE;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.harry.jesus.fxutils.event.SetLinkEvent;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.HTMLRendering;
import org.harry.jesus.jesajautils.LinkDetector;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;


import javax.swing.event.HyperlinkEvent;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.harry.jesus.fxutils.LinkHandler.getRealBibleLink;

/**
 * The type Accordance view hyper listener.
 */
public class WebViewerHyperListener implements WebViewHyperlinkListener {



    private final FoldableStyledArea area;

    private final WebView view;

    private final WebView chapterView;


    /**
     * Instantiates a new Accordance view hyper listener.
     *
     * @param area        the area
     * @param view        the view
     * @param chapterView the chapter view
     * @param selected    the selected
     */
    public WebViewerHyperListener(FoldableStyledArea area,
                                  WebView view, WebView chapterView, XMLBIBLE selected) {

        this.area = area;
        this.view = view;
        this.chapterView = chapterView;

    }

    @Override
    public boolean hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        URL url = hyperlinkEvent.getURL();
        String href = url.toString();

        if (href.indexOf("#") > -1) {
            gotoHtmlLink(href);
        } else {
            gotoBibleLink(href);
        }
        return false;
    }

    private void gotoHtmlLink(String href) {
        String htmlLink = href.substring("http://_self/#".length());
        WebEngine engine = this.view.getEngine();
        engine.setJavaScriptEnabled(true);
        engine.executeScript("scrollTo('" + htmlLink + "')");
        System.out.println(href);
    }

    private void gotoChapterLink(String href) {
        if (this.chapterView != null) {
            String htmlLink = href.substring("http://_self/#" .length());
            WebEngine engine = this.chapterView.getEngine();
            engine.setJavaScriptEnabled(true);
            JScriptWebViewUtils.findString(engine, "(" + htmlLink + ")");
            System.out.println(href);
        }
    }

    private void gotoBibleLink(String href) {
        try {
            BibleTextUtils utils = BibleTextUtils.getInstance();
            Tuple<XMLBIBLE, BibleTextUtils.BookLink> bibleLink = getRealBibleLink(href);
            utils.setSelected(bibleLink.getFirst());
            BibleTextUtils.BookLink link = bibleLink.getSecond();
            SetLinkEvent event = new SetLinkEvent(link);
            this.area.fireEvent(event);
            if (chapterView != null) {
                chapterView.getEngine().loadContent(
                        HTMLRendering.renderFullChapter(utils, utils.getSelected(),
                                Arrays.asList(link)));
                StringBuffer linkBuffer = new StringBuffer();
                LinkHandler.buildLinkInternal(Integer.toString(link.getVerses().get(0)),
                        linkBuffer);
                chapterView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {


                    @Override
                    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                        if (newValue.equals(Worker.State.SUCCEEDED)) {
                            gotoChapterLink(linkBuffer.toString());
                        }
                    }
                });

                try {
                    gotoChapterLink(linkBuffer.toString());
                } catch (Exception ex) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
