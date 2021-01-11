package org.harry.jesus.jesajautils;

import java.net.URLEncoder;
import java.util.Optional;

public class BibleLinkCreator {

    private static final String LINK_PREFIX = "https://www.bibleserver.com/";

    public static  String createBibleLinkShort(BibleTranslations translation,
                                                BibleTextUtils.BookLabel label, Integer chapterNo) {
        StringBuffer buffer = new StringBuffer(LINK_PREFIX);
        buffer.append(translation.getTransSchort() + "/");
        buffer.append(label.getShortName() + chapterNo);
        return buffer.toString();
    }

    public static  String createBibleLink(BibleTranslations translation, BibleTextUtils.BookLink link) {
        // String test = "https://www.bibleserver.com/HFA/1.Korinther13%2C7-14";
        StringBuffer buffer = new StringBuffer(LINK_PREFIX);
        buffer.append(translation.getTransSchort() + "/");
        BibleTextUtils.BookLabel label = new BibleTextUtils.BookLabel(link.getBookLabel());
        // here wwe have to enhance it
        buffer.append(label.getShortName() + link.getChapter());
        buffer.append("%2C" + link.getVerses().get(0));
        return buffer.toString();
    }

    public enum BibleTranslations {

        BIBLE_SELF("SELF", "Local linked Bible"),
        BIBLE_HFA("HFA", "Hoffnung für alle"),
        BIBLE_LUT("LUT", "Luther Übersetzung"),
        BIBLE_ELB("ELB", "Elberfelder Übersetzung"),
        BIBLE_SLT("SLT", "Schlachter Übersetzung"),
        BIBLE_ZB("ZB", "Zürricher Bibel"),
        BIBLE_NLB("NLB", "Neues Leben Bibel"),
        BIBLE_EU("EU", "Einheitsübersetzung 2016"),
        BIBLE_MENG("MENG", "Menge Bibel"),
        BIBLE_NEU("NeÜ", "Neue evangelistische Übersetzung"),
        BIBLE_GNB("GNB", "Gute Nachricht Bibel");


        private final String transSchort;

        private final String description;

        BibleTranslations(String transSchort, String description) {
            this.transSchort = transSchort;
            this.description = description;
        }

        public String getTransSchort() {
            return transSchort;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return description + " (" + transSchort + ")";
        }

        public static Optional<BibleTranslations> findByShort(String shortName) {
            for (BibleTranslations actEnum: BibleTranslations.values()) {
                if (actEnum.getTransSchort().equals(shortName)) {
                    return Optional.of(actEnum);
                }
            }
            return Optional.empty();
        }

        public static Optional<BibleTranslations> findByDescription(String description) {
            for (BibleTranslations actEnum: BibleTranslations.values()) {
                if (actEnum.getDescription().equals(description)) {
                    return Optional.of(actEnum);
                }
            }
            return Optional.empty();
        }
    }
}
