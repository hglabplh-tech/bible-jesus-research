package org.harry.jesus.fxutils;

import generated.XMLBIBLE;
import javafx.scene.control.IndexRange;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.HTMLRendering;
import org.harry.jesus.jesajautils.LinkHandler;
import org.harry.jesus.jesajautils.TextRendering;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;


import javax.swing.event.HyperlinkEvent;
import java.net.URL;
import java.util.List;

import static org.harry.jesus.fxutils.WebViewHyperListener.getRealBibleLink;

public class AccordanceViewHyperListener implements WebViewHyperlinkListener {

    private final BibleTextUtils utils;

    private final FoldableStyledArea area;

    private final WebView view;

    private final WebView chapterView;


    public AccordanceViewHyperListener(BibleTextUtils utils, FoldableStyledArea area,
                                       WebView view, WebView chapterView) {
        this.utils = utils;
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
        engine.executeScript("scrollTo('" + htmlLink + "')");
        System.out.println(href);
    }

    private void gotoBibleLink(String href) {
        String bibleLink = getRealBibleLink(href);
        try {
            System.out.println(bibleLink);
            List<BibleTextUtils.BookLink> links = LinkHandler.parseLinks(utils, bibleLink);
            BibleTextUtils.BookLink link = links.get(0);
            SetLinkEvent event = new SetLinkEvent(link);
            this.area.fireEvent(event);
            chapterView.getEngine().loadContent(
                    HTMLRendering.renderFullChapter(utils, utils.getSelected(), links));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
