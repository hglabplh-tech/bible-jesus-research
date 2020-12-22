package org.harry.jesus.jesajautils;

import com.google.common.io.LineReader;
import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.VERS;
import generated.XMLBIBLE;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.pmw.tinylog.Logger;

import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class BibleTextUtils {
    public final static List<String> bibleFnames = Arrays.asList(
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
        int endIndex = linkString.indexOf(" ", start);
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


}
