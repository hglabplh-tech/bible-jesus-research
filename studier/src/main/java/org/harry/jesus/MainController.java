package org.harry.jesus;

import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.XMLBIBLE;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import jesus.harry.org.plan._1.Day;
import jesus.harry.org.plan._1.Days;
import jesus.harry.org.versnotes._1.Note;
import jesus.harry.org.versnotes._1.Vers;
import jesus.harry.org.versnotes._1.Versnotes;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.model.*;
import org.harry.jesus.danielpersistence.PersistenceLayer;
import org.harry.jesus.fxutils.*;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.HTMLRendering;
import org.harry.jesus.jesajautils.TextRendering;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.jesajautils.browse.ParStyle;
import org.harry.jesus.jesajautils.browse.TextStyle;
import org.harry.jesus.jesajautils.editor.HTMLToPDF;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.harry.jesus.jesajautils.fulltext.StatisticsCollector;

import org.tinylog.Logger;
//import org.reactfx.util.Either;


import javax.xml.bind.JAXBElement;
import java.io.*;
import java.util.*;

public class MainController {

    @FXML
    TextField chapterTitle;

    @FXML
    ListView<String> bibles;

    @FXML
    ListView<String> footerNotes;

    @FXML
    TreeView<String> booksTree;

    @FXML
    TextField query;

    @FXML
    ListView<String> resultlist;


    @FXML
    VirtualizedScrollPane<GenericStyledArea<ParStyle, String, TextStyle>> chapterReader;

    @FXML private TableView<NoteTabEntry> notesTable;

    @FXML private HTMLEditor devotionalEdit;

    @FXML private ListView<String> planList;

    @FXML private WebView devView;

    @FXML private WebView versesView;

    BorderPane borderPane = null;

    BibleTextUtils utils;

    List<BIBLEBOOK> theBooks = new ArrayList<>();

    FoldableStyledArea area;

    XMLBIBLE selected = null;

    private String actBookLabel;

    BibleTextUtils.BookLabel actBook = null;

    private Integer actChapter = 1;

    TextRendering rendering = null;

    Map<Integer, IndexRange> selectedVersesMap = new LinkedHashMap<>();
    List<BibleFulltextEngine.BibleTextKey> verseKeys = new ArrayList<>();

    Versnotes noteList = new Versnotes();

    Days planDays = new Days();



    int dayNo = 1;



    @FXML
    public void initialize() {
        initChapterReader();
        initAreaContextMenu();
        initListeners();





        TextOps<String, TextStyle> styledTextOps = SegmentOps.styledTextOps();

        this.borderPane.setCenter(chapterReader);
        utils = new BibleTextUtils();
        List<String> bibleNames = utils.getBibleInfos();

        for (String name: bibleNames) {
            bibles.getItems().add(name);
        }
        bibles.getSelectionModel().selectFirst();

        actBookLabel = utils.getBookLabels().get(0);
        actBook = utils.getBookLabelAsClass(actBookLabel);
        TreeItem<String> root = buildBooksTree();
        showRoot();
        System.out.println("second");
    }

    private void initListeners() {
        booksTree.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<TreeItem<String>>() {

                    @Override
                    public void changed(ObservableValue<? extends TreeItem<String>> observableValue, TreeItem<String> old_val, TreeItem<String> new_val) {
                        String value = new_val.getValue();
                        String regex = "[0-9]+";

                        String bookLabel = "";
                        int chapter = 1;
                        if (value.matches(regex)) {
                            bookLabel = new_val.getParent().getValue();
                            chapter = Integer.valueOf(value, 10);
                        } else {
                            bookLabel = value;
                        }
                        Optional<BIBLEBOOK> book = utils.getBookByLabel(selected, bookLabel);
                        if (book.isPresent()) {
                            actBookLabel = bookLabel;
                            actBook = utils.getBookLabelAsClass(bookLabel);
                            actChapter = chapter;
                            showChapter();
                        }
                    }
                });
        bibles.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                selected = utils.getBibles().get(t1.intValue());
                TreeItem<String> root = buildBooksTree();
                showChapter();
            }
        });
        footerNotes.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                String note = footerNotes.getItems().get(t1.intValue());
                List<BibleTextUtils.BookLink> links = utils.parseLinks(note);
                if (links.size() > 0) {
                    showLink(links);
                }
            }

        });

        planList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Day theDay = planDays.getDay().get(newValue.intValue());
                String versHtml = HTMLRendering.renderVersesASDoc(selected, utils, theDay.getVerses());
                String devHtml = devotionalEdit.getHtmlText();
                planDays.getDay().get(newValue.intValue()).setDevotional(devHtml);
                WebEngine engine = devView.getEngine();
                engine.loadContent(devHtml);
                engine = versesView.getEngine();
                engine.loadContent(versHtml);


            }

        });

        area.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 1) {
                    IndexRange range = area.getSelection();
                    Map.Entry<Integer, IndexRange> versPointer =
                            rendering.selectVerseByGivenRange(range);
                    selectedVersesMap.put(versPointer.getKey(), versPointer.getValue());
                }
            }
        });
    }

    private void initAreaContextMenu() {
        ContextMenu contMenu = new ContextMenu();
        MenuItem mItem = new MenuItem();
        mItem.setText("Highlight selected Verses");
        contMenu.getItems().add(mItem);
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Optional<Color> resultColor = ColorDialog.callColorDialog();
                for (IndexRange range: getSelectedMapSorted().values()) {
                    if (resultColor.isPresent()) {
                    area.setStyle(range.getStart(),
                            range.getEnd(),
                            TextStyle.backgroundColor(resultColor.get()));
                    }
                }
            }
        });
        mItem = new MenuItem();
        mItem.setText("createNote");
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Note theNote = new Note();

                for (Integer versNo: getSelectedMapSorted().keySet()) {
                    Vers vers = BibleTextUtils.generateVers(actBook, actChapter, area, getSelectedMapSorted(), versNo);
                    theNote.getVerslink().add(vers);
                }
                Optional<Note> newNote = CreateNoteDialog.showNoteCreateDialog(theNote);
                if (newNote.isPresent()) {
                    noteList.getVersenote().add(newNote.get());
                    notesTable.getSelectionModel().setCellSelectionEnabled(true);
                    notesTable.setEditable(false);

                    notesTable.getSelectionModel().getSelectedItem();
                    NoteTabEntry entry = new NoteTabEntry(utils.generateVersLink(newNote.get().getVerslink(), actBook),
                            newNote.get().getVerslink().get(0).getVtext()
                            , newNote.get().getNote());
                    notesTable.getItems().add(entry);

                    notesTable.setVisible(false);
                    notesTable.refresh();
                    notesTable.setVisible(true);

                }


            }
        });
        contMenu.getItems().add(mItem);
        area.contextMenuObjectProperty().setValue(contMenu);
        mItem = new MenuItem();
        mItem.setText("Copy");
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Note theNote = new Note();

                Vers vers = BibleTextUtils.generateVerses(utils, actBook, actChapter, area, getSelectedMapSorted());
                List<Vers> verses = new ArrayList<>();
                verses.add(vers);
                String versHtml = HTMLRendering.renderVerses(selected, utils, verses);
                copyHtmlToClip(new StringBuffer(versHtml));
            }
        });
        contMenu.getItems().add(mItem);
        area.contextMenuObjectProperty().setValue(contMenu);
        mItem = new MenuItem();
        mItem.setText("copy Link to last PlanDay");
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                List<Day> dayList = planDays.getDay();
                if (dayList.size() == 0) {
                    Day newDay = nextPlanDay();
                    planList.getItems().add(newDay.getTitle());
                    planDays.getDay().add(newDay);
                    dayList = planDays.getDay();
                }
                Day theDay = dayList.get(dayList.size() - 1);
                Vers vers = BibleTextUtils.generateVerses(utils, actBook, actChapter, area, getSelectedMapSorted());
                theDay.getVerses().add(vers);
            }

        });
        contMenu.getItems().add(mItem);
        area.contextMenuObjectProperty().setValue(contMenu);
    }

    private Day nextPlanDay() {
        Day newDay = new Day();
        newDay.setTitle("Day" + dayNo);
        dayNo++;
        newDay.setDevotional(devotionalEdit.getHtmlText());
        return newDay;
    }

    private void initChapterReader() {
        Parent parent = booksTree.getParent();
        this.borderPane = (BorderPane) parent;
        area = new FoldableStyledArea();
        chapterReader = new VirtualizedScrollPane(area);
        chapterReader.setMinSize(600, 300);
        chapterReader.setMaxSize(600, 300);
    }

    private void showRoot() {
        rendering = new TextRendering(utils, this.area, actBookLabel, actChapter);
        fillTextArea();
    }

    private void fillTextArea() {
        rendering.render(selected, utils.getBookLabels().get(0), actChapter);
        actBookLabel = utils.getBookLabels().get(0);
        actBook = utils.getBookLabelAsClass(actBookLabel);
        footerNotes.getItems().clear();
        footerNotes.getItems().addAll(rendering.getNotes());
        String [] splitted = actBookLabel.split(",");
        chapterTitle.setText("Book: " + splitted[1] + " Chapter: " + actChapter);
    }

    private boolean showChapter() {
        rendering = new TextRendering(utils, this.area, actBookLabel, actChapter);
        rendering = new TextRendering(utils, area, actBookLabel, actChapter);
        boolean found = fillChapterText();
        return found;
    }

    private boolean fillChapterText() {
        boolean found = rendering.render(selected, actBookLabel, actChapter);
        footerNotes.getItems().clear();
        footerNotes.getItems().addAll(rendering.getNotes());
        String [] splitted = actBookLabel.split(",");
        selectedVersesMap.clear();
        rendering.clearRendering();
        chapterTitle.setText("Book: " + splitted[1] + " Chapter: " + actChapter);
        return found;
    }

    private void showLink(List<BibleTextUtils.BookLink> links) {
        String chapter = rendering.render(selected, links);
        footerNotes.getItems().clear();
        footerNotes.getItems().addAll(rendering.getNotes());
    }


    private TreeItem<String> buildBooksTree() {
        TreeItem<String> root = new TreeItem<>();
        root.setValue("The books");
        booksTree.setRoot(root);
        theBooks.clear();
        List<JAXBElement<BIBLEBOOK>> books = selected.getBIBLEBOOK();
        for (JAXBElement<BIBLEBOOK> book: books) {
            theBooks.add(book.getValue());
        }
        int index = 0;
        for(BIBLEBOOK theBook: theBooks) {
            String label = utils.getBookLabels().get(index);
            TreeItem item = new TreeItem(label);
            root.getChildren().add(item);
            for (JAXBElement<CHAPTER> chapter : theBook.getCHAPTER()) {
                TreeItem<String> cItem = new TreeItem<>(chapter.getValue().getCnumber().toString());
                item.getChildren().add(cItem);
            }
            index++;
        }
        booksTree.refresh();
        return root;
    }


    @FXML
    public void search(ActionEvent event) {
        String pattern = query.getText();
        BibleFulltextEngine engine = new BibleFulltextEngine(this.selected);
        StatisticsCollector collector = new StatisticsCollector();
        Map<BibleFulltextEngine.BibleTextKey, String> hits =
                engine.searchPlain(pattern, collector);
        verseKeys.clear();
        resultlist.getItems().clear();
        for (Map.Entry<BibleFulltextEngine.BibleTextKey, String> entry: hits.entrySet()) {
            verseKeys.add(entry.getKey());
            resultlist.getItems().add(utils.generateVersEntry(entry.getKey(), entry.getValue()));
        }

    }

    @FXML
    public void openPlan(ActionEvent event) {

    }

    @FXML
    public void savePlan(ActionEvent event) {

    }

    @FXML
    public void newPlan(ActionEvent event) {
        dayNo = 1;
        Day newDay = nextPlanDay();
        planList.getItems().add(newDay.getTitle());
        planDays.getDay().add(newDay);
    }

    @FXML
    public void addDay(ActionEvent event) {
        Day newDay = nextPlanDay();
        planList.getItems().add(newDay.getTitle());
        planDays.getDay().add(newDay);
    }


    @FXML
    public void prevChapter(ActionEvent event) {
        actChapter = actChapter - 1;
        boolean found = showChapter();
        if (!found) {

        }
    }

    @FXML
    public void loadDev(ActionEvent event) {
        byte [] buffer = new byte[4096];
        InputStream stream = JesusMisc.showOpenDialog(event);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int read = stream.read(buffer);
            while (read != -1) {
                out.write(buffer, 0, read);
                read = stream.read(buffer);
            }
            String html = new String(out.toByteArray());
            devotionalEdit.setHtmlText(html);
        } catch (IOException ex) {

        }
    }

    @FXML
    public void saveDev(ActionEvent event) {
        OutputStream os = JesusMisc.showSaveDialog(event);
        String htmlText = devotionalEdit.getHtmlText();
        try {
            PrintWriter writer = new PrintWriter(os);
            writer.print(htmlText);
            writer.close();
            os.close();
        } catch(IOException ex) {
            Logger.trace("close html file failed");
        }
    }

    @FXML
    public void newDev(ActionEvent event) {

    }

    @FXML
    public void copyVers(ActionEvent event) {
        StringBuffer htmlBuffer = new StringBuffer();
        int index = resultlist.getSelectionModel().getSelectedIndex();
        BibleFulltextEngine.BibleTextKey link = verseKeys.get(index);
        BIBLEBOOK book = theBooks.get(link.getBook() - 1);
        JAXBElement<CHAPTER> jaxbChapter = book.getCHAPTER().get(link.getChapter() - 1);
        CHAPTER chapter = jaxbChapter.getValue();
        String listText = resultlist.getItems().get(index);
        int endIndex = listText.indexOf("]");
        StringBuffer buffer = HTMLRendering.buildVersHTML(link, listText.substring(0, endIndex + 1), chapter);
        HTMLRendering.renderVers(htmlBuffer, buffer.toString());

        copyHtmlToClip(htmlBuffer);


    }


    private void copyHtmlToClip(StringBuffer htmlBuffer) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();

        content.putHtml(htmlBuffer.toString());
        clipboard.setContent(content);
    }

    @FXML
    public void copyNote(ActionEvent event) {
        Integer row = notesTable.getSelectionModel().getSelectedIndex();
        Note note = noteList.getVersenote().get(row);
        String noteText = note.getNote();
        List<Vers> verses = note.getVerslink();
        StringBuffer buffer = new StringBuffer();
        for (Vers vers: verses) {
            buffer.append("<p/>").append(BibleTextUtils.generateVersEntry(utils, vers, vers.getVtext()));
        }
        StringBuffer htmlBuffer = new StringBuffer();
        HTMLRendering.noteToHTML(noteText, buffer, htmlBuffer);


        copyHtmlToClip(htmlBuffer);

    }


    @FXML
    public void nextChapter(ActionEvent event) {
        actChapter = actChapter + 1;
        boolean found = showChapter();
        if (!found) {

        }
    }

    @FXML
    public void linkBack(ActionEvent event) {
        showChapter();
    }

    @FXML
    public void toPDF(ActionEvent event) {
        String htmlText = devotionalEdit.getHtmlText();
        OutputStream pdfOut = JesusMisc.showSaveDialog(event);
        HTMLToPDF.convertTo(htmlText, pdfOut);
    }

    @FXML
    public void printDev(ActionEvent event) {
        PrinterJob job = PrinterJob.createPrinterJob(Printer.getDefaultPrinter());
        devotionalEdit.print(job);
        job.endJob();
    }

    @FXML
    public void loadNotes(ActionEvent event) {
        InputStream input = JesusMisc.showOpenDialog(event);
        Versnotes notes = PersistenceLayer.loadNotes(input);
        noteList = notes;

        for (Note newNote :noteList.getVersenote()) {
            notesTable.getSelectionModel().setCellSelectionEnabled(true);
            notesTable.setEditable(false);

            notesTable.getSelectionModel().getSelectedItem();
            NoteTabEntry entry = new NoteTabEntry(utils.generateVersLink(newNote.getVerslink(), actBook),
                    newNote.getVerslink().get(0).getVtext()
                    , newNote.getNote());
            notesTable.getItems().add(entry);
        }

        notesTable.setVisible(false);
        notesTable.refresh();
        notesTable.setVisible(true);

    }

    @FXML
    public void saveNotes(ActionEvent event) {
        OutputStream os = JesusMisc.showSaveDialog(event);
        PersistenceLayer.storeNotes(noteList, os);
    }


    private Map<Integer, IndexRange> getSelectedMapSorted() {
        List<Integer> sortedList = new ArrayList<>();
        sortedList.addAll(selectedVersesMap.keySet());
        Collections.sort(sortedList, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        Map<Integer, IndexRange> temp = new LinkedHashMap<>();
        for (Integer no: sortedList) {
            IndexRange range = selectedVersesMap.get(no);
            temp.put(no, range);
        }
        return temp;
    }

}
