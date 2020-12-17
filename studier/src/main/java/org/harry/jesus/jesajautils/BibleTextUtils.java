package org.harry.jesus.jesajautils;

import com.google.common.io.LineReader;
import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.XMLBIBLE;
import org.pmw.tinylog.Logger;

import javax.xml.bind.JAXBElement;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    List<String> bookLabels = new ArrayList<>();

    public BibleTextUtils() {
        try {
            InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream("/booksinfo.csv"));
            LineReader lreader = new LineReader(reader);
            String line = lreader.readLine();
            while (line != null && !line.isEmpty()) {
                bookLabels.add(line);
                line = lreader.readLine();
            }
            reader.close();

            for (String bibleName: bibleFnames) {
                InputStream bibleIN = BibleTextUtils.class.getResourceAsStream("/" + bibleName);
                XMLBIBLE actBible = BibleReader.loadBible(bibleIN);
                bibles.add(actBible);
            }
        } catch (Exception ex) {
            Logger.trace("Exception occured loading bibles");
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

    public List<String> getBibleBookInfo(XMLBIBLE bible) {
        List<String> csvBooksList = new ArrayList<>();
        List<JAXBElement<BIBLEBOOK>> books = bible.getBIBLEBOOK();
        for (JAXBElement<BIBLEBOOK> jBook: books) {
            BIBLEBOOK book = jBook.getValue();
            csvBooksList.add(book.getBnumber().toString() + "," + book.getBname() + "," + book.getBsname());

        }

        return csvBooksList;
    }

    public List<String> getBookLabels() {
        return this.bookLabels;
    }

    public Optional<BIBLEBOOK> getBookByName(XMLBIBLE bible, String bookLabel) {
        String [] labArr = bookLabel.split(",");
        Integer bookNo = Integer.valueOf(labArr[0]);
        Optional<BIBLEBOOK> result = Optional.empty();
        Optional<JAXBElement<BIBLEBOOK>> optBook = bible.getBIBLEBOOK().stream()
                .filter(e -> e.getValue().getBnumber().intValue() == bookNo)
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
