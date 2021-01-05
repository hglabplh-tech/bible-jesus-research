package org.harry.jesus.jesajautils.fulltext;

import generated.XMLBIBLE;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class BibleFulltextEngineTests {

    @Test
    public void simpleSearch() {
        XMLBIBLE bible = BibleFulltextEngine.getUtil().getBibleInstances().get(2).getBible();
        BibleFulltextEngine engine = new BibleFulltextEngine(bible);
        StatisticsCollector collector = new StatisticsCollector();
        Map<BibleFulltextEngine.BibleTextKey, String> hits =
                engine.searchPlain("Herz", collector);
        Map<Integer, Integer> booksStats = collector.getBookStatsMap();
        System.out.println("search-size: " + hits.size());
    }

    @Test
    public void patternSearch() {
        XMLBIBLE bible = BibleFulltextEngine.getUtil().getBibleInstances().get(2).getBible();
        BibleFulltextEngine engine = new BibleFulltextEngine(bible);
        StatisticsCollector collector = new StatisticsCollector();
        Map<BibleFulltextEngine.BibleTextKey, String> hits =
                engine.searchPattern("*Herz*Jesus* OR *Jesus*Herz*", 0,collector);
        Map<Integer, Integer> booksStats = collector.getBookStatsMap();
        hits.values().forEach(e -> System.err.println(e));
        System.out.println("search-size: " + hits.size());
    }

    @Test
    public void patternFuzzySearch() {
        XMLBIBLE bible = BibleFulltextEngine.getUtil().getBibleInstances().get(2).getBible();
        BibleFulltextEngine engine = new BibleFulltextEngine(bible);
        StatisticsCollector collector = new StatisticsCollector();
        Map<BibleFulltextEngine.BibleTextKey, String> hits =
                engine.searchPatternFuzzy("Herz Jesus OR Lazarus Jesus", 0,collector);
        Map<Integer, Integer> booksStats = collector.getBookStatsMap();
        hits.values().forEach(e -> System.err.println(e));
        System.out.println("search-size: " + hits.size());
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
