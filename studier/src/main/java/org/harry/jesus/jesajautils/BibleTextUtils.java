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
            "SF_2015-08-16_ENG_NHEBYHWH_(NEW HEART ENGLISH BIBLE (YHWH)).xml",
            "SF_2009-01-20_SPA_RVA_(REINA VALERA 1989).xml",
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

    public static String buildVersLinkEnhanced(BibleTextUtils utils, Integer bookNumber, Integer chapter, List versNoList) {
        String versLink = "";
        StringBuffer linkBuffer = new StringBuffer();
        List<String> csv = utils.getBookLabels();
        Optional<String> book = csv.stream()
                .filter(e -> e.contains(bookNumber.toString()))
                .findFirst();
        if (book.isPresent()) {
            String [] split = book.get().split(",");
            List<Tuple<Integer, Integer>> rangesList = detectVersesRangeForLink(versNoList);
            boolean first = true;
            for (Tuple<Integer, Integer> range: rangesList) {
                if (!first) {
                    linkBuffer.append(";");
                }
                if (range.getSecond() == 0) {
                    versLink = "[" + split[1] + " "
                            + chapter
                            + ","
                            + range.getFirst() + "]";
                } else {
                    versLink = "[" + split[1] + " "
                            + chapter
                            + ","
                            + range.getFirst() + "-" + range.getSecond() + "]";
                }
                first = false;
                linkBuffer.append(versLink);
            }
        }
        return linkBuffer.toString();
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
        String links = buildVersLinkEnhanced(utils, actBook.getBookNumber(),
                actChapter,
                new ArrayList(selectedVersesMap.keySet()));
        List<BookLink> bookLinks = utils.parseLinks(links);
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

    public List<BookLink> parseLinks(String text) {
        List<BookLink> links = new ArrayList<>();
        int start = 0;
        int startLink = 0;
        while (startLink != -1) {
            startLink = text.indexOf("[", start);
            if (startLink != -1) {
                int endLink = text.indexOf("]", startLink);
                if (endLink != -1) {
                    String linkString = text.substring(startLink + 1, endLink);
                    Optional<BookLink> link = buildLink(linkString);
                    if (link.isPresent()) {
                        links.add(link.get());
                    }
                    start = endLink + 1;
                } else {
                    startLink = -1;
                }
            }
        }
        return links;
    }

    private Optional<BookLink> buildLink(String linkString) {
        int start = 0;
        int endIndex = linkString.lastIndexOf(" ");
        if (endIndex > -1) {
            String bookStr = linkString.substring(start, endIndex);
            Optional<String> label = this.getBookLabels().stream().filter(e ->
                    e.contains(bookStrMapping(bookStr))).findFirst();
            String temp = linkString.substring(endIndex + 1);
            endIndex = temp.indexOf(",");
            if (endIndex > -1) {
                String chapStr = temp.substring(0, endIndex).trim();
                start = endIndex + 1;
                boolean matches = chapStr.matches("[0-9]+");
                boolean processed = false;
                if (matches) {
                    Integer chapter = Integer.parseInt(chapStr);
                    String versesStr = temp.substring(start);
                    String[] single = versesStr.split("\\.");
                    String[] range = versesStr.split("-");
                    List<Integer> verses = new ArrayList<>();
                    if (single.length == 1 && range.length == 1) {
                        String numtemp = "";
                        if (single.length == 1) {
                            numtemp = single[0];
                        } else {
                            numtemp = range[0];
                        }

                        if (numtemp.matches("[0-9]+")) {
                            verses.add(Integer.parseInt(numtemp));
                            processed = true;
                        }
                    }
                    else if (range.length == 2) {
                        String startStr = range[0];
                        String endStr = range[1];
                        int sRange = Integer.parseInt(startStr);
                        int eRange = Integer.parseInt(endStr);
                        for (Integer vers = sRange; vers <= eRange; vers++) {
                            verses.add(vers);
                        }
                        processed = true;
                    } else if (single.length >1) {
                        for (String verseStr: single) {
                            int vers = Integer.parseInt(verseStr);
                            verses.add(vers);
                        }
                        processed = true;
                    }
                    if (processed) {
                        if (label.isPresent()) {
                            return Optional.of(new BookLink(label.get(), chapter, verses));
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    private String bookStrMapping(String book) {
        Map<String, String> map = new HashMap<>();
        map.put("1.Mo", "1. Mose");
        map.put("1.Mose", "1. Mose");
        map.put("2.Mo", "2. Mose");
        map.put("2.Mose", "2. Mose");
        map.put("3.Mo", "3. Mose");
        map.put("3.Mose", "3. Mose");
        map.put("4.Mo", "4. Mose");
        map.put("4.Mose", "4. Mose");
        map.put("5.Mo", "5. Mose");
        map.put("5.Mose", "5. Mose");
        map.put("1.Ch", "1. Chr");
        map.put("2.Ch", "2. Chr");
        map.put("1.Chr", "1. Chr");
        map.put("2.Chr", "2. Chr");
        if (map.get(book) != null) {
            return map.get(book);
        } else {
            return book;
        }

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
