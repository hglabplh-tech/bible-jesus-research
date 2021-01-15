package org.harry.jesus.jesajautils;

import com.google.common.io.LineReader;
import generated.*;
import generated.Dictionary;
import javafx.scene.control.IndexRange;
import jesus.harry.org.versnotes._1.Vers;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.harry.jesus.synchjeremia.AccordanceRef;
import org.harry.jesus.synchjeremia.ApplicationProperties;
import org.harry.jesus.synchjeremia.BibleRef;
import org.harry.jesus.synchjeremia.BibleThreadPool;

import org.pmw.tinylog.Logger;

import javax.xml.bind.JAXBElement;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class BibleTextUtils {


    List<BibleBookInstance> bibleInstances = new ArrayList<>();
    List<DictionaryInstance> dictInstances = new ArrayList<>();
    List<String> bookLabels = new ArrayList<>();

    XMLBIBLE selected = null;

    Map<Integer, BookLabel> bookLabMap = new LinkedHashMap<>();


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
            buidBookLabMap();
            File biblePath = new File(ApplicationProperties.getApplicationBiblesDir());
            File accordancePath = new File(ApplicationProperties.getApplicationAccordanceDir());
            BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();
            loadBiblesDownLoaded(biblePath, context);
            //loadAccordancesDownLoaded(accordancePath,context);
            selected = bibleInstances.get(0).getBible();


        } catch (Exception ex) {
            Logger.trace("Exception occured loading bibles");
        }
    }

    private void buidBookLabMap() {
        for (String label: bookLabels) {
            BookLabel labAsClass = getBookLabelAsClass(label);
            Integer book = Integer.parseInt(label.split(",")[0]);
            this.bookLabMap.put(book, labAsClass);
        }
    }

    public XMLBIBLE getSelected() {
        return selected;
    }

    public BibleTextUtils setSelected(XMLBIBLE selected) {
        this.selected = selected;
        return this;
    }

    public Map<Integer, BookLabel> getBookLabMap() {
        return bookLabMap;
    }

    private void loadBiblesDownLoaded(File biblePath, BibleThreadPool.ThreadBean context) {
        for (File bibleFile : biblePath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.contains(".xml")) {
                    return true;
                }
                return false;
            }
        })) {
            BibleRef newRef = new BibleRef().setPathToBook(bibleFile.getAbsolutePath());
            context.getBibleRefList().add(newRef);
        }
        loadBiblesFromRef(context.getBibleRefList());
    }

    public void loadAccordancesDownLoaded(File accordancePath, BibleThreadPool.ThreadBean context) {
        for (File accordanceFile : accordancePath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.contains(".xml")) {
                    return true;
                }
                return false;
            }
        })) {

            Tuple<Dictionary, String> actAccordance = BibleReader.loadBibleAccordance(accordanceFile);
            String fileName = accordanceFile.getName();
            fileName = fileName.substring(0, fileName.indexOf(".xml"));
            AccordanceRef accRef = new AccordanceRef()
                    .setHashValue(actAccordance.getSecond())
                    .setFilename(fileName)
                    .setPathToBook(accordanceFile.getAbsolutePath());
            AccordanceUtil util = new AccordanceUtil(
                    Arrays.asList(new Tuple<String, Dictionary>(fileName, actAccordance.getFirst())));
            Optional <BibleBookInstance> instance = bibleInstances.stream().filter(e -> {
                Optional<Tuple<String,Dictionary>> opt = util.findAccordance(actAccordance.getFirst(),
                        e.getBible().getINFORMATION().getValue());
                return opt.isPresent();
                    }).findFirst();
            if (instance.isPresent()) {
                instance.get().setOptDictAccRefTuple(actAccordance.getFirst(), accRef);
            }
            String id = AccordanceUtil.getIdFromInfo(actAccordance.getFirst().getINFORMATION());
            accRef.setDictionaryID(id);
            dictInstances.add(new DictionaryInstance(accRef, actAccordance.getFirst()));
           /* AccordanceGenThread thread =
                    new AccordanceGenThread(this,
                            actAccordance.getFirst(), fileName, accordancePath);
            thread.start(); */

        }

    }


    private void loadBiblesFromRef(List<BibleRef> references) {

        for (BibleRef ref : references) {
            Tuple<XMLBIBLE, String> actBible = BibleReader.loadBible(new File(ref.getPathToBook()));
            ref.setBibleID(AccordanceUtil
                    .getIdFromBibleInfo(actBible
                            .getFirst()
                            .getINFORMATION()
                            .getValue())).setHashValue(actBible.getSecond());
            bibleInstances.add(new BibleBookInstance(ref, actBible.getFirst()));
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
        for (BibleBookInstance inst : bibleInstances) {
            result.add(inst.getBible().getBiblename());
        }
        return result;
    }

    public List<BibleBookInstance> getBibleInstances() {
        return Collections.unmodifiableList(bibleInstances);
    }

    public List<DictionaryInstance> getDictInstances() {
        return Collections.unmodifiableList(dictInstances);
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
        BookLabel label = new BookLabel(bookLabel);
        Integer bookNo = label.getBookNumber();
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
                            String text = (String) content;
                            buffer.append(text);
                        } else if (content instanceof JAXBElement) {
                            Class jaxbClazz = ((JAXBElement<?>) content).getDeclaredType();
                            if (jaxbClazz.getName().equals(STYLE.class.getName())) {
                                STYLE styled = (STYLE) ((JAXBElement<?>) content).getValue();
                                for (Object styledContent : styled.getContent()) {
                                    if (styledContent instanceof String) {
                                        String text = (String) styledContent;
                                        buffer.append(text);
                                    }

                                }

                            }
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

    public static class BookLink implements Serializable {

        private final String bookLabel;

        private final Integer chapter;

        private final List<Integer> verses;

        public BookLink(String bookLabel, Integer chapter) {
            this(bookLabel, chapter, Arrays.asList(1));
        }

        public BookLink(String bookLabel, Integer chapter, List<Integer> verses) {
            this.bookLabel = bookLabel;
            this.chapter = chapter;
            this.verses = verses;
        }

        public String getBookLabel() {
            return bookLabel;
        }

        public BookLabel getBookLabelClass() {
            return new BookLabel(bookLabel);
        }

        public Integer getChapter() {
            return chapter;
        }

        public List<Integer> getVerses() {
            return verses;
        }

        @Override
        public String toString() {
            StringBuffer buffer = new StringBuffer();
            List<Integer> versList = this.getVerses();
            String vers = new Integer(1).toString();
            if (versList.size() != 0) {
                vers = versList.get(0).toString();
            } else {

            }
            buffer.append("[")
                    .append(this.getBookLabelClass().longName)
                    .append(" " + this.getChapter())
                    .append("," + vers)
                    .append("]");
            return buffer.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BookLink)) return false;
            BookLink bookLink = (BookLink) o;
            return getBookLabel().equals(bookLink.getBookLabel()) && getChapter().equals(bookLink.getChapter()) && getVerses().equals(bookLink.getVerses());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBookLabel(), getChapter(), getVerses());
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


    public static class BibleBookInstance {

        private final BibleRef bibleRef;

        private final XMLBIBLE bible;

        private Optional<Tuple<Dictionary, AccordanceRef>> optDictAccRefTuple = Optional.empty();

        public BibleBookInstance(BibleRef bibleRef, XMLBIBLE bible) {
            this.bibleRef = bibleRef;
            this.bible = bible;
        }

        public BibleRef getBibleRef() {
            return bibleRef;
        }

        public XMLBIBLE getBible() {
            return bible;
        }

        public Optional<Tuple<Dictionary, AccordanceRef>> getOptDictAccRefTuple() {
            return optDictAccRefTuple;
        }

        public BibleBookInstance setOptDictAccRefTuple(Dictionary dictionary,
                                                       AccordanceRef accRef) {
            this.optDictAccRefTuple = Optional.of(new Tuple<>(dictionary, accRef));
            return this;
        }
    }


    public static class DictionaryInstance {

        private final AccordanceRef dictionaryRef;

        private final Dictionary dictionary;

        public DictionaryInstance(AccordanceRef dictionaryRef, Dictionary dictionary) {
            this.dictionaryRef = dictionaryRef;
            this.dictionary = dictionary;
        }

        public AccordanceRef getDictionaryRef() {
            return dictionaryRef;
        }

        public Dictionary getDictionary() {
            return dictionary;
        }

        @Override
        public String toString() {
            return dictionaryRef.getFilename() + " : " + dictionaryRef.getDictionaryID();
        }

    }
}
