package org.harry.jesus.fxutils;

import generated.XMLBIBLE;
import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.TextRendering;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.net.URL;
import java.util.List;

public class WebViewHyperListener implements WebViewHyperlinkListener {

    private final BibleTextUtils utils;

    private final FoldableStyledArea area;

    public WebViewHyperListener (BibleTextUtils utils, FoldableStyledArea area) {
        this.utils = utils;
        this.area = area;
    }
    @Override
    public boolean hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        URL url = hyperlinkEvent.getURL();
        String href = url.toString();
        href = getRealBibleLink(href);
        try {
            List<BibleTextUtils.BookLink> links = utils.parseLinks(href);
            BibleTextUtils.BookLink link = links.get(0);
            TextRendering rendering = new TextRendering(utils, area, link.getBookLabel(), link.getChapter());
            rendering.render(utils.getBibles().get(2),link.getBookLabel(), link.getChapter() );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @NotNull
    private String getRealBibleLink(String href) {
        href = href.replace("http://bible/", "");
        href = href.replace("\\", " ");
        href = href.replace("opstart", "[");
        href = href.replace("opend", "]");
        href = href.replace("/vers/", ",");
        return href;
    }
}
