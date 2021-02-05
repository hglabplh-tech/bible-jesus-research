package org.harry.jesus.jesajautils.httpSrv.context;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import generated.XMLBIBLE;
import org.harry.jesus.fxutils.LinkHandler;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.HTMLRendering;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.jesajautils.httpSrv.HttpSrvUtils;
import org.harry.jesus.jesajautils.judaerrmsg.BibleStudyException;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class RetrieveVerseHandler implements HttpHandler {

    private final XMLBIBLE bible;

    public RetrieveVerseHandler(XMLBIBLE bible)  {
        this.bible = bible;
    }
    @Override
    public void handle(HttpExchange httpExchange)  {
        try {
            URI reqURI = httpExchange.getRequestURI();
            Tuple<XMLBIBLE, BibleTextUtils.BookLink> link = LinkHandler.getRealBibleLink(reqURI);
            XMLBIBLE oldBible = BibleTextUtils.getInstance().getSelected();
            BibleTextUtils.getInstance().setSelected(link.getFirst());
            String html = HTMLRendering.renderLink(BibleTextUtils.getInstance(),
                    link.getFirst(),
                    Arrays.asList(link.getSecond()));
            BibleTextUtils.getInstance().setSelected(oldBible);
            HttpSrvUtils.getLengthAndSendText(httpExchange, html);
        } catch (IOException | BibleStudyException ex) {
            HttpSrvUtils.getLengthAndSendException(httpExchange, ex);
        }

    }


}
