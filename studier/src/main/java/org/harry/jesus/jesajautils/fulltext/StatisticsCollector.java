package org.harry.jesus.jesajautils.fulltext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The type Statistics collector.
 */
public class StatisticsCollector {

    private Map<Integer, Integer> bookStatsMap = new LinkedHashMap<>();

    private Map<Integer, ChapterStats> chapterStatsMap = new LinkedHashMap<>();

    private Integer lastBook = 0;

    private int counter = 0;

    /**
     * Call back.
     *
     * @param book    the book
     * @param chapter the chapter
     */
    public void callBack(Integer book, Integer chapter) {
        if (lastBook.equals(0)) {
            lastBook = book;
            counter++;
        } else if (book.equals(lastBook)) {
            counter++;
        } else if (!lastBook.equals(book)) {
            counter++;
            bookStatsMap.put(lastBook, counter);
            counter = 0;
            lastBook = book;
        }
    }

    /**
     * Call back end.
     *
     * @param book    the book
     * @param chapter the chapter
     */
    public void callBackEnd(Integer book, Integer chapter) {
        bookStatsMap.put(lastBook, counter);
    }

    /**
     * Gets book stats map.
     *
     * @return the book stats map
     */
    public Map<Integer, Integer> getBookStatsMap() {
        return bookStatsMap;
    }

    /**
     * Gets chapter stats map.
     *
     * @return the chapter stats map
     */
    public Map<Integer, ChapterStats> getChapterStatsMap() {
        return chapterStatsMap;
    }

    /**
     * The type Chapter stats.
     */
    public static class ChapterStats {
        private final int chapter;

        private final int hitCount;

        /**
         * Instantiates a new Chapter stats.
         *
         * @param chapter  the chapter
         * @param hitCount the hit count
         */
        public ChapterStats(int chapter, int hitCount) {
            this.chapter = chapter;
            this.hitCount = hitCount;
        }

        /**
         * Gets chapter.
         *
         * @return the chapter
         */
        public int getChapter() {
            return chapter;
        }

        /**
         * Gets hit count.
         *
         * @return the hit count
         */
        public int getHitCount() {
            return hitCount;
        }
    }


}
