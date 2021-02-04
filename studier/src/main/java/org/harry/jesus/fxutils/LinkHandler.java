package org.harry.jesus.fxutils;

import com.sun.xml.internal.rngom.util.Uri;
import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.XMLBIBLE;
import org.apache.http.client.utils.URIBuilder;
import org.harry.jesus.jesajautils.BibleDictUtil;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.jesajautils.judaerrmsg.BibleStudyException;
import org.pmw.tinylog.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.*;

/**
 * The type Link handler.
 */
public class LinkHandler {

    /**
     * The constant BIBLE_PARAM.
     */
    public static final String BIBLE_PARAM = "bible";

    /**
     * The constant BOOKNO_PARAM.
     */
    public static final String BOOKNO_PARAM = "bookNo";

    /**
     * The constant BOOKNO_PARAM.
     */
    public static final String BOOK_PARAM = "book";

    /**
     * The constant CHAPTERNO_PARAM.
     */
    public static final String CHAPTERNO_PARAM = "chapterNo";

    /**
     * The constant VERSENO_PARAM.
     */

    public static final String VERSENO_PARAM = "versNo";

    /**
     * Gets real bible link.
     *
     * @param href the href
     * @return the real bible link
     */
    public static Tuple<XMLBIBLE, BibleTextUtils.BookLink> getRealBibleLink(String href) throws BibleStudyException {
        try {
            URI uri = new URI(href);
            return getRealBibleLink(uri);
        } catch (URISyntaxException ex) {
            Logger.trace("Link is unsatisfied" + ex.getMessage());
            throw new IllegalStateException(ex);
        }
    }

    private static void  assertNumeric(String toTest) throws BibleStudyException {
        boolean ok = toTest.matches("[0-9]+");
        if (!ok) {
            throw new BibleStudyException(toTest + " must be numeric");
        }
    }

    /**
     * Gets real bible link.
     *
     * @param uri the the URI
     * @return the real bible link
     */
    public static Tuple<XMLBIBLE, BibleTextUtils.BookLink> getRealBibleLink(URI uri) throws BibleStudyException{

        Map<String, String> parameters = splitURIQuery(uri);
        String bibleId = parameters.get(BIBLE_PARAM);
        String bookNo = parameters.get(BOOKNO_PARAM);
        if (bookNo == null) {
            String bookName = parameters.get(BOOK_PARAM);
            if (bookName == null) {
                throw new BibleStudyException("Book is not given define either book or bookNo parameter");
            }
            Optional<String> bookLabel = BibleTextUtils.getInstance().getBookLabels()
                    .stream()
                    .filter(e -> e.contains(bookName)).findFirst();
            if (bookLabel.isPresent()) {
                BibleTextUtils.BookLabel label  = BibleTextUtils
                        .getInstance()
                        .getBookLabelAsClass(bookLabel.get());
                bookNo = Integer.toString(label.getBookNumber(), 10);
            } else {
                throw new BibleStudyException("The book label: " + bookName + " is not found");
            }

        }
        String chapter = parameters.get(CHAPTERNO_PARAM);
        String verseNo = parameters.get(VERSENO_PARAM);
        if (verseNo == null) {
            verseNo = "1";
        }

        assertNumeric(bookNo);
        assertNumeric(chapter);
        assertNumeric(verseNo);


        Optional<BibleTextUtils.BibleBookInstance> optBible =
                findBibleById(bibleId);
        BibleTextUtils.BookLink link;



        if (optBible.isPresent()) {
            XMLBIBLE selected = optBible.get().getBible();
            link = checkBibleLink(selected, Integer.parseInt(bookNo),
                    Integer.parseInt(chapter), Integer.parseInt(verseNo));
            return new Tuple<>(selected, link);
        } else {
            Throwable ex = new UnsatisfiedLinkError(uri.toString());
            Logger.trace(ex);
            Logger.trace("Link is unsatisfied" + ex.getMessage());
            throw new BibleStudyException(BibleStudyException.Code.BIBLE_NOT_FOUND, ex, bibleId);
        }
    }

    private static BibleTextUtils.BookLink checkBibleLink(XMLBIBLE selected,
                                                          Integer bookNo, Integer chapterNo,
                                                          Integer verseNo
                                                          ) throws BibleStudyException {
        XMLBIBLE oldSelected = BibleTextUtils.getInstance().getSelected();
        BibleTextUtils.getInstance().setSelected(selected);
        List<BIBLEBOOK> books = BibleTextUtils.getInstance().getBooks(selected);
        Optional<BIBLEBOOK> optBook =
                books.stream()
                        .filter(e -> e.getBnumber().intValue() == bookNo)
                        .findFirst();
        if (optBook.isPresent()) {
            List<CHAPTER> chapters = BibleTextUtils.getInstance().getChapters(optBook.get());
            if (chapterNo < chapters.size() -1 && chapterNo >= 1) {
                CHAPTER chapter = chapters.get(chapterNo - 1);
                List<Integer> verses = BibleTextUtils.getInstance().getVersesForChapterCooked(new Tuple<>(chapterNo, chapter));
                if (verseNo >= chapters.size() -1 || verseNo < 1) {
                    throw new BibleStudyException(BibleStudyException.Code.VERSE_NOT_FOUND,
                            verseNo,
                            chapterNo);
                }
            } else {
                throw new BibleStudyException(BibleStudyException.Code.CHAPTER_NOT_FOUND, chapterNo,
                        bookNo);
            }
        } else {
            throw new BibleStudyException(BibleStudyException.Code.BOOK_NOT_FOUND, bookNo,
                    BibleDictUtil.getIdFromBibleInfo(selected.getINFORMATION().getValue()));
        }
        BibleTextUtils.getInstance().setSelected(oldSelected);
        BibleTextUtils.BookLink link = new BibleTextUtils.BookLink(
                bookNo,
                chapterNo,
                Arrays.asList(verseNo));
        return link;
    }


    /**
     * Find bible by id optional.
     *
     * @param bibleId the bible id
     * @return the optional
     */
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
     * @param buffer  the buffer
     * @param bookNo  the book no
     * @param chapter the chapter
     * @param vers    the vers
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
            URI uri = new URIBuilder("http://localhost")
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

    /**
     * Split uri query map.
     *
     * @param theURI the the uri
     * @return the map
     */
    public static Map<String, String> splitURIQuery(URI theURI)  {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = theURI.getQuery();
        query_pairs = splitQuery(query);
        return query_pairs;
    }

    /**
     * Split uri query map.
     *
     * @param query the query string from the URI
     * @return the map
     */
    public static Map<String, String> splitQuery(String query)  {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        try {
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
