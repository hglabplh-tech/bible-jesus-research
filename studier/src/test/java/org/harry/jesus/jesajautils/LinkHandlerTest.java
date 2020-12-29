package org.harry.jesus.jesajautils;

import org.junit.Test;

public class LinkHandlerTest {

    @Test
    public void simpleTest() {
        String text2 = "verboten (vergleiche 3. Mose 18,9; 20,17; 5. Mose 27,22)";
        BibleTextUtils utils = new BibleTextUtils();
        LinkHandler.parseLinksFuzzy(utils, "vergleiche Hesekiel 28,11-15; Daniel 10,13");
        LinkHandler.parseLinksFuzzy(utils, text2);
        System.out.println(LinkHandler.generateLinksFuzzy(utils, text2));
    }
}
