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

public class BibleFulltextEngine {

    public static final int INSENSITIVE = Pattern.CASE_INSENSITIVE;

    public static final String OR_OP = "OR";

    public static final String WILDCARD = "*";

    public static final String NOT = "-";

    public static final Map<String, String> operatorMap= new HashMap<>();

    static {
        operatorMap.put(NOT,"{0}");
        operatorMap.put(WILDCARD,"(.*)");
    }

    private Map<BibleTextKey, String> wholeBook = new LinkedHashMap<>();

    private static BibleTextUtils util = BibleTextUtils.getInstance();

    private final XMLBIBLE bible;

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

    public static BibleTextUtils getUtil() {
        return util;
    }
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

    public static class BibleTextKey implements Serializable {

        private final Integer book;
        
        private final Integer chapter;
        
        private final Integer vers;

        public BibleTextKey(Integer book, Integer chapter, Integer vers) {
            this.book = book;
            this.chapter = chapter;
            this.vers = vers;
        }

        public Integer getBook() {
            return book;
        }

        public Integer getChapter() {
            return chapter;
        }

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
