package org.harry.jesus.jesajautils.fulltext;

import generated.XMLBIBLE;
import org.junit.Test;

import java.util.Map;

public class BibleFulltextEngineTests {

    @Test
    public void simpleSearch() {
        XMLBIBLE bible = BibleFulltextEngine.getUtil().getBibles().get(2);
        BibleFulltextEngine engine = new BibleFulltextEngine(bible);
        StatisticsCollector collector = new StatisticsCollector();
        Map<BibleFulltextEngine.BibleTextKey, String> hits =
                engine.searchPlain("Herz", collector);
        Map<Integer, Integer> booksStats = collector.getBookStatsMap();
        System.out.println("search-size: " + hits.size());
    }
}
