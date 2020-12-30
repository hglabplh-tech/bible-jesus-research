package org.harry.jesus.jesajautils;

import org.junit.Test;

import java.util.List;
import java.util.Map;

public class LinkHandlerTest {

    @Test
    public void simpleTest() {
        String text2 = "verboten (vergleiche 3. Mose 18,9; 20,17; 5. Mose 27,22)";
        BibleTextUtils utils = new BibleTextUtils();
        LinkHandler.parseLinksFuzzy(utils, "vergleiche Hesekiel 28,11-15; Daniel 10,13");
        LinkHandler.parseLinksFuzzy(utils, text2);
        System.out.println(LinkHandler.generateLinksFuzzy(utils, text2));
    }

    @Test
    public void testComplexLine() {
        BibleTextUtils utils = new BibleTextUtils();
        String text = "Das zweite Buch Mose wird auch \"Exodus\" genannt, \"Auszug\", " +
                "denn es beinhaltet die Geschichte vom Auszug des Volkes Israel aus der Sklaverei in Ägypten. " +
                "Das Buch ist in zwei Hauptteile gegliedert: Kapitel 1-19: " +
                "Auszug aus Ägypten; Kapitel 20-40: Gesetzgebung am Berg Sinai und Bau des Heiligtums. " +
                "In diesem Buch wird der Verfasser, Mose, selbst zum Träger der Handlung. " +
                "Man kann davon ausgehen, dass er dieses Buch während oder kurz nach dem Aufenthalt des " +
                "Volkes am Sinai 1446 v.Chr. (Frühdatierung) niederschrieb. " +
                "Gott hatte ihn verschiedentlich dazu aufgefordert (2. Mose 17,14; 34,27). " +
                "Auch andere biblische Bücher und Jesus selbst bestätigen die Verfasserschaft " +
                "Moses (1. Könige 2,3; Nehemia 8,1; Markus 7,10; 12,26). " +
                "Siehe Fußnote zu 1. Mose 46,27. Jakobs Ankunft in " +
                "Ägypten könnte um das Jahr 1867 v.Chr. stattgefunden haben.";
        Map<String, List<Integer>> bookCol = LinkHandler.collectLabelsWithBeginIndex(utils, text);
        bookCol.entrySet().stream().forEach(e -> {
                    System.out.println(e.getKey());
                    for (Integer num : e.getValue()) {
                        System.out.print(num + ",");
                    }
                    System.out.println("");
                });
        LinkHandler.parseLinksFuzzy(utils, text);
    }
}
