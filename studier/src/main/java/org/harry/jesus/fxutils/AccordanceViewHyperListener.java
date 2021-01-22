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
import org.harry.jesus.jesajautils.LinkHandler;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;


import javax.swing.event.HyperlinkEvent;
import java.net.URL;
import java.util.List;

import static org.harry.jesus.fxutils.WebViewHyperListener.getRealBibleLink;

public class AccordanceViewHyperListener implements WebViewHyperlinkListener {



    private final FoldableStyledArea area;

    private final WebView view;

    private final WebView chapterView;

    private final XMLBIBLE selected;


    public AccordanceViewHyperListener(FoldableStyledArea area,
                                       WebView view, WebView chapterView, XMLBIBLE selected) {

        this.area = area;
        this.view = view;
        this.chapterView = chapterView;
        this.selected = selected;
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
        String htmlLink = href.substring("http://_self/#".length());
        WebEngine engine = this.chapterView.getEngine();
        engine.setJavaScriptEnabled(true);
        JScriptWebViewUtils.findString(engine, "(" + htmlLink + ")");
        System.out.println(href);
    }

    private void gotoBibleLink(String href) {
        String bibleLink = getRealBibleLink(href);
        try {
            BibleTextUtils utils = BibleTextUtils.getInstance();
            System.out.println(bibleLink);
            List<BibleTextUtils.BookLink> links = LinkHandler.parseLinks(utils, bibleLink);
            BibleTextUtils.BookLink link = links.get(0);
            SetLinkEvent event = new SetLinkEvent(link);
            utils.setSelected(selected);
            this.area.fireEvent(event);
            chapterView.getEngine().loadContent(
                    HTMLRendering.renderFullChapter(utils, utils.getSelected(), links));
            StringBuffer linkBuffer = new StringBuffer();
            HTMLRendering.buildLinkInternal(Integer.toString(link.getVerses().get(0)),
                    linkBuffer);
            chapterView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener< Worker.State >() {

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
