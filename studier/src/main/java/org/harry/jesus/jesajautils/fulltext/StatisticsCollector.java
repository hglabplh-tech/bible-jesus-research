package org.harry.jesus.jesajautils.fulltext;

import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticsCollector {

    private Map<Integer, Integer> bookStatsMap = new LinkedHashMap<>();

    private Map<Integer, ChapterStats> chapterStatsMap = new LinkedHashMap<>();

    private Integer lastBook = 0;

    private int counter = 0;

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

    public void callBackEnd(Integer book, Integer chapter) {
        bookStatsMap.put(lastBook, counter);
    }

    public Map<Integer, Integer> getBookStatsMap() {
        return bookStatsMap;
    }

    public Map<Integer, ChapterStats> getChapterStatsMap() {
        return chapterStatsMap;
    }

    public static class ChapterStats {
        private final int chapter;

        private final int hitCount;

        public ChapterStats(int chapter, int hitCount) {
            this.chapter = chapter;
            this.hitCount = hitCount;
        }

        public int getChapter() {
            return chapter;
        }

        public int getHitCount() {
            return hitCount;
        }
    }


}
