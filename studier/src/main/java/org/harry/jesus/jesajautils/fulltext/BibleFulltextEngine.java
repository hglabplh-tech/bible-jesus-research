package org.harry.jesus.jesajautils.fulltext;

import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.XMLBIBLE;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.Tuple;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private List<BibleTextKey> wholeBook = new ArrayList<>();

    private static BibleTextUtils util = BibleTextUtils.getInstance();

    private final XMLBIBLE bible;

    private final List<BibleTextKey> hits = new ArrayList<>();

    private final StatisticsCollector collector;

    /**
     * Instantiates a new Bible fulltext engine.
     *
     * @param bible the bible
     */
    public BibleFulltextEngine(XMLBIBLE bible, StatisticsCollector collector) {
        this.bible = bible;
        List<BIBLEBOOK> books = util.getBooks(bible);
        for (BIBLEBOOK book: books) {
            List<CHAPTER> chapters = util.getChapters(book);
            for (CHAPTER chapter: chapters) {
                wholeBook.addAll(util.getVerses(chapter, book.getBnumber().intValue()));
            }
        }
        this.collector = collector;
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
     * @return the map
     */
// (.*)
    public void searchPlain(String plainQuery) {
        for (BibleTextKey entry: this.wholeBook) {
            String upper = entry.getVerseText().toUpperCase();
            if (upper.contains(plainQuery.toUpperCase())) {
                collector.callBack(entry.getBook(),
                        entry.getChapter(),
                        entry.getVers(),  entry.getVerseText());
                hits.add(entry);
            }
        }
    }

    /**
     * Search pattern map.
     *
     * @param patternQuery the pattern query
     * @param flags        the flags
     * @return the map
     */
    public void searchPattern(String patternQuery, int flags) {
        List<String> patternList = patternFromMiniGoogle(patternQuery);
        for (BibleTextKey entry: this.wholeBook) {
            if (patternMatch(entry.getVerseText(), patternList, flags)) {
                collector.callBack(entry.getBook(),
                        entry.getChapter(),
                        entry.getVers(),  entry.getVerseText());
                hits.add(entry);
            }
        }


    }

    /**
     * Returns the query result in books order
     * @return the book order result
     */
    public List<BibleTextKey> retrieveResultsByBookOrder() {
        return hits;
    }

    /**
     * returns the relkevance ordered result of the query
     * @return the relevance result of the query
     */
    public List<BibleTextKey> retrieveResultsByRelevanz() {
        List<BibleTextKey> hits = new ArrayList<>();
        List<BibleTextKey> hitsResult = new ArrayList<>();
        Stream<StatisticsCollector.BooksStats> sortedStream = collector.getBookStatsList().stream().sorted(
                new Comparator<StatisticsCollector.BooksStats>() {
            @Override
            public int compare(StatisticsCollector.BooksStats o1, StatisticsCollector.BooksStats o2) {
                int result = o2.getHitCount().compareTo(o1.getHitCount());
                return result;
            }
        });
        sortedStream.forEachOrdered(e ->
        {
            Integer book = e.getBook();
            List<StatisticsCollector.ChapterStats> chapterStats =
                    collector.getChapterStatsList().stream().filter(e1 -> (e1.getBook() == book))
                    .collect(Collectors.toList());
            for (StatisticsCollector.ChapterStats stat: chapterStats) {
                for (Tuple<Integer, String> verse: stat.getVerseList()) {
                    BibleTextKey textKey = new BibleTextKey(stat.getBook(),
                            stat.getChapter(),
                            verse.getFirst(), verse.getSecond());
                    hitsResult.add(textKey);
                }
            }
        });
        return hitsResult;
    }

    /**
     * Search pattern fuzzy map.
     *
     * @param patternQuery the pattern query
     * @param flags        the flags
     * @return the map
     */
    public void searchPatternFuzzy(String patternQuery, int flags) {
        List<String> patternList = patternFromMiniGoogle(patternQuery);
        for (BibleTextKey entry: this.wholeBook) {
            if (patternMatchFuzzy(entry.getVerseText(), patternList, flags)) {
                collector.callBack(entry.getBook(),
                        entry.getChapter(),
                        entry.getVers(),  entry.getVerseText());
                hits.add(entry);
            }
        }

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
    public static class     BibleTextKey implements Serializable {

        private final Integer book;
        
        private final Integer chapter;
        
        private final Integer vers;

        private final String verseText;

        /**
         * Instantiates a new Bible text key.
         *  @param book    the book
         * @param chapter the chapter
         * @param vers    the vers
         * @param verseText
         */
        public BibleTextKey(Integer book, Integer chapter, Integer vers, String verseText) {
            this.book = book;
            this.chapter = chapter;
            this.vers = vers;
            this.verseText = verseText;
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

        /**
         * Get the verse text
         * @return the verse text
         */
        public String getVerseText() {
            return verseText;
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

        @Override
        public String toString() {
            return "BibleTextKey{" +
                    "book=" + book +
                    ", chapter=" + chapter +
                    ", vers=" + vers +
                    ", verseText=\"" + verseText + "\"" +
                    '}';
        }
    }
}
