package org.harry.jesus.fxutils;

import javafx.scene.control.IndexRange;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.harry.jesus.jesajautils.BibleTextUtils;
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

    public AccordanceViewHyperListener(BibleTextUtils utils, FoldableStyledArea area, WebView view) {
        this.utils = utils;
        this.area = area;
        this.view = view;
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
            TextRendering rendering = new TextRendering(utils, area, link.getBookLabel(), link.getChapter());
            rendering.render(utils.getBibleInstances().get(0).getBible(),
                    link.getBookLabel(), link.getChapter() );
            IndexRange range = rendering.getChapterMap().get(link.getVerses().get(0));
            rendering.selectVerseColorByGivenRange(range, Color.CORAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
