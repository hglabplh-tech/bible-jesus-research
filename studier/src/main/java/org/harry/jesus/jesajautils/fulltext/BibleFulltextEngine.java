package org.harry.jesus.jesajautils.fulltext;

import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.XMLBIBLE;
import org.apache.pdfbox.pdfviewer.MapEntry;
import org.harry.jesus.jesajautils.BibleTextUtils;

import java.io.Serializable;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.*;

public class BibleFulltextEngine {

    private Map<BibleTextKey, String> wholeBook = new LinkedHashMap<>();

    private static BibleTextUtils util = new BibleTextUtils();

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
