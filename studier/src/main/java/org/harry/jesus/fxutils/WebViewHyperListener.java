package org.harry.jesus.fxutils;

import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.LinkHandler;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;


import javax.swing.event.HyperlinkEvent;
import java.net.URL;
import java.util.List;

public class WebViewHyperListener implements WebViewHyperlinkListener {

    private final BibleTextUtils utils;

    private final FoldableStyledArea area;



    public WebViewHyperListener(BibleTextUtils utils, FoldableStyledArea area) {
        this.utils = utils;
        this.area = area;
    }
    @Override
    public boolean hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        URL url = hyperlinkEvent.getURL();
        String href = url.toString();
        href = getRealBibleLink(href);
        try {
            List<BibleTextUtils.BookLink> links = LinkHandler.parseLinks(utils, href);
            BibleTextUtils.BookLink link = links.get(0);
            SetLinkEvent event = new SetLinkEvent(link);
            this.area.fireEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getRealBibleLink(String href) {
        href = href.replace("http://bible/", "");
        href = href.replace("\\", " ");
        href = href.replace("opstart", "[");
        href = href.replace("opend", "]");
        href = href.replace("/vers/", ",");
        return href;
    }
}
