package org.harry.jesus.fxutils;

import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.harry.jesus.fxutils.event.SetLinkEvent;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.LinkHandler;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.pmw.tinylog.Logger;


import javax.swing.event.HyperlinkEvent;
import java.net.URL;
import java.util.List;

/**
 * The type Web view hyper listener.
 */
public class WebViewHyperListener implements WebViewHyperlinkListener {

    private final BibleTextUtils utils;

    private final FoldableStyledArea area;


    /**
     * Instantiates a new Web view hyper listener.
     *
     * @param utils the utils
     * @param area  the area
     */
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
        } catch (Exception ex) {
            Logger.trace(ex);
            Logger.trace("cannot send Link Event");
        }
        return false;
    }


    /**
     * Gets real bible link.
     *
     * @param href the href
     * @return the real bible link
     */
    public static String getRealBibleLink(String href) {
        href = href.replace("http://bible/", "");
        href = href.replace("\\", " ");
        href = href.replace("opstart", "[");
        href = href.replace("opend", "]");
        href = href.replace("/vers/", ",");
        return href;
    }
}
