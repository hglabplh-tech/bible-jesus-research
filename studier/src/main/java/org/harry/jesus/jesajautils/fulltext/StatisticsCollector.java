package org.harry.jesus.jesajautils.fulltext;

import org.harry.jesus.jesajautils.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The type Statistics collector.
 */
public class StatisticsCollector {

    private List<BooksStats> bookStatsList = new ArrayList<>();

    private List<ChapterStats> chapterStatsList = new ArrayList<>();

    private Integer lastBook = 0;

    private Integer lastChapter = 0;

    private int counter = 0;

    /**
     * The Act chapter stats.
     */
    ChapterStats actChapterStats = null;

    /**
     * The Act book stats.
     */
    BooksStats actBookStats = null;

    /**
     * update the book statistics and update the chapters counters
     *
     * @param book     the book
     * @param chapter  the chapter
     * @param verse    the verse
     * @param vContent the v content
     */
    public void callBack(Integer book, Integer chapter, Integer verse, String vContent) {

        if (!lastBook.equals(book)) {
            switchBookStats(book);
        }
        actBookStats.incrementHitCount();

        countChapter(book, chapter, verse, vContent);
    }

    /**
     * update the chapter counters and set the verses information
     * @param thisBook the book
     * @param thisChapter the chapter
     * @param verse the verse number
     * @param vContent the verse content
     */
    private void countChapter(Integer thisBook, Integer thisChapter,
                              Integer verse, String vContent) {

        if (!thisChapter.equals(lastChapter) || !thisBook.equals(lastBook)) {
            switchChapterStat(thisBook, thisChapter);
            debugPrintChapterSwitch(thisBook, thisChapter, verse);
        }
        actChapterStats.incrementCounter();
        actChapterStats.addVerseToList(verse, vContent);

    }

    private void debugPrintChapterSwitch(Integer thisBook, Integer thisChapter, Integer verse) {
        System.out.println("Book: "
                + thisBook
                + " Chapter: "
                + thisChapter
                +  " Verse: "
                + verse);
    }

    /**
     * switch to the next book
     * @param book the book
     */
    private void switchBookStats(Integer book) {
        lastBook = book;
        actBookStats = new BooksStats(book);
        bookStatsList.add(actBookStats);
    }

    /**
     * switch to the next chapter
     * @param thisBook the book number
     * @param thisChapter the chapter number
     */
    private void switchChapterStat(Integer thisBook, Integer thisChapter) {
        lastChapter = thisChapter;
        actBookStats.incrementChapterCount();
        actChapterStats =
                new ChapterStats(thisBook, thisChapter);
        chapterStatsList.add(actChapterStats);
    }

    /**
     * Gets book stats map.
     *
     * @return the book stats map
     */
    public List<BooksStats> getBookStatsList() {
        return bookStatsList;
    }

    /**
     * Gets chapter stats map.
     *
     * @return the chapter stats map
     */
    public List<ChapterStats> getChapterStatsList() {
        return chapterStatsList;
    }

    /**
     * The type Chapter stats.
     */
    public static class ChapterStats {

        private final int book;

        private final int chapter;

        private int hitCount;

        private List<Tuple<Integer, String>> verseList;

        /**
         * Instantiates a new Chapter stats.
         *
         * @param book    the book
         * @param chapter the chapter
         */
        public ChapterStats(int book, int chapter) {
            this.book = book;
            this.chapter = chapter;
            this.hitCount = 0;
            this.verseList = new ArrayList<>();
        }

        /**
         * Gets the book of the chapter
         *
         * @return the book
         */
        public int getBook() {
            return book;
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
         * gets the verse list of the chapter
         *
         * @return the list
         */
        public List<Tuple<Integer, String>> getVerseList() {
            return verseList;
        }

        /**
         * Gets hit count.
         *
         * @return the hit count
         */
        public int getHitCount() {
            return hitCount;
        }

        /**
         * Increment counter.
         */
        public void incrementCounter() {
            this.hitCount++;
        }

        /**
         * Add verse to list.
         *
         * @param verseNo   the verse no
         * @param verseText the verse text
         */
        public void addVerseToList(Integer verseNo, String verseText) {
            Tuple<Integer, String> verseTuple = new Tuple<>(verseNo, verseText);
            this.getVerseList().add(verseTuple);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ChapterStats)) return false;
            ChapterStats that = (ChapterStats) o;
            return getBook() == that.getBook() && getChapter() == that.getChapter() && getHitCount() == that.getHitCount();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBook(), getChapter(), getHitCount());
        }

        @Override
        public String toString() {
            return "ChapterStats{" +
                    "book=" + book +
                    ", chapter=" + chapter +
                    ", hitCount=" + hitCount +
                    ", verseList=" + verseList +
                    '}';
        }
    }

    /**
     * The type Books stats.
     */
    public static class BooksStats {

        private final Integer book;

        private Integer chapterCount = 0;

        private Integer hitCount = 0;

        /**
         * constructor initializing the final member book
         *
         * @param book the book number
         */
        public BooksStats(Integer book) {
            this.book = book;
        }

        /**
         * get the book number
         *
         * @return the book number
         */
        public Integer getBook() {
            return book;
        }

        /**
         * get the chabter count
         *
         * @return the chapter count
         */
        public Integer getChapterCount() {
            return chapterCount;
        }

        /**
         * get the hit count
         *
         * @return the hit count
         */
        public Integer getHitCount() {
            return hitCount;
        }

        /**
         * Increment the chapter count
         */
        public void incrementChapterCount() {
            chapterCount++;
        }

        /**
         * Increment the hit count
         */
        public void incrementHitCount() {
            hitCount++;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BooksStats)) return false;
            BooksStats that = (BooksStats) o;
            return getBook().equals(that.getBook()) && getChapterCount().equals(that.getChapterCount()) && getHitCount().equals(that.getHitCount());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBook(), getChapterCount(), getHitCount());
        }

        @Override
        public String toString() {
            return "BooksStats{" +
                    "book=" + book +
                    ", chapterCount=" + chapterCount +
                    ", hitCount=" + hitCount +
                    '}';
        }
    }


}
