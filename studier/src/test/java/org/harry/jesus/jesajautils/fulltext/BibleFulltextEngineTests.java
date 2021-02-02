package org.harry.jesus.jesajautils.fulltext;

import generated.XMLBIBLE;
import org.harry.jesus.jesajautils.BibleDictUtil;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class BibleFulltextEngineTests {

    @Test
    public void simpleSearch() {
        XMLBIBLE bible = BibleFulltextEngine.getUtil().getBibleInstances().get(2).getBible();
        StatisticsCollector collector = new StatisticsCollector();
        BibleFulltextEngine engine = new BibleFulltextEngine(bible, collector);
        engine.searchPlain("Herz");
        List<BibleFulltextEngine.BibleTextKey> hits = engine.retrieveResultsByBookOrder();
        List<StatisticsCollector.BooksStats> booksStats = collector.getBookStatsList();
        System.out.println("search-size: " + hits.size());
        printStats(collector);
    }

    @Test
    public void simpleSearchBothMechanisms() {
        Optional<BibleTextUtils.BibleBookInstance> bibleInst = BibleFulltextEngine.getUtil().getBibleInstances().stream()
                .filter(e -> BibleDictUtil.getIdFromBibleInfo(e.getBible()
                        .getINFORMATION()
                        .getValue()).equals("ELB1905STR")
                ).findFirst();
        XMLBIBLE bible = bibleInst.get().getBible();
        StatisticsCollector collector = new StatisticsCollector();
        BibleFulltextEngine engine = new BibleFulltextEngine(bible, collector);
        engine.searchPlain("Jesus");
        List<BibleFulltextEngine.BibleTextKey> hits = engine.retrieveResultsByBookOrder();
        List<BibleFulltextEngine.BibleTextKey> hitsRelevanz = engine.retrieveResultsByRelevanz();

        List<StatisticsCollector.BooksStats> booksStats = collector.getBookStatsList();
        printStats(collector);

        System.out.println("search-size: " + hits.size());
        System.out.println("search-size-relevance : " + hitsRelevanz.size());
        System.out.println("Missing Items in relevance");
        analyzeMissing(hits, hitsRelevanz);
    }

    private void analyzeMissing(List<BibleFulltextEngine.BibleTextKey> hitsFirst,
                                List<BibleFulltextEngine.BibleTextKey> hitsSecond) {
        if (hitsFirst.size() != hitsSecond.size()) {
            System.out.println("the hit counts are NOT equal ->:\n "
                    + "first hits: " + hitsFirst.size()
                    + "\nsecond hits: " + hitsSecond.size()
            );
        } else {
            System.out.println("the hit counts are equal: " + hitsFirst.size());
        }
        /*
        deeper check
         */
        System.out.println("Missing in first List: \n\n");
        List<BibleFulltextEngine.BibleTextKey> missingFirstList = new ArrayList<>();
        for (BibleFulltextEngine.BibleTextKey key: hitsSecond) {
            if (!hitsFirst.contains(key)) {
                missingFirstList.add(key);
                System.out.println("Value missing in first: " + key.toString());
            }
        }
        System.out.println("Missing in second List: \n\n");
        List<BibleFulltextEngine.BibleTextKey> missingSecondList = new ArrayList<>();
        for (BibleFulltextEngine.BibleTextKey key: hitsFirst) {
            if (!hitsSecond.contains(key)) {
                missingSecondList.add(key);
                System.out.println("Value missing in second: " + key.toString());
            }
        }

        List<BibleFulltextEngine.BibleTextKey> allMissingList = new ArrayList<>();
        allMissingList.addAll(missingFirstList);
        allMissingList.addAll(missingSecondList);
        if (allMissingList.size() > 0) {
            System.out.println("Critical entries:\n");
            for(BibleFulltextEngine.BibleTextKey key: allMissingList) {
                System.out.println(key);
            }
        }

    }

    @Test
    public void patternSearch() {
        XMLBIBLE bible = BibleFulltextEngine.getUtil().getBibleInstances().get(2).getBible();
        StatisticsCollector collector = new StatisticsCollector();
        BibleFulltextEngine engine = new BibleFulltextEngine(bible, collector);
        engine.searchPattern("*Herz*Jesus* OR *Jesus*Herz*", 0);
        List<BibleFulltextEngine.BibleTextKey> hits = engine.retrieveResultsByBookOrder();
        List<StatisticsCollector.BooksStats> booksStats = collector.getBookStatsList();
        hits.forEach(e -> System.err.println(e.getVerseText()));
        System.out.println("search-size: " + hits.size());
        printStats(collector);
    }

    private void printStats(StatisticsCollector collector) {
        List<StatisticsCollector.ChapterStats> chapterStats = collector.getChapterStatsList();
        List<StatisticsCollector.BooksStats> booksStats = collector.getBookStatsList();
        System.out.println("Chapters Statistics:\n");
        AtomicInteger aIntChapters = new AtomicInteger(0);
        chapterStats.forEach(e ->
        {System.out.println(e.toString());
        Integer newInt = aIntChapters.get() + e.getHitCount();
        aIntChapters.set(newInt);}
        );
        System.out.println("Books Statistics:\n");
        AtomicInteger aIntBooks = new AtomicInteger(0);
        booksStats.forEach(e -> {
                    System.out.println(e.toString());
                    Integer newInt = aIntBooks.get() + e.getHitCount();
                    aIntBooks.set(newInt);
                }
        );
        System.out.println("Chapters count total: " + aIntChapters.get());
        System.out.println("Books count total: " + aIntBooks.get());
    }

    @Test
    public void patternFuzzySearch() {
        XMLBIBLE bible = BibleFulltextEngine.getUtil().getBibleInstances().get(2).getBible();
        StatisticsCollector collector = new StatisticsCollector();
        BibleFulltextEngine engine = new BibleFulltextEngine(bible, collector);
                engine.searchPatternFuzzy("Herz Jesus OR Lazarus Jesus", 0);
        List<BibleFulltextEngine.BibleTextKey> hits = engine.retrieveResultsByBookOrder();
        List<StatisticsCollector.BooksStats> booksStats = collector.getBookStatsList();
        hits.forEach(e -> System.err.println(e.getVerseText()));
        System.out.println("search-size: " + hits.size());
        printStats(collector);
    }

    @Test
    public void patternCheck01() {
        List<String> patternList = BibleFulltextEngine
                .patternFromMiniGoogle("Jesus OR -Herr*Israel OR -Lazarus*Jesus");
        Boolean result = BibleFulltextEngine
                .patternMatch("Jesus und Mose auf dem Berg", patternList, 0);

        System.out.println("match : " + Boolean.toString(result));
    }

    @Test
    public void patternCheck02() {
        List<String> patternList = BibleFulltextEngine
                .patternFromMiniGoogle("*-Herr*Israel* OR *-Lazarus*Jesus*");
        Boolean result = BibleFulltextEngine
                .patternMatch("Jesus und Lazarus und Mose und der Herr auf dem Berg in Israel", patternList, 0);

        System.out.println("match : " + Boolean.toString(result));
    }

    @Test
    public void patternCheck03() {
        List<String> patternList = BibleFulltextEngine
                .patternFromMiniGoogle("*Herrn*Israel* OR *Lazarus*Jesus*");
        Boolean result = BibleFulltextEngine
                .patternMatch("Lazarus und Jesus  und Mose  dem Berg beim Herrn in Israel", patternList, 0);

        System.out.println("match : " + Boolean.toString(result));
    }

    @Test
    public void patternCheck04() {
        List<String> patternList = BibleFulltextEngine
                .patternFromMiniGoogle("Herr Israel OR Lazarus Jesus");
        Boolean result = BibleFulltextEngine
                .patternMatchFuzzy("Jesus  und Mose  dem Berg beim Herr in Israel", patternList, 0);

        System.out.println("match : " + Boolean.toString(result));
    }
}
