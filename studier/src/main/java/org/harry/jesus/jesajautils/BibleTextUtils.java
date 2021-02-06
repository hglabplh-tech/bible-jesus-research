package org.harry.jesus.jesajautils;

import com.google.common.io.LineReader;
import generated.*;
import generated.Dictionary;
import javafx.scene.control.IndexRange;
import jesus.harry.org.versnotes._1.Vers;
import org.apache.commons.io.IOUtils;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.jesajautils.configjaxbser.BaseConfig;
import org.harry.jesus.jesajautils.configjaxbser.BiblesDictConfig;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.harry.jesus.jesajautils.configjaxbser.DictionaryRef;
import org.harry.jesus.jesajautils.configjaxbser.BibleRef;
import org.harry.jesus.jesajautils.judaerrmsg.ExceptionAlert;
import org.harry.jesus.synchjeremia.BibleThreadPool;

import org.harry.jesus.synchjeremia.SynchThread;
import org.pmw.tinylog.Logger;

import javax.xml.bind.JAXBElement;
import java.io.*;
import java.math.BigInteger;
import java.util.*;


/**
 * Class to read bibles from zefania XML and to read Dictionaries
 * and access their content
 *
 * @author Harald Glab-Plhak (C) Harald Glab-Plhak
 */
public class BibleTextUtils {

    private static String biblesResDir = "/bibles/";
    private static String dictResDir = "/bibles/dictionaries/";

    private static List<String> installedDicts = Arrays.asList(
            "SF_2005-12-09_GER_LUTH1912-KOK_(DIE LUTHERBIBEL VON 1912 - KONKORDANZ).xml",
            "SF_2006-03-08_GER_ELB1905-KOK_(DIE ELBERFELDERBIBEL VON 1905 - KONKORDANZ).xml",
            "HarrysComments.xml"

    );
    private static List<String> installedBibles = Arrays.asList(
            "SF_2009-01-20_GER_ELB1905_(ELBERFELDER 1905).xml",
            "SF_2009-01-20_GER_LUTH1912_(LUTHER 1912).xml",
            "SF_2009-01-20_GER_SCH1951_(SCHLACHTER 1951).xml",
            "SF_2009-01-22_GER_KNT_(KONKORDANTES NT).xml",
            "SF_2009-01-20_GER_ILGRDE_(INTERLINEARÜBERSETZUNG).xml",
            "SF_2009-01-22_GER_ELB1905STR_(ELBERFELDER 1905).xml",
            "SF_2009-01-20_GER_LUTH1545STR_(LUTHER 1545 MIT STRONGS).xml"
    );
    /**
     * bible instances list
     */
    private List<BibleBookInstance> bibleInstances = new ArrayList<>();

    /**
     * dictionary instances list
     */
    private List<DictionaryInstance> dictInstances = new ArrayList<>();

    /**
     * The book labels String list {bookNo, book Long Name, book Short Name}
     */
    private List<String> bookLabels = new ArrayList<>();

    /**
     *  association between dictionaries and bibles
     */
    private Set<Tuple<BibleRef, DictionaryRef>> assocSet = new HashSet<>();

    /**
     * The actual selected bible
     */
    private XMLBIBLE selected = null;

    /**
     * The private singleton instance for a Object instance of this class
     */
    private static BibleTextUtils myInst  = null;


    /**
     * A map with the key bookNo and the label(class) as value
     */
    private  Map<Integer, BookLabel> bookLabMap = new LinkedHashMap<>();


    /**
     * The constructor here the stuff is loaded
     */
    private BibleTextUtils() {
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
            SynchThread.loadApplicationSettings(BibleThreadPool.getContext());
            BaseConfig base = BibleThreadPool.getContext().getAppSettings().getBaseConfig();
            File biblePath = new File(base.getBiblesDir());
            File dictionariesPath = new File(base.getDictionariesDir());
            if (!biblePath.exists()) {
                biblePath.mkdirs();
            }
            if (!dictionariesPath.exists()) {
                dictionariesPath.mkdirs();
            }
            copyBibles(biblePath);
            copyDicts(dictionariesPath);
            BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();
            loadBiblesDownLoaded(biblePath, context);
            loadAccordancesDownLoaded(dictionariesPath,context);
            if (bibleInstances.size() > 0) {
                selected = bibleInstances.get(0).getBible();
            } else {
                selected = null;
            }


        } catch (Exception ex) {
            ExceptionAlert alert = new ExceptionAlert(ex);
            alert.showAndWait();
            Logger.trace("Exception occured loading bibles");
        }
    }

    private void copyBibles(File biblePath) throws IOException {
        for(String bibleName: installedBibles) {
            InputStream input = this.getClass()
                    .getResourceAsStream(biblesResDir + bibleName);
            writeIfNotThere(biblePath, bibleName, input);
        }
    }

    private void copyDicts(File dictPath) throws IOException {
        for(String dictName: installedDicts) {
            InputStream input = this.getClass()
                    .getResourceAsStream(dictResDir + dictName);
            writeIfNotThere(dictPath, dictName, input);
        }
    }

    private void writeIfNotThere(File path, String fileName, InputStream input) throws IOException {
        if (input != null) {
            File outFile = new File(path, fileName);
            if (!outFile.exists()) {
                FileOutputStream outStream = new FileOutputStream(outFile);
                IOUtils.copy(input, outStream);
            }
        }
    }

    /**
     * Return the singleton instance if there is none the one and only is created
     *
     * @return the singleton
     */
    public static BibleTextUtils getInstance()  {
        if (myInst == null) {
            myInst = new BibleTextUtils();
        }
        return myInst;
    }


    /**
     * Build the book-label-map out of the book label strings
     */
    private void buidBookLabMap() {
        for (String label: bookLabels) {
            BookLabel labAsClass = getBookLabelAsClass(label);
            Integer book = labAsClass.getBookNumber();
            this.bookLabMap.put(book, labAsClass);
        }
    }

    /**
     * Return the selected bible
     *
     * @return the selected bible
     */
    public XMLBIBLE getSelected() {
        return selected;
    }

    /**
     * Set the actual selected bible
     *
     * @param selected the new selected bible
     * @return the bible text utils instance builder setter
     */
    public BibleTextUtils setSelected(XMLBIBLE selected) {
        this.selected = selected;
        return this;
    }

    /**
     * Return the book label map
     *
     * @return the book label map
     */
    public Map<Integer, BookLabel> getBookLabMap() {
        return bookLabMap;
    }

    /**
     * Load the bibles placed in the bibles directory by the user
     * @param biblePath the bibles path
     * @param context the thread-context
     */
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

    /**
     * Search selected for dict.
     *
     * @param dictionaryRef the dictionary ref
     */
    public static void searchSelectedForDict(DictionaryRef dictionaryRef) {
        XMLBIBLE selected = getInstance().getSelected();
        BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();
        BiblesDictConfig dictConf = context.getAppSettings().getDictConfig();
        Optional<BibleRef> refFound = Optional.empty();
        for (Map.Entry<DictionaryRef, BibleRef> entry : dictConf.getDictBibleMapping().entrySet()) {
            if (entry.getKey().equals(dictionaryRef)) {
                refFound = Optional.of(entry.getValue());
            }
        }
        if (refFound.isPresent()) {
            for (BibleBookInstance bible: BibleTextUtils.getInstance().getBibleInstances()) {
                if (bible.getBibleRef().equals(refFound.get())) {
                    selected = bible.getBible();
                }
            }
        }
        BibleTextUtils.getInstance().setSelected(selected);
    }

    /**
     * Search selected for bible optional.
     *
     * @param bibleRef the bible ref
     * @return the optional
     */
    public static Optional<DictionaryInstance> searchSelectedForBible(BibleRef bibleRef) {
        BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();
        BiblesDictConfig dictConf = context.getAppSettings().getDictConfig();
        Optional<DictionaryRef> refFound = Optional.empty();
        for (Map.Entry<DictionaryRef, BibleRef> entry : dictConf.getDictBibleMapping().entrySet()) {
            if (entry.getValue().equals(bibleRef)) {
                refFound = Optional.of(entry.getKey());
            }
        }
        if (refFound.isPresent()) {
            for (DictionaryInstance dictionary: BibleTextUtils.getInstance().getDictInstances()) {
                if (dictionary.getDictionaryRef().equals(refFound.get())) {
                    return Optional.of(dictionary);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Load the dictionaries placed in the dictionary directory of the user
     *
     * @param accordancePath the dictionaries path
     * @param context        the thread context
     */
    public void loadAccordancesDownLoaded(File accordancePath, BibleThreadPool.ThreadBean context) {
        dictInstances.clear();
        for (File accordanceFile : accordancePath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.contains(".xml")) {
                    return true;
                }
                return false;
            }
        })) {

            Tuple<Dictionary, String> actAccordance = BibleReader.loadBibleDictionary(
                   accordanceFile);
            String fileName = accordanceFile.getName();
            fileName = fileName.substring(0, fileName.indexOf(".xml"));
            DictionaryRef accRef = new DictionaryRef()
                    .setHashValue(actAccordance.getSecond())
                    .setFilename(fileName)
                    .setPathToBook(accordanceFile.getAbsolutePath());
            BibleDictUtil util = new BibleDictUtil(
                    Arrays.asList(new Tuple<String, Dictionary>(fileName, actAccordance.getFirst())));
            Optional<BibleBookInstance> instance = bibleInstances.stream().filter(e -> {
                Optional<Tuple<String, Dictionary>> opt = util.findAccordance(actAccordance.getFirst(),
                        e.getBible().getINFORMATION().getValue());
                return opt.isPresent();
            }).findFirst();
            if (instance.isPresent()) {
                instance.get().setOptDictAccRefTuple(actAccordance.getFirst(), accRef);
            }
            String id = BibleDictUtil.getIdFromInfo(actAccordance.getFirst().getINFORMATION());
            String name = BibleDictUtil.getNameFromInfo(actAccordance.getFirst().getINFORMATION());
            accRef.setDictionaryID(id);
            accRef.setDictionaryName(name);
            dictInstances.add(new DictionaryInstance(accRef, actAccordance.getFirst()));
        }

    }


    /**
     * Really load the bible from the reference object
     * @param references the bible reference object
     */
    private void loadBiblesFromRef(List<BibleRef> references) {

        for (BibleRef ref : references) {
            Tuple<XMLBIBLE, String> actBible = BibleReader.loadBible(new File(ref.getPathToBook()));
            ref.setBibleID(BibleDictUtil
                    .getIdFromBibleInfo(actBible
                            .getFirst()
                            .getINFORMATION()
                            .getValue())).setHashValue(actBible.getSecond())
                    .setBiblename(actBible.getFirst().getBiblename());
            bibleInstances.add(new BibleBookInstance(ref, actBible.getFirst()));
    }

    }

    /**
     * Build a verse text entry from a single search result
     *
     * @param key      the bible text key which was from the search result
     * @param versText the text of the verse
     * @return The entry as string
     */
    public String generateVersEntry(BibleFulltextEngine.BibleTextKey key) {
        String versLink =  "";
        versLink = buildVersLink(key);
        String result = versLink + key.getVerseText();
        return result;
    }

    /**
     * Build a link in internal format from a single search result
     * @param key the search result key
     * @return the generated link
     */
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

    /**
     * Detect verses range for link list.
     *
     * @param verses the verses
     * @return the list
     */
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

    /**
     * Get a Integer from Integer or BigIntegewr
     * @param vers the Integer or BigInteger
     * @return the Integer
     */
    private static Integer getSequence(Object vers) {
        int sequence;
        if (vers instanceof Integer) {
            sequence = (Integer) vers;
        } else {
            sequence = ((BigInteger)vers).intValue();
        }
        return sequence;
    }

    /**
     * Generate verses vers.
     *
     * @param utils             the utils
     * @param actBook           the act book
     * @param actChapter        the act chapter
     * @param area              the area
     * @param selectedVersesMap the selected verses map
     * @return the vers
     */
    public static Vers generateVerses(BibleTextUtils utils, BookLabel actBook, Integer actChapter,
                                      FoldableStyledArea area, Map<Integer, IndexRange> selectedVersesMap) {
        Vers vers = new Vers();
        vers.setChapter(BigInteger.valueOf(actChapter));
        String links = LinkDetector.buildVersLinkEnhanced(utils, actBook.getBookNumber(),
                actChapter,
                new ArrayList(selectedVersesMap.keySet()));
        List<BookLink> bookLinks = LinkDetector.parseLinks(utils, links);
        vers.setBook(BigInteger.valueOf(actBook.getBookNumber()));
        StringBuffer versBuffer = new StringBuffer();
        int index = 0;
        String [] linkArr = links.split(";");
        versBuffer.append(linkArr[index] + ":\n");
        for (Map.Entry<Integer, IndexRange> entry: selectedVersesMap.entrySet()) {

            Integer versNo = entry.getKey();
            vers.getVers().add(BigInteger.valueOf((long) versNo));
            if ((index < (bookLinks.size())) && bookLinks.get(index).getVerses().contains(versNo)) {
                String vText = area.getText(entry.getValue());
                versBuffer.append(vText);
            } else if ((index + 1) < (linkArr.length)) {
                index++;
                versBuffer.append(linkArr[index] + ":\n");
                String vText = area.getText(entry.getValue());
                versBuffer.append(vText);
            }
            versBuffer.append('\n');
        }
        vers.setVtext(versBuffer.toString());
        return vers;
    }

    /**
     * Gets bible infos.
     *
     * @return the bible infos
     */
    public List<String> getBibleInfos() {
        List<String> result = new ArrayList<>();
        for (BibleBookInstance inst : bibleInstances) {
            result.add(inst.getBibleRef().getBibleID()
                    + "||"
                    + inst.getBible().getBiblename());
        }
        return result;
    }

    /**
     * Gets bible instances.
     *
     * @return the bible instances
     */
    public List<BibleBookInstance> getBibleInstances() {
        return Collections.unmodifiableList(bibleInstances);
    }

    /**
     * Gets dict instances.
     *
     * @return the dict instances
     */
    public List<DictionaryInstance> getDictInstances() {
        return Collections.unmodifiableList(dictInstances);
    }

    /**
     * Gets bible book info.
     *
     * @param bible the bible
     * @return the bible book info
     */
    public List<String> getBibleBookInfo(XMLBIBLE bible) {
        List<String> csvBooksList = new ArrayList<>();
        List<JAXBElement<BIBLEBOOK>> books = bible.getBIBLEBOOK();
        for (JAXBElement<BIBLEBOOK> jBook: books) {
            BIBLEBOOK book = jBook.getValue();
            csvBooksList.add(book.getBnumber().toString() + "," + book.getBname() + "," + book.getBsname());

        }

        return csvBooksList;
    }

    /**
     * Gets book labels.
     *
     * @return the book labels
     */
    public List<String> getBookLabels() {
        return this.bookLabels;
    }

    /**
     * Gets book label as class.
     *
     * @param label the label
     * @return the book label as class
     */
    public BookLabel getBookLabelAsClass(String label) {
        return new BookLabel(label);
    }

    /**
     * Gets book by label.
     *
     * @param bible     the bible
     * @param bookLabel the book label
     * @return the book by label
     */
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


    /**
     * Gets books.
     *
     * @param bible the bible
     * @return the books
     */
    public List<BIBLEBOOK> getBooks(XMLBIBLE bible) {
        List<BIBLEBOOK> result = new ArrayList<>();
        for (JAXBElement<BIBLEBOOK> xmlBook: bible.getBIBLEBOOK()) {
            result.add(xmlBook.getValue());
        }
        return result;
    }

    /**
     * Gets chapters.
     *
     * @param book the book
     * @return the chapters
     */
    public List<CHAPTER> getChapters(BIBLEBOOK book) {
        List<CHAPTER> result = new ArrayList<>();
        for (JAXBElement<CHAPTER> xmlChapter: book.getCHAPTER()) {
            result.add(xmlChapter.getValue());
        }
        return result;
    }

    /**
     * Gets verses.
     *
     * @param chapter    the chapter
     * @param bookNumber the book number
     * @return the verses
     */
    public List<BibleFulltextEngine.BibleTextKey> getVerses(CHAPTER chapter, Integer bookNumber) {
        List<BibleFulltextEngine.BibleTextKey> result = new ArrayList<>();
        for (JAXBElement xmlVers: chapter.getPROLOGOrCAPTIONOrVERS()) {
            if (xmlVers.getValue() instanceof VERS) {
              VERS vers = (VERS)xmlVers.getValue();
              Integer versNo = vers.getVnumber().intValue();
              BibleFulltextEngine.BibleTextKey keyObj =
                      new BibleFulltextEngine.BibleTextKey(bookNumber, chapter.getCnumber().intValue(),
                              versNo, null);
              keyObj = getVersEntry(chapter,keyObj);
              result.add(keyObj);
            }
        }
        return result;
    }

    /**
     * Gets vers entry.
     *
     * @param chapter the chapter
     * @param key     the key
     * @return the vers entry
     */
    public BibleFulltextEngine.BibleTextKey getVersEntry(CHAPTER chapter,
                                                                      BibleFulltextEngine.BibleTextKey key) {
        StringBuffer buffer = new StringBuffer();
        BibleFulltextEngine.BibleTextKey result = null;
        for (JAXBElement xmlVers : chapter.getPROLOGOrCAPTIONOrVERS()) {
            if (xmlVers.getValue() instanceof VERS) {
                VERS vers = (VERS) xmlVers.getValue();
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

                            } else if (jaxbClazz.getName().equals(GRAM.class.getName())) {
                                String prefix;
                                if (key.getBook() < 40) {
                                    prefix = "H";
                                } else {
                                    prefix = "G";
                                }
                                GRAM gram = (GRAM) ((JAXBElement<?>) content).getValue();
                                for (Object styledContent : gram.getContent()) {
                                    if (styledContent instanceof String) {
                                        String text = (String) styledContent;
                                        buffer.append(text);
                                    }
                                    String[] strNos = gram.getStr().split(" ");
                                    for (String no : strNos) {
                                        String gramStrong = "[" + prefix + no + "]";
                                        buffer.append(gramStrong);
                                    }

                                }

                            }
                        }
                    }
                }
            }
        }
        result = new BibleFulltextEngine.BibleTextKey(key.getBook(),
                key.getChapter(),
                key.getVers(),
                buffer.toString());
        return result;
    }


    /**
     * Gets chapter.
     *
     * @param book      the book
     * @param chapterNo the chapter no
     * @return the chapter
     */
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

    /**
     * Generate vers link string.
     *
     * @param verses the verses
     * @param label  the label
     * @return the string
     */
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

    /**
     * Get all books from the given bible
     * @param selected the bible
     * @return the list of books and display Strings
     */
    public List<Tuple<BIBLEBOOK, String>> getBibleBooksCooked(XMLBIBLE selected) {
        List<BIBLEBOOK> books = getInstance().getBooks(selected);
        List<BookLabel> labels = getBookLabelsAsClasses();
        List<Tuple<BIBLEBOOK, String>> result = new ArrayList<>();
        for (BIBLEBOOK book: books) {
            Optional<BookLabel> theLabel = labels.stream()
                    .filter(e -> e.getBookNumber().equals(book.getBnumber().intValue()))
                    .findFirst();
            theLabel.ifPresent(e -> {
                String displayString = e.bookNumber + ". " + e.getLongName();
                result.add(new Tuple<>(book, displayString));});
        }
        return result;
    }

    /**
     * get the list of chapters from the specified book
     * @param book the book tuple
     * @return the list of chapters of the book
     */
    public List<Tuple<Integer, CHAPTER>> getChaptersForBookCooked(Tuple<BIBLEBOOK, String> book) {
        List<CHAPTER> chapterList  = getInstance().getChapters(book.getFirst());
        List<Tuple<Integer, CHAPTER>> result = new ArrayList<>();
        for (CHAPTER chapter:chapterList) {
            result.add(new Tuple<>(chapter.getCnumber().intValue(), chapter));
        }
        return  result;
    }

    /**
     * get the verses for a specific chapter
     * @param chapter the chapter tuple
     * @return the verses for the chapter
     */
    public List<Integer> getVersesForChapterCooked(Tuple<Integer, CHAPTER> chapter) {
        List<Integer> result = new ArrayList<>();
        for (JAXBElement<?> element:chapter.getSecond().getPROLOGOrCAPTIONOrVERS()) {
            if (element.getValue() instanceof VERS) {
                VERS verse = (VERS)element.getValue();
                result.add(verse.getVnumber().intValue());
            }
        }
        return result;
    }

    private List<BookLabel> getBookLabelsAsClasses() {
        List<BookLabel> labels = new ArrayList<>();
        List<String> temp = getInstance().getBookLabels();
        for (String label: temp) {
            labels.add(getInstance().getBookLabelAsClass(label));
        }
        return labels;
    }

    /**
     * The type Book link.
     */
    public static class BookLink implements Serializable {

        private final String bookLabel;

        private final Integer chapter;

        private final List<Integer> verses;

        private final Integer book;

        /**
         * Instantiates a new Book link.
         *
         * @param bookLabel the book label
         * @param chapter   the chapter
         */
        public BookLink(String bookLabel, Integer chapter) {
            this(bookLabel, chapter, Arrays.asList(1));
        }

        /**
         * Instantiates a new Book link.
         *
         * @param bookLabel the book label
         * @param chapter   the chapter
         * @param verses    the verses
         */
        public BookLink(String bookLabel, Integer chapter, List<Integer> verses) {
            this.bookLabel = bookLabel;
            this.book = this.getBookLabelClass().getBookNumber();
            this.chapter = chapter;
            this.verses = verses;
        }

        /**
         * Instantiates a new Book link.
         *
         * @param bookNo  the book no
         * @param chapter the chapter
         * @param verses  the verses
         */
        public BookLink(Integer bookNo, Integer chapter, List<Integer> verses) {
            this.bookLabel = BibleTextUtils.getInstance()
                    .getBookLabels().get(bookNo -1);
            this.book = bookNo;
            this.chapter = chapter;
            this.verses = verses;
        }

        /**
         * Gets book label.
         *
         * @return the book label
         */
        public String getBookLabel() {
            return bookLabel;
        }

        /**
         * Gets book label class.
         *
         * @return the book label class
         */
        public BookLabel getBookLabelClass() {
            return new BookLabel(bookLabel);
        }

        /**
         * Gets chapter.
         *
         * @return the chapter
         */
        public Integer getChapter() {
            return chapter;
        }

        /**
         * Gets verses.
         *
         * @return the verses
         */
        public List<Integer> getVerses() {
            return verses;
        }

        public Integer getBook() {
            return book;
        }

        @Override
        public String toString() {
            StringBuffer buffer = new StringBuffer();
            List<Integer> versList = this.getVerses();
            String vers = new Integer(1).toString();
            if (versList.size() != 0) {
                getVerseFormatted(buffer, versList);
            } else {
                buffer.append("[")
                        .append(this.getBookLabelClass().longName)
                        .append(" " + this.getChapter())
                        .append("," + vers)
                        .append("]");

            }
            return buffer.toString();
        }

        private void getVerseFormatted(StringBuffer buffer, List<Integer> versList) {
            String vers;
            Integer versNoStart = versList.get(0);
            Integer verseInt = versNoStart;
            Boolean sequential = false;
            for (Integer verseNum: versList) {
                if (verseNum == verseInt) {
                    sequential = true;
                } else {
                    sequential = false;
                    break;
                }
                verseInt++;
            }
            if (sequential) {
                vers = "" + versNoStart + "-" + versList.get(versList.size() - 1);
            } else {
                vers = "";
                for (Integer versNum: versList) {
                    vers = vers + versNum + ".";
                }
            }
            buffer.append("[")
                    .append(this.getBookLabelClass().longName)
                    .append(" " + this.getChapter())
                    .append("," + vers)
                    .append("]");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BookLink)) return false;
            BookLink bookLink = (BookLink) o;
            return getBookLabel().equals(bookLink.getBookLabel())
                    && getChapter().equals(bookLink.getChapter())
                    && getVerses().equals(bookLink.getVerses());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBookLabel(), getChapter(), getVerses());
        }
    }

    /**
     * The type Book label.
     */
    public static class BookLabel {

        private Integer bookNumber = 0;

        private String shortName = "";

        private String longName = "";

        private String label = "";

        /**
         * Instantiates a new Book label.
         *
         * @param label the label
         */
        public BookLabel(String label) {
            this.label = label;
            String [] temp = label.split(",");
            bookNumber = Integer.parseInt(temp[0]);
            longName = temp[1];
            shortName = temp[2];
        }

        /**
         * Gets book number.
         *
         * @return the book number
         */
        public Integer getBookNumber() {
            return bookNumber;
        }

        /**
         * Gets short name.
         *
         * @return the short name
         */
        public String getShortName() {
            return shortName;
        }

        /**
         * Gets long name.
         *
         * @return the long name
         */
        public String getLongName() {
            return longName;
        }

        @Override
        public String toString() {
            return label;
        }
    }


    /**
     * The type Bible book instance.
     */
    public static class BibleBookInstance {

        private final BibleRef bibleRef;

        private final XMLBIBLE bible;

        private Optional<Tuple<Dictionary, DictionaryRef>> optDictAccRefTuple = Optional.empty();

        /**
         * Instantiates a new Bible book instance.
         *
         * @param bibleRef the bible ref
         * @param bible    the bible
         */
        public BibleBookInstance(BibleRef bibleRef, XMLBIBLE bible) {
            this.bibleRef = bibleRef;
            this.bible = bible;
        }

        /**
         * Gets bible ref.
         *
         * @return the bible ref
         */
        public BibleRef getBibleRef() {
            return bibleRef;
        }

        /**
         * Gets bible.
         *
         * @return the bible
         */
        public XMLBIBLE getBible() {
            return bible;
        }

        /**
         * Gets opt dict acc ref tuple.
         *
         * @return the opt dict acc ref tuple
         */
        public Optional<Tuple<Dictionary, DictionaryRef>> getOptDictAccRefTuple() {
            return optDictAccRefTuple;
        }

        /**
         * Sets opt dict acc ref tuple.
         *
         * @param dictionary the dictionary
         * @param accRef     the acc ref
         * @return the opt dict acc ref tuple
         */
        public BibleBookInstance setOptDictAccRefTuple(Dictionary dictionary,
                                                       DictionaryRef accRef) {
            this.optDictAccRefTuple = Optional.of(new Tuple<>(dictionary, accRef));
            return this;
        }
    }


    /**
     * The type Dictionary instance.
     */
    public static class DictionaryInstance {

        private final DictionaryRef dictionaryRef;

        private Optional<Tuple<XMLBIBLE, BibleRef>> optBibleRefTuple = Optional.empty();

        private final Dictionary dictionary;

        /**
         * Instantiates a new Dictionary instance.
         *
         * @param dictionaryRef the dictionary ref
         * @param dictionary    the dictionary
         */
        public DictionaryInstance(DictionaryRef dictionaryRef, Dictionary dictionary) {
            this.dictionaryRef = dictionaryRef;
            this.dictionary = dictionary;
        }

        /**
         * Gets dictionary ref.
         *
         * @return the dictionary ref
         */
        public DictionaryRef getDictionaryRef() {
            return dictionaryRef;
        }

        /**
         * Gets dictionary.
         *
         * @return the dictionary
         */
        public Dictionary getDictionary() {
            return dictionary;
        }

        /**
         * Gets opt bible ref tuple.
         *
         * @return the opt bible ref tuple
         */
        public Optional<Tuple<XMLBIBLE, BibleRef>> getOptBibleRefTuple() {
            return optBibleRefTuple;
        }

        /**
         * Sets bible ref tuple.
         *
         * @param bibleRefTuple the bible ref tuple
         * @return the bible ref tuple
         */
        public DictionaryInstance setBibleRefTuple(Tuple<XMLBIBLE, BibleRef> bibleRefTuple) {
            this.optBibleRefTuple = Optional.of(bibleRefTuple);
            return this;
        }

        @Override
        public String toString() {
            return dictionaryRef.getFilename() + " : " + dictionaryRef.getDictionaryID();
        }

    }
}
