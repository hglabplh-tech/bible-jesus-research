package org.harry.jesus.jesajautils.httpSrv.context;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import generated.XMLBIBLE;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.harry.jesus.fxutils.LinkHandler;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.HTMLRendering;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.jesajautils.httpSrv.HttpSrvUtils;
import org.harry.jesus.jesajautils.judaerrmsg.BibleStudyException;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The type Retrieve chapter handler.
 */
public class RetrieveChapterHandler implements HttpHandler {

    private final XMLBIBLE bible;

    /**
     * Instantiates a new Retrieve chapter handler.
     *
     * @param bible the bible
     */
    public RetrieveChapterHandler(XMLBIBLE bible)  {
        this.bible = bible;
    }
    @Override
    public void handle(HttpExchange httpExchange)  {
        try {
            URI reqURI = httpExchange.getRequestURI();
            Tuple<XMLBIBLE, BibleTextUtils.BookLink> link = LinkHandler.getRealBibleLink(reqURI);
            String html = HTMLRendering.renderFullChapter(BibleTextUtils.getInstance(),
                    link.getFirst(),
                    Arrays.asList(link.getSecond()));
            HttpSrvUtils.getLengthAndSendText(httpExchange, html);
        } catch (IOException | BibleStudyException ex) {
            HttpSrvUtils.getLengthAndSendException(httpExchange, ex);
        }

    }


}
