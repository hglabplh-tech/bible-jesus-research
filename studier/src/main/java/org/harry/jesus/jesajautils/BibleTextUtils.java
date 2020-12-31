package org.harry.jesus.jesajautils;

import com.google.common.io.LineReader;
import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.VERS;
import generated.XMLBIBLE;
import javafx.scene.control.IndexRange;
import jesus.harry.org.versnotes._1.Vers;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.jetbrains.annotations.NotNull;
import org.pmw.tinylog.Logger;

import javax.xml.bind.JAXBElement;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.*;

public class BibleTextUtils {
    public final static List<String> bibleFnames = Arrays.asList(
            "bible_luther1912.xml",
            "SF_2012-08-14_DEUTSCH_LUT_1545_LH_(LUTHER 1545 (LETZTE HAND)).xml",
            "SF_2009-01-20_GER_ELB1905_(ELBERFELDER 1905).xml",
            "SF_2014-09-30_GER_NEU_(NEUE EVANGELISTISCHE ÜBERSETZUNG (NEÜ)).xml",
            "SF_2015-08-16_ENG_NHEBYHWH_(NEW HEART ENGLISH BIBLE (YHWH)).xml",
            "SF_2009-01-20_SPA_RVA_(REINA VALERA 1989).xml",
            "SF_2009-01-20_ENG_UKJV_(UPDATED KING JAMES VERSION).xml"

    );

    List<String> bibleHashes = Arrays.asList(
            "d78b23abc90a1e9fd44ef796953ea552b0f8e2f0d15cc96b04217ba11e436f0d",
            "66fe26a722e9fb908e1baf631ea7bc9cd8e9366f4afe65994e4042421728ea3a",
            "336b9137ecb8fda38688c408d7dc6d4c36f4caa5a3a2599ac200c9ab8d198abf",
            "fa192749106f8ba3a27552f394d01a11904488cfb269c8244313c1170c096938",
            "faba332e53dd17ead43106aabe4ab4605dcb3b0a8b10040d670fca913680bf83",
            "6e4a8381e4377f54e29d592eafeba72a92fe0b8384bdd598e6fd52c258eb4cd4",
            "ca6d02c950945462d543dd6a85e3c46e52321c646abe2dd204088f2449d9bbfc");

    public final static List<Integer> fuzzyIndex = Arrays.asList(3);

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

    public String generateVersEntry(BibleFulltextEngine.BibleTextKey key,
                                           String versText) {
        String versLink =  "";
        versLink = buildVersLink(key);
        String result = versLink + versText;
        return result;
    }

    private String buildVersLink(BibleFulltextEngine.BibleTextKey key) {
        String versLink = "";
        List<String> csv = this.getBookLabels();
        Optional<String> book = csv.stream()
                .filter(e -> e.contains(key.getBook().toString()))
                .findFirst();
        if (book.isPresent()) {
            String [] split = book.get().split(",");
            versLink = "[" + split[1] + " " + key.getChapter() + "," + key.getVers() + "]: ";
        }
        return versLink;
    }

    public static List<Tuple<Integer, Integer>> detectVersesRangeForLink(List<Object> verses) {
        List<Tuple<Integer, Integer>> result = new ArrayList<>();
        int sequence;
        sequence = getSequence(verses.get(0));
        int rangeStart = sequence;
        int rangeEnd = sequence;
        for (int index = 1; index < verses.size(); index++) {
            Integer versNo = getSequence(verses.get(index));
            if (versNo == sequence + 1) {
                rangeEnd = versNo;
                sequence++;
            } else {
                if (rangeEnd == rangeStart) {
                    result.add(new Tuple<Integer, Integer>(rangeStart, 0));
                } else {
                    result.add(new Tuple<Integer, Integer>(rangeStart, rangeEnd));
                }
                sequence = versNo;
                rangeStart = sequence;
                rangeEnd = sequence;
            }
        }
        if (rangeEnd == rangeStart) {
            result.add(new Tuple<Integer, Integer>(rangeStart, 0));
        } else {
            result.add(new Tuple<Integer, Integer>(rangeStart, rangeEnd));
        }
        return result;
    }

    private static int getSequence(Object vers) {
        int sequence;
        if (vers instanceof Integer) {
            sequence = (Integer) vers;
        } else {
            sequence = ((BigInteger)vers).intValue();
        }
        return sequence;
    }

    public static String generateVersEntry(BibleTextUtils utils, Vers vers, String versText) {
        List<String> csv = utils.getBookLabels();
        Optional<String> book = csv.stream()
                .filter(e -> e.contains(vers.getBook().toString()))
                .findFirst();
        if (book.isPresent()) {
            String [] split = book.get().split(",");
            String versLink = "[" + split[1] + " " + vers.getChapter() + "," + vers.getVers() + "]: ";
            String result = versLink + versText;
            return result;
        } else {
            return versText;
        }
    }

    @NotNull
    public static Vers generateVers(BookLabel actBook, Integer actChapter,
                                    FoldableStyledArea area, Map<Integer, IndexRange> selectedVersesMap,
                                    Integer versNo) {
        Vers vers = new Vers();
        vers.getVers().add(BigInteger.valueOf((long) versNo));
        vers.setChapter(BigInteger.valueOf(actChapter));
        vers.setBook(BigInteger.valueOf(actBook.getBookNumber()));
        IndexRange range = selectedVersesMap.get(versNo);
        String vText = area.getText(range);
        vers.setVtext(vText);
        return vers;
    }

    @NotNull
    public static Vers generateVerses(BibleTextUtils utils, BookLabel actBook, Integer actChapter,
                                      FoldableStyledArea area, Map<Integer, IndexRange> selectedVersesMap) {
        Vers vers = new Vers();
        vers.setChapter(BigInteger.valueOf(actChapter));
        String links = LinkHandler.buildVersLinkEnhanced(utils, actBook.getBookNumber(),
                actChapter,
                new ArrayList(selectedVersesMap.keySet()));
        List<BookLink> bookLinks = LinkHandler.parseLinks(utils, links);
        vers.setBook(BigInteger.valueOf(actBook.getBookNumber()));
        StringBuffer versBuffer = new StringBuffer();
        int index = 0;
        String [] linkArr = links.split(";");
        versBuffer.append(linkArr[index] + ": ");
        for (Map.Entry<Integer, IndexRange> entry: selectedVersesMap.entrySet()) {

            Integer versNo = entry.getKey();
            vers.getVers().add(BigInteger.valueOf((long) versNo));
            if ((index < (bookLinks.size())) && bookLinks.get(index).getVerses().contains(versNo)) {
                String vText = area.getText(entry.getValue());
                versBuffer.append(vText);
            } else if ((index + 1) < (linkArr.length)) {
                index++;
                versBuffer.append(linkArr[index] + ": ");
                String vText = area.getText(entry.getValue());
                versBuffer.append(vText);
            }

        }
        vers.setVtext(versBuffer.toString());
        return vers;
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

    public List<String> getBibleHashes() {
        return this.bibleHashes;
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

    public BookLabel getBookLabelAsClass(String label) {
        return new BookLabel(label);
    }

    public Optional<BIBLEBOOK> getBookByLabel(XMLBIBLE bible, String bookLabel) {
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

    public List<BIBLEBOOK> getBooks(XMLBIBLE bible) {
        List<BIBLEBOOK> result = new ArrayList<>();
        for (JAXBElement<BIBLEBOOK> xmlBook: bible.getBIBLEBOOK()) {
            result.add(xmlBook.getValue());
        }
        return result;
    }

    public List<CHAPTER> getChapters(BIBLEBOOK book) {
        List<CHAPTER> result = new ArrayList<>();
        for (JAXBElement<CHAPTER> xmlChapter: book.getCHAPTER()) {
            result.add(xmlChapter.getValue());
        }
        return result;
    }

    public Map<BibleFulltextEngine.BibleTextKey, String> getVerses(CHAPTER chapter, Integer bookNumber) {
        Map<BibleFulltextEngine.BibleTextKey, String> result = new LinkedHashMap<>();
        for (JAXBElement xmlVers: chapter.getPROLOGOrCAPTIONOrVERS()) {
            if (xmlVers.getValue() instanceof VERS) {
              VERS vers = (VERS)xmlVers.getValue();
              StringBuffer buffer = new StringBuffer();
              Integer versNo = vers.getVnumber().intValue();
              for (Object content: vers.getContent()) {
                  if (content instanceof String) {
                      buffer.append((String)content);
                  }
              }

              BibleFulltextEngine.BibleTextKey keyObj =
                      new BibleFulltextEngine.BibleTextKey(bookNumber, chapter.getCnumber().intValue(), versNo);
              result.put(keyObj, buffer.toString());
            }
        }
        return result;
    }

    public Map.Entry<BibleFulltextEngine.BibleTextKey, String> getVersEntry(CHAPTER chapter,
                                                                      BibleFulltextEngine.BibleTextKey key) {
        StringBuffer buffer = new StringBuffer();
        Map.Entry<BibleFulltextEngine.BibleTextKey, String> result = null;
        for (JAXBElement xmlVers: chapter.getPROLOGOrCAPTIONOrVERS()) {
            if (xmlVers.getValue() instanceof VERS) {
                VERS vers = (VERS)xmlVers.getValue();
                Integer versNo = vers.getVnumber().intValue();
                if (versNo.equals(key.getVers())) {
                    for (Object content : vers.getContent()) {
                        if (content instanceof String) {
                            buffer.append((String) content);
                        }
                    }
                }



            }
        }
        result = new Map.Entry<BibleFulltextEngine.BibleTextKey, String>() {
            @Override
            public BibleFulltextEngine.BibleTextKey getKey() {
                return key;
            }

            @Override
            public String getValue() {
                return buffer.toString();
            }

            @Override
            public String setValue(String value) {
                return buffer.toString();
            }
        };
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

    public String generateVersLink(List<Vers> verses, BookLabel label) {
        StringBuffer buffer = new StringBuffer();
        for (Vers vers : verses) {
            for (BigInteger versNo: vers.getVers()) {
                buffer.append("[");
                buffer.append(label.getLongName() + " ");
                buffer.append(vers.getChapter() + ",");
                buffer.append(versNo.toString(10));
                buffer.append("]");
            }
        }
        return buffer.toString();
    }

    public static class BookLink {

        private final String bookLabel;

        private final Integer chapter;

        private final List<Integer> verses;

        public BookLink(String bookLabel, Integer chapter, List<Integer> verses) {
            this.bookLabel = bookLabel;
            this.chapter = chapter;
            this.verses = verses;
        }

        public String getBookLabel() {
            return bookLabel;
        }

        public Integer getChapter() {
            return chapter;
        }

        public List<Integer> getVerses() {
            return verses;
        }
    }

    public static class BookLabel {

        private Integer bookNumber = 0;

        private String shortName = "";

        private String longName = "";

        public BookLabel(String label) {
            String [] temp = label.split(",");
            bookNumber = Integer.parseInt(temp[0]);
            longName = temp[1];
            shortName = temp[2];
        }

        public Integer getBookNumber() {
            return bookNumber;
        }

        public String getShortName() {
            return shortName;
        }

        public String getLongName() {
            return longName;
        }
    }


}
