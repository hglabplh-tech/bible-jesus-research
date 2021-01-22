package org.harry.jesus.jesajautils.fulltext;

import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.XMLBIBLE;
import org.harry.jesus.jesajautils.BibleTextUtils;

import java.io.Serializable;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Bible fulltext engine.
 */
public class BibleFulltextEngine {

    /**
     * The constant INSENSITIVE.
     */
    public static final int INSENSITIVE = Pattern.CASE_INSENSITIVE;

    /**
     * The constant OR_OP.
     */
    public static final String OR_OP = "OR";

    /**
     * The constant WILDCARD.
     */
    public static final String WILDCARD = "*";

    /**
     * The constant NOT.
     */
    public static final String NOT = "-";

    /**
     * The constant operatorMap.
     */
    public static final Map<String, String> operatorMap= new HashMap<>();

    static {
        operatorMap.put(NOT,"{0}");
        operatorMap.put(WILDCARD,"(.*)");
    }

    private Map<BibleTextKey, String> wholeBook = new LinkedHashMap<>();

    private static BibleTextUtils util = BibleTextUtils.getInstance();

    private final XMLBIBLE bible;

    /**
     * Instantiates a new Bible fulltext engine.
     *
     * @param bible the bible
     */
    public BibleFulltextEngine(XMLBIBLE bible) {
        this.bible = bible;
        List<BIBLEBOOK> books = util.getBooks(bible);
        for (BIBLEBOOK book: books) {
            List<CHAPTER> chapters = util.getChapters(book);
            for (CHAPTER chapter: chapters) {
                wholeBook.putAll(util.getVerses(chapter, book.getBnumber().intValue()));
            }
        }
        System.err.println("End of engine construct");
    }

    /**
     * Gets util.
     *
     * @return the util
     */
    public static BibleTextUtils getUtil() {
        return util;
    }

    /**
     * Search plain map.
     *
     * @param plainQuery the plain query
     * @param collector  the collector
     * @return the map
     */
// (.*)
    public Map<BibleTextKey, String> searchPlain(String plainQuery, StatisticsCollector collector) {
        Map<BibleTextKey, String> hits = new LinkedHashMap<>();
        Map.Entry<BibleTextKey, String> lastentry = null;
        for (Map.Entry<BibleTextKey, String> entry: this.wholeBook.entrySet()) {
            String upper = entry.getValue().toUpperCase();
            if (upper.contains(plainQuery.toUpperCase())) {
                lastentry = entry;
                collector.callBack(entry.getKey().getBook(), entry.getKey().getChapter());
                hits.put(entry.getKey(), entry.getValue());
            }
        }
        if (lastentry != null) {
            collector.callBackEnd(lastentry.getKey().getBook(), lastentry.getKey().getChapter());
        }
        return hits;
    }

    /**
     * Search pattern map.
     *
     * @param patternQuery the pattern query
     * @param flags        the flags
     * @param collector    the collector
     * @return the map
     */
    public Map<BibleTextKey, String> searchPattern(String patternQuery, int flags, StatisticsCollector collector) {
        Map<BibleTextKey, String> hits = new LinkedHashMap<>();
        Map.Entry<BibleTextKey, String> lastentry = null;
        List<String> patternList = patternFromMiniGoogle(patternQuery);
        for (Map.Entry<BibleTextKey, String> entry: this.wholeBook.entrySet()) {
            if (patternMatch(entry.getValue(), patternList, flags)) {
                lastentry = entry;
                collector.callBack(entry.getKey().getBook(), entry.getKey().getChapter());
                hits.put(entry.getKey(), entry.getValue());
            }
        }
        if (lastentry != null) {
            collector.callBackEnd(lastentry.getKey().getBook(), lastentry.getKey().getChapter());
        }
        return hits;
    }

    /**
     * Search pattern fuzzy map.
     *
     * @param patternQuery the pattern query
     * @param flags        the flags
     * @param collector    the collector
     * @return the map
     */
    public Map<BibleTextKey, String> searchPatternFuzzy(String patternQuery, int flags, StatisticsCollector collector) {
        Map<BibleTextKey, String> hits = new LinkedHashMap<>();
        Map.Entry<BibleTextKey, String> lastentry = null;
        List<String> patternList = patternFromMiniGoogle(patternQuery);
        for (Map.Entry<BibleTextKey, String> entry: this.wholeBook.entrySet()) {
            if (patternMatchFuzzy(entry.getValue(), patternList, flags)) {
                lastentry = entry;
                collector.callBack(entry.getKey().getBook(), entry.getKey().getChapter());
                hits.put(entry.getKey(), entry.getValue());
            }
        }
        if (lastentry != null) {
            collector.callBackEnd(lastentry.getKey().getBook(), lastentry.getKey().getChapter());
        }
        return hits;
    }

    /**
     * Pattern match boolean.
     *
     * @param toSearch    the to search
     * @param patternList the pattern list
     * @param flags       the flags
     * @return the boolean
     */
    public static boolean patternMatch(String toSearch, List<String> patternList, int flags) {
        boolean result = false;
        for (String patternString: patternList) {
            String finitePattern = "(.*)" + patternString + "(.*)";
            Pattern pattern = Pattern.compile(finitePattern, flags);
            Matcher matcher = pattern.matcher(toSearch);
            result = result ||  matcher.matches();
        }
        return result;
    }

    /**
     * Pattern match fuzzy boolean.
     *
     * @param toSearch    the to search
     * @param patternList the pattern list
     * @param flags       the flags
     * @return the boolean
     */
    public static boolean patternMatchFuzzy(String toSearch, List<String> patternList, int flags) {
        boolean result = false;
        for (String patternString: patternList) {
            String [] andValues = patternString.trim().split(" ");
            boolean andMatch = true;
            for (String andValue: andValues) {
                andMatch = andMatch && toSearch.contains(andValue);
            }
            result = result ||  andMatch;
        }
        return result;
    }

    /**
     * Pattern from mini google list.
     *
     * @param rawPattern the raw pattern
     * @return the list
     */
    public static List<String> patternFromMiniGoogle(String rawPattern) {
        int index = rawPattern.indexOf(OR_OP);
        int start = 0;
        int end = 0;
        List<String> rawPatternList = new ArrayList<>();
        while (index > -1) {
            rawPatternList.add(rawPattern.substring(start, index));
            start = index + OR_OP.length();
            index = rawPattern.indexOf(OR_OP,start);
        }
        List<String> patternList = new ArrayList<>();
        rawPatternList.add(rawPattern.substring(start));
        for (String e : rawPatternList) {
            String temp = e.replaceAll("\\" + WILDCARD, operatorMap.get(WILDCARD));
            patternList.add(temp.replaceAll("\\" + NOT, operatorMap.get(NOT)));
        }
        return patternList;
    }

    /**
     * The type Bible text key.
     */
    public static class BibleTextKey implements Serializable {

        private final Integer book;
        
        private final Integer chapter;
        
        private final Integer vers;

        /**
         * Instantiates a new Bible text key.
         *
         * @param book    the book
         * @param chapter the chapter
         * @param vers    the vers
         */
        public BibleTextKey(Integer book, Integer chapter, Integer vers) {
            this.book = book;
            this.chapter = chapter;
            this.vers = vers;
        }

        /**
         * Gets book.
         *
         * @return the book
         */
        public Integer getBook() {
            return book;
        }

        /**
         * Gets chapter.
         *
         * @return the chapter
         */
        public Integer getChapter() {
            return chapter;
        }

        /**
         * Gets vers.
         *
         * @return the vers
         */
        public Integer getVers() {
            return vers;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BibleTextKey that = (BibleTextKey) o;
            return book.equals(that.book) &&
                    chapter.equals(that.chapter) &&
                    vers.equals(that.vers);
        }

        @Override
        public int hashCode() {
            return Objects.hash(book, chapter, vers);
        }
    }
}
