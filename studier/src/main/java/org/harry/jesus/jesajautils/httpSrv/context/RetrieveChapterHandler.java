package org.harry.jesus.jesajautils.httpSrv.context;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import generated.XMLBIBLE;
import org.apache.http.client.utils.URIBuilder;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.harry.jesus.MainController;
import org.harry.jesus.fxutils.LinkHandler;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.HTMLRendering;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.jesajautils.httpSrv.BibleHTTPSrv;
import org.harry.jesus.jesajautils.httpSrv.HttpSrvUtils;
import org.harry.jesus.jesajautils.judaerrmsg.BibleStudyException;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
            if (ex instanceof BibleStudyException) {
                BibleStudyException studyEx = (BibleStudyException)ex;
                if (studyEx.getCode().equals(BibleStudyException.Code.BIBLE_NOT_FOUND)) {
                    try {
                        HttpSrvUtils.getLengthAndSendText(httpExchange,  bibleNotFoundPage());
                    } catch (Exception e) {
                        HttpSrvUtils.getLengthAndSendException(httpExchange, e);
                    }
                } else if (studyEx.getCode().equals(BibleStudyException.Code.BOOK_NOT_FOUND)) {
                    Map<String, String> parameters = LinkHandler.splitURIQuery(httpExchange.getRequestURI());
                    String bibleId = parameters.get(LinkHandler.BIBLE_PARAM);
                    try {
                        HttpSrvUtils.getLengthAndSendText(httpExchange,  bookNotFoundPage(bibleId));
                    } catch (IOException e) {
                        HttpSrvUtils.getLengthAndSendException(httpExchange, e);
                    }
                } else {
                    HttpSrvUtils.getLengthAndSendException(httpExchange, ex);
                }
            } else {
                HttpSrvUtils.getLengthAndSendException(httpExchange, ex);
            }
        }

    }

    private String bibleNotFoundPage () throws URISyntaxException {
        StringBuffer htmlBuffer = new StringBuffer();
        HTMLRendering.buildHead(htmlBuffer);
        for (BibleTextUtils.BibleBookInstance instance: BibleTextUtils.getInstance().getBibleInstances()) {
            String bibleId = instance.getBibleRef().getBibleID();
            String bibleName = instance.getBibleRef().getBiblename();
            URI uri = new URIBuilder("http://localhost:"+ BibleHTTPSrv.getPORT() + "/retrieveChapter?")
                    .setParameter(LinkHandler.BIBLE_PARAM, bibleId).build();
            htmlBuffer.append("<br>");
            String text = bibleId + "||" + bibleName;
            String linkText = "<a href=\"" + uri.toString() + "\" target=\"_blank\">" + text + "</a>";
            htmlBuffer.append(linkText);
        }
        HTMLRendering.buildFoot(htmlBuffer);
        return htmlBuffer.toString();
    }

    private String bookNotFoundPage(String bibleId) {
        StringBuffer htmlBuffer = new StringBuffer();
        HTMLRendering.buildHead(htmlBuffer);
        htmlBuffer.append("<form action=\"http://localhost:" + BibleHTTPSrv.getPORT()
                + "/retrieveChapter?bible=" + bibleId +"\">\n" +
                "<div>\n" +
                " <label for=\"bible\">Bible Book:</label><br>\n" +
                "  <input type=\"text\" id=\"bible\" value=\"" + bibleId + "\" name=\"" + LinkHandler.BIBLE_PARAM + "\">\n" +
                "</div>\n" +
                "<div>\n" +
                " <label for=\"book\">Book Name:</label><br>\n" +
                "  <input type=\"text\" id=\"book\" name=\"" + LinkHandler.BOOK_PARAM + "\">\n" +
                "</div>\n" +
                "<div>\n" +
                "  <label for=\"chapter\">Chapter Number</label>\n" +
                "  <input type=\"\" id=\"chapter\" name=\"" + LinkHandler.CHAPTERNO_PARAM + "\">\n" +
                "</div>\n"  +
                "<div>\n" +
                "  <label for=\"verse\">Verse Number</label>\n" +
                "  <input type=\"\" id=\"verse\" name=\"" + LinkHandler.VERSENO_PARAM + "\">\n" +
                "</div>\n"  +
                "<div>\n" +
                "  <input type=\"submit\" value=\"Show\">" +
                "</div>\n" +
                " </form>"
        );
        HTMLRendering.buildFoot(htmlBuffer);
        return htmlBuffer.toString();
    }


}
