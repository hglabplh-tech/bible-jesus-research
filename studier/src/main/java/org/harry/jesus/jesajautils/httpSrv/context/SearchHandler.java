package org.harry.jesus.jesajautils.httpSrv.context;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import generated.XMLBIBLE;
import org.harry.jesus.MainController;
import org.harry.jesus.fxutils.LinkHandler;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.BibleTextUtils.BibleBookInstance;
import org.harry.jesus.jesajautils.HTMLRendering;
import org.harry.jesus.jesajautils.configjaxbser.BibleRef;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.harry.jesus.jesajautils.fulltext.StatisticsCollector;
import org.harry.jesus.jesajautils.httpSrv.BibleHTTPSrv;
import org.harry.jesus.jesajautils.httpSrv.HttpSrvUtils;
import org.harry.jesus.jesajautils.judaerrmsg.BibleStudyException;

import javax.swing.text.html.Option;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * The type Search handler.
 */
public class SearchHandler {

    /**
     * The constant QUERY_PARAM.
     */
    public static final String QUERY_PARAM = "query";

    /**
     * The constant REL_PARAM.
     */
    public static final String REL_PARAM = "relevance";

    /**
     * The constant SEARCHOPT_PARAM.
     */
    public static final String SEARCHOPT_PARAM = "searchOption";

    /**
     * The type Search form.
     */
    public static class SearchForm implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String html = searchForm();
            HttpSrvUtils.getLengthAndSendText(httpExchange, html);
        }
    }

    /**
     * The type Search execute.
     */
    public static class SearchExecute implements HttpHandler {

        /**
         * The Selected.
         */
        XMLBIBLE selected = null;

        /**
         * The Query.
         */
        String query = null;

        /**
         * The Relevance.
         */
        Boolean relevance = false;

        /**
         * The S options.
         */
        MainController.SearchOptions sOptions = MainController.SearchOptions.SIMPLE;


        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                fillParameters(httpExchange.getRequestURI());
                StatisticsCollector collector = new StatisticsCollector();
                BibleFulltextEngine engine = new BibleFulltextEngine(selected, collector);
                if (sOptions.equals(MainController.SearchOptions.SIMPLE)) {
                    engine.searchPlain(query);
                } else if (sOptions.equals(MainController.SearchOptions.EXACT)) {
                    engine.searchPattern(query, BibleFulltextEngine.INSENSITIVE);
                } else {
                    engine.searchPatternFuzzy(query, BibleFulltextEngine.INSENSITIVE);
                }
                List<BibleFulltextEngine.BibleTextKey> verseKeys = new ArrayList<>();
                if (relevance) {
                    verseKeys = engine.retrieveResultsByRelevanz();
                } else {
                    verseKeys = engine.retrieveResultsByBookOrder();
                }
                String htmlResult = HTMLRendering.renderSearchResult(selected, query, verseKeys);
                HttpSrvUtils.getLengthAndSendText(httpExchange, htmlResult);
            } catch (Exception ex) {
                HttpSrvUtils.getLengthAndSendException(httpExchange, ex);
            }
        }

        private void fillParameters(URI uri) throws BibleStudyException {
            Map<String, String> params = LinkHandler.splitURIQuery(uri);
            String bible = params.get(LinkHandler.BIBLE_PARAM);
            if (bible == null) {
                throw new BibleStudyException(BibleStudyException.Code.BIBLE_NOT_FOUND, "none");
            }
            Optional<BibleBookInstance> bibleInst = BibleTextUtils.getInstance()
                    .getBibleInstances()
                    .stream().filter(e -> e.getBibleRef().getBibleID().equals(bible))
                    .findFirst();
            if (bibleInst.isPresent()) {
                selected = bibleInst.get().getBible();
            } else {
                throw new BibleStudyException(BibleStudyException.Code.BIBLE_NOT_FOUND, bible);
            }
            String query = params.get(QUERY_PARAM);
            if (query == null) {
                throw new BibleStudyException("No query String for search is given");
            }
            this.query = query;
            String relParam = params.get(REL_PARAM);
            if (relParam == null) {
                relevance = false;
            } else {
                relevance = true;
            }
            String searchOption = params.get(SEARCHOPT_PARAM);
            if (searchOption != null) {
                sOptions = MainController.SearchOptions.valueOf(searchOption);
            }
        }
    }

    private static String searchForm() {
        StringBuffer htmlBuffer = new StringBuffer();
        HTMLRendering.buildHead(htmlBuffer);
        htmlBuffer.append("<form action=\"http://localhost:" + BibleHTTPSrv.getPORT()  + "/searchExecute\">\n" +
                "<div>\n" +
                " <label for=\"query\">Search Query:</label><br>\n" +
                "  <input type=\"text\" id=\"query\" name=\"" + QUERY_PARAM + "\">\n" +
                "</div>\n" +
                "<div>\n" +
                "  <input type=\"checkbox\" id=\"relevance\" name=\"" + REL_PARAM + "\" value=\"relevance\">\n" +
                "  <label for=\"relevance\">Sort by relevance</label>\n" +
                "</div>\n" +
                "<div>\n" +
                "  <label for=\"bible\">Bible to search:</label>\n" +
                "  <select id=\"bible\" name=\"" + LinkHandler.BIBLE_PARAM + "\">\n" +
                biblesToSearchChoice() +
                "  </select>\n" +
                "</div>\n" +
                "<div>\n" +
                "  <label for=\"sOption\">Search Option:</label>\n" +
                "  <select id=\"sOption\" name=\"" + SEARCHOPT_PARAM + "\">\n" +
                "    <option value=\"" + MainController.SearchOptions.SIMPLE.toString() + "\" selected>Simple Search</option>\n" +
                "    <option value=\"" + MainController.SearchOptions.FUZZY.toString() + "\">Fuzzy Search</option>\n" +
                "    <option value=\"" + MainController.SearchOptions.EXACT.toString() + "\">Exact Search</option>    \n" +
                "  </select>\n" +
                "</div>\n" +
                "<div>\n" +
                "  <input type=\"submit\" value=\"Search\">" +
                "</div>\n" +
                " </form>"
        );
        HTMLRendering.buildFoot(htmlBuffer);
        return htmlBuffer.toString();
    }

    private static String biblesToSearchChoice () {
        StringBuffer htmlBuffer = new StringBuffer();
        String fmtSelected = "<option value=\"%s\" selected>%s</option>\n";
        String fmtNotSelected = "<option value=\"%s\">%s</option>\n";
        Map<BibleBookInstance, BibleRef> idMap = BibleTextUtils.getInstance().getBibleInstances().stream().collect(
                Collectors.toMap(Function.identity(),x -> x.getBibleRef()));
        for (BibleRef ref: idMap.values()) {
            if (ref.getBibleID().contains("ELB1905STR")) {
                htmlBuffer.append(String.format(fmtSelected,
                        ref.getBibleID(),
                        ref.getBibleID() + "||" + ref.getBiblename()));
            } else {
                htmlBuffer.append(String.format(fmtNotSelected,
                        ref.getBibleID(),
                        ref.getBibleID() + "||" + ref.getBiblename()));
            }
        }
        return htmlBuffer.toString();
    }


}
