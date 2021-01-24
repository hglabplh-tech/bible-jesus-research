package org.harry.jesus.fxutils;

import generated.XMLBIBLE;
import org.apache.http.client.utils.URIBuilder;
import org.harry.jesus.jesajautils.BibleDictUtil;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.Tuple;
import org.pmw.tinylog.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class LinkHandler {

     public static final String BIBLE_PARAM = "bible";
     public static final String BOOKNO_PARAM = "bookNo";
     public static final String CHAPTERNO_PARAM = "chapterNo";
    public static final String VERSENO_PARAM = "versNo";

    /**
     * Gets real bible link.
     *
     * @param href the href
     * @return the real bible link
     */
    public static Tuple<XMLBIBLE, BibleTextUtils.BookLink> getRealBibleLink(String href) {
        try {
            URI uri = new URI(href);
            Map<String, String> parameters = splitURIQuery(uri);
            String bibleId = parameters.get(BIBLE_PARAM);
            String bookNo = parameters.get(BOOKNO_PARAM);
            String chapter = parameters.get(CHAPTERNO_PARAM);
            String verseNo = parameters.get(VERSENO_PARAM);
            BibleTextUtils.BookLink link = new BibleTextUtils.BookLink(
                    Integer.parseInt(bookNo),
                    Integer.parseInt(chapter),
                    Arrays.asList(Integer.parseInt(verseNo)));
            Optional<BibleTextUtils.BibleBookInstance> optBible =
                    findBibleById(bibleId);
            if (!optBible.isPresent()) {
                optBible = findBibleById("ELB1905");
            }

            if (optBible.isPresent()) {
                XMLBIBLE selected = optBible.get().getBible();
                return new Tuple<>(selected, link);
            } else {
                Throwable ex = new UnsatisfiedLinkError(uri.toString());
                Logger.trace(ex);
                Logger.trace("Link is unsatisfied" + ex.getMessage());
                throw new IllegalStateException(ex);
            }
        } catch (URISyntaxException ex) {
            Logger.trace("Link is unsatisfied" + ex.getMessage());
            throw new IllegalStateException(ex);
        }
    }

    public static Optional<BibleTextUtils.BibleBookInstance> findBibleById(final String bibleId) {
       return BibleTextUtils.getInstance().getBibleInstances().stream()
                .filter(e -> e.getBibleRef().getBibleID().equals(bibleId))
                .findFirst();
    }

    /**
     * Build link internal.
     *
     * @param link       the link
     * @param htmlBuffer the html buffer
     */
    public static void buildLinkInternal(String link, StringBuffer htmlBuffer) {
            htmlBuffer.append("http://_self/#"
                    + link);
    }

    /**
     * Generate hyper link string.
     *
     * @param buffer the buffer
     * @param link   the link
     *
     * @return the string
     */
    public static String generateHyperLink(StringBuffer buffer, Integer bookNo
            , Integer chapter, Integer vers) {
        try {
            BibleTextUtils.BookLabel label = BibleTextUtils.getInstance()
                    .getBookLabMap().get(bookNo);
            String text = "[" + label.getLongName()
                    + " " + chapter +  "," + vers + "]";
            XMLBIBLE bible = BibleTextUtils.getInstance().getSelected();
            String bibleId = BibleDictUtil
                    .getIdFromBibleInfo(
                            bible.getINFORMATION().getValue());
            URI uri = new URIBuilder("http://bible")
                    .setParameter(BIBLE_PARAM, bibleId)
                    .setParameter(BOOKNO_PARAM, Integer.toString(bookNo))
                    .setParameter(CHAPTERNO_PARAM, Integer.toString(chapter))
                    .setParameter(VERSENO_PARAM, Integer.toString(vers)).build();
            String linkText = "<a href=\"" + uri.toString() + "\">" + text + "</a>";
            buffer.append(linkText);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            Logger.trace(ex);
            Logger.trace("cannt create link reason: " + ex.getMessage());
        }

        return buffer.toString();
    }

    public static Map<String, String> splitURIQuery(URI theURI)  {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        try {
            String query = theURI.getQuery();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                        URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }

        } catch(UnsupportedEncodingException ex) {
            Logger.trace(ex);
            Logger.trace("URI parsing failed with: " + ex.getMessage());
        }
        return query_pairs;
    }
}
