package org.harry.jesus.jesajautils;

import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.XMLBIBLE;
import org.pmw.tinylog.Logger;

import javax.xml.bind.JAXBElement;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BibleTextUtils {
    List<String> bibleFnames = Arrays.asList(
            "bible_luther1912.xml",
            "SF_2012-08-14_DEUTSCH_LUT_1545_LH_(LUTHER 1545 (LETZTE HAND)).xml",
            "SF_2009-01-20_GER_ELB1905_(ELBERFELDER 1905).xml",
            "SF_2015-08-16_ENG_NHEBYHWH_(NEW HEART ENGLISH BIBLE (YHWH)).xml",
            "SF_2009-01-20_ENG_UKJV_(UPDATED KING JAMES VERSION).xml"
    );

    List<XMLBIBLE> bibles = new ArrayList<>();

    public BibleTextUtils() {
        for (String bibleName: bibleFnames) {
            InputStream bibleIN = BibleTextUtils.class.getResourceAsStream("/" + bibleName);
            try {
                XMLBIBLE actBible = BibleReader.loadBible(bibleIN);
                bibles.add(actBible);
            } catch (Exception ex) {
                Logger.trace("Exception occured loading bible: " + bibleName);
            }
        }
    }

    public List<String> getBibleInfos() {
        List<String> result = new ArrayList<>();
        for (XMLBIBLE bible: bibles) {
            result.add(bible.getBiblename());
        }
        return result;
    }

    public List<XMLBIBLE> getBibles() {
        return this.bibles;
    }

    public Optional<BIBLEBOOK> getBookByName(XMLBIBLE bible, String bookName) {
        Optional<BIBLEBOOK> result = Optional.empty();
        Optional<JAXBElement<BIBLEBOOK>> optBook = bible.getBIBLEBOOK().stream()
                .filter(e -> e.getValue().getBname().equals(bookName))
                .findFirst();
        if (optBook.isPresent()) {
            result = Optional.of(optBook.get().getValue());
        }
        return result;
    }

    public Optional<CHAPTER> getChapter(BIBLEBOOK book, Integer chapterNo) {
        Optional<CHAPTER> result = Optional.empty();
        Optional<JAXBElement<CHAPTER>> optChapter = book.getCHAPTER().stream()
                .filter(e -> e.getValue().getCnumber().intValue() == chapterNo)
                .findFirst();
        if (optChapter.isPresent()) {
            result = Optional.of(optChapter.get().getValue());
        }
        return result;
    }
}
