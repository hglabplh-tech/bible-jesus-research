package org.harry.jesus;

import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.XMLBIBLE;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import jesus.harry.org.highlights._1.Highlights;
import jesus.harry.org.plan._1.Day;
import jesus.harry.org.plan._1.Plan;
import jesus.harry.org.versnotes._1.Note;
import jesus.harry.org.versnotes._1.Vers;
import jesus.harry.org.versnotes._1.Versnotes;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.model.*;
import org.harry.jesus.danielpersistence.PersistenceLayer;
import org.harry.jesus.fxutils.*;
import org.harry.jesus.fxutils.media.MediaControl;
import org.harry.jesus.fxutils.media.PlayBible;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.HTMLRendering;
import org.harry.jesus.jesajautils.LinkHandler;
import org.harry.jesus.jesajautils.TextRendering;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.jesajautils.browse.ParStyle;
import org.harry.jesus.jesajautils.browse.TextStyle;
import org.harry.jesus.jesajautils.editor.HTMLToPDF;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.harry.jesus.jesajautils.fulltext.StatisticsCollector;

import org.harry.jesus.synchjeremia.ApplicationProperties;
import org.harry.jesus.synchjeremia.BibleThreadPool;
import org.harry.jesus.synchjeremia.SynchThread;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

//import org.reactfx.util.Either;


import javax.xml.bind.JAXBElement;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class MainController {

    @FXML
    TabPane mainTabPane;

    @FXML Tab readBible;
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

    @FXML private TableView<HighlightsEntry> highlightsTab;

    @FXML private HTMLEditor devotionalEdit;

    @FXML private ListView<String> planList;

    @FXML private WebView devView;

    @FXML Button printDev;


    @FXML private WebView versesView;

    @FXML ChoiceBox<SearchOptions> searchOptions;

    @FXML
    GridPane topGridPane;

    @FXML MediaView chapterPlayView;

    BorderPane borderPane = null;

    BibleTextUtils utils;

    List<BIBLEBOOK> theBooks = new ArrayList<>();

    FoldableStyledArea area;

    XMLBIBLE selected = null;

    private String actBookLabel;

    BibleTextUtils.BookLabel actBook = null;

    private Integer actChapter = 1;

    TextRendering rendering = null;

    BibleThreadPool.ThreadBean context = null;

    Map<Integer, IndexRange> selectedVersesMap = new LinkedHashMap<>();

    List<BibleFulltextEngine.BibleTextKey> verseKeys;

    Versnotes noteList;

    Highlights highlights;

    Plan planDays = new Plan();



    int planDayNumber = 0;

    int editPlanDayIndex = 0;

    int selectedIndex = 0;

    private PlayBible playBible;

    private MediaControl mediaControl;



    @FXML
    public void initialize() {
        initChapterReader();
        initAreaContextMenu();
        printDev.setDisable(true);
        initListeners();

        context = BibleThreadPool.getContext();
        bibles.getSelectionModel().selectFirst();


        SynchThread.loadRendering(context);
        SynchThread.loadNotes(context);
        SynchThread.loadHighlights(context);
        ApplicationProperties.loadApplicationProperties();
        String mediaPath = context.getSettings()
                .getProperty(ApplicationProperties.AUDIO_PATH, System.getProperty("user.home")
                        + File.separator
                        + "bibleStudyAudio"
                );
        utils = new BibleTextUtils();
        selected = null;
        if (utils.getBibles().size() > 0) {
            selected = utils.getBibles().get(0);
        }
        searchOptions.getItems().addAll(SearchOptions.SIMPLE,
                SearchOptions.EXACT,
                SearchOptions.FUZZY);
        searchOptions.getSelectionModel().select(0);
        actBookLabel = utils.getBookLabels().get(0);
        actBook = utils.getBookLabelAsClass(actBookLabel);
        context.addSetting(ApplicationProperties.AUDIO_PATH, mediaPath);
        noteList = context.getNoteList();
        verseKeys = context.getVerseKeys();
        highlights = context.getHighlights();


        TextOps<String, TextStyle> styledTextOps = SegmentOps.styledTextOps();

        this.borderPane.setCenter(chapterReader);
        List<String> bibleNames = utils.getBibleInfos();

        for (String name: bibleNames) {
            bibles.getItems().add(name);
        }

        TreeItem<String> root = buildBooksTree();
        if (selected != null) {
            loadNotesAndRender();
            loadHighlightsAndRender();
            showRoot();
            initMediaView();
            List<String> fonts = Font.getFamilies();
            Optional<String> optFont = fonts.stream().filter(e -> e.contains("Tempus")).findFirst();
            if (optFont.isPresent()) {
                String text = area.getText();
                TextStyle style = TextStyle.fontFamily(optFont.get()).updateFontSize(12);
                area.setStyle(0, text.length() -1, style);
            }
            System.out.println("second");
        }
    }

    private void initMediaView() {
        String mediaPath = context.getSettings().getProperty(ApplicationProperties.AUDIO_PATH);
        this.playBible = new PlayBible(mediaPath
                , chapterPlayView);
        BibleTextUtils.BookLink link =
                new BibleTextUtils.BookLink(actBookLabel, actChapter, Arrays.asList(1));
        MediaPlayer mp = playBible.playChapter(link);
        if (mp != null) {
            mediaControl = new MediaControl(mp, chapterPlayView);
            topGridPane.add(mediaControl, 0, 1,1,2);
        }
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
                        if (utils.getBookLabels().contains(bookLabel)) {
                        Optional<BIBLEBOOK> book = utils.getBookByLabel(selected, bookLabel);
                        if (book.isPresent()) {
                            actBookLabel = bookLabel;
                            actBook = utils.getBookLabelAsClass(bookLabel);
                            actChapter = chapter;
                            showChapter();
                            BibleTextUtils.BookLink link =
                                    new BibleTextUtils.BookLink(actBookLabel, actChapter, Arrays.asList(1));
                            if (playBible != null) {
                                playBible.stopChapter();
                            }
                            initMediaView();
                        }
                        }
                    }
                });
        bibles.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                selectedIndex = t1.intValue();
                selected = utils.getBibles().get(t1.intValue());
                TreeItem<String> root = buildBooksTree();
                showChapter();
                BibleTextUtils.BookLink link =
                        new BibleTextUtils.BookLink(actBookLabel, actChapter, Arrays.asList(1));
                if (playBible != null) {
                    playBible.stopChapter();
                }
                initMediaView();
            }
        });
        footerNotes.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                String note = footerNotes.getItems().get(t1.intValue());
                List<BibleTextUtils.BookLink> links = LinkHandler.parseLinks(utils, note);
                if (links.size() > 0) {
                    showLink(links);
                }
            }

        });

        planList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                editPlanDayIndex = newValue.intValue();
                Day theDay = planDays.getDay().get(editPlanDayIndex);
                String versHtml = HTMLRendering.renderVersesASDoc(selected, utils, theDay.getVerses());
                setPlanOutputSelected(theDay, versHtml);


            }

        });

        area.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    IndexRange range = area.getSelection();
                    Map.Entry<Integer, IndexRange> versPointer =
                            rendering.selectVerseByGivenRange(range);
                    if (selectedVersesMap.get(versPointer.getKey()) != null) {
                        selectedVersesMap.remove(versPointer.getKey());
                        area.setStyle(versPointer.getValue().getStart(),
                                versPointer.getValue().getEnd(),
                                TextStyle.underline(false));

                    } else {
                        selectedVersesMap.put(versPointer.getKey(), versPointer.getValue());
                    }
                }
            }
        });
    }

    private void setPlanOutputSelected(Day theDay, String versHtml) {
        WebEngine engine = devView.getEngine();
        byte [] devBytes = theDay.getDevotional();
        if (devBytes != null) {
            String devotional = new String(Base64.getDecoder().decode(devBytes));
            engine.loadContent(devotional);
            devotionalEdit.setHtmlText(devotional);
        }
        engine = versesView.getEngine();
        engine.loadContent(versHtml);
    }

    private void initAreaContextMenu() {
        ContextMenu contMenu = new ContextMenu();
        MenuItem mItem = new MenuItem();
        mItem.setText("Highlight selected Verses");
        contMenu.getItems().add(mItem);
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                highlightsTab.getSelectionModel().setCellSelectionEnabled(true);
                highlightsTab.setEditable(false);
                Optional<Color> resultColor = ColorDialog.callColorDialog();
                List<Vers> versList = new ArrayList<>();
                Vers vers = BibleTextUtils.generateVerses(utils, actBook, actChapter, area, getSelectedMapSorted());
                if (resultColor.isPresent()) {
                    for (Integer key: getSelectedMapSorted().keySet()) {
                        IndexRange range = getSelectedMapSorted().get(key);
                        area.setStyle(range.getStart(),
                                range.getEnd(),
                                TextStyle.backgroundColor(resultColor.get()));
                        if(!vers.getVers().contains(BigInteger.valueOf(key))) {
                            vers.getVers().add(BigInteger.valueOf(key));
                        }
                        vers.setBackcolor(resultColor.get().toString());
                    }
                    versList.add(vers);
                    TextRendering.storeVersRendering(versList, resultColor.get());
                    highlights.getHighlight().addAll(versList);
                    HighlightsEntry entry = getHighlightsEntry(vers);
                    highlightsTab.getItems().add(entry);
                }
            }
        });
        mItem = new MenuItem();
        mItem.setText("createNote");
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Note theNote = new Note();
                Vers vers = BibleTextUtils.generateVerses(utils, actBook, actChapter, area, getSelectedMapSorted());
                theNote.getVerslink().add(vers);
                Optional<Color> resultColor = ColorDialog.callColorDialog();

                Optional<Note> newNote = CreateNoteDialog.showNoteCreateDialog(theNote);
                if (newNote.isPresent()) {
                    Color noteColor = null;
                    if (resultColor.isPresent()) {
                        noteColor = resultColor.get();
                        vers.setBackcolor(noteColor.toString());
                    }
                    noteList.getVersenote().add(newNote.get());

                    notesTable.getSelectionModel().setCellSelectionEnabled(true);
                    notesTable.setEditable(false);

                    notesTable.getSelectionModel().getSelectedItem();
                    NoteTabEntry entry = new NoteTabEntry(utils.generateVersLink(newNote.get().getVerslink(), actBook),
                            newNote.get().getVerslink().get(0).getVtext()
                            , newNote.get().getNote());
                    notesTable.getItems().add(entry);
                    List<Vers> versList = newNote.get().getVerslink();
                    for (BigInteger versNo: vers.getVers()) {
                        IndexRange range = getSelectedMapSorted().get(versNo.intValue());
                        area.setStyle(range.getStart(),
                                range.getEnd(),
                                TextStyle.backgroundColor(resultColor.get()));
                    }
                    TextRendering.storeVersRendering(versList, noteColor);
                    noteList.getVersenote().add(newNote.get());
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
                List<Day> dayList = ensureFirstDay();
                Day theDay = dayList.get(dayList.size() - 1);
                Vers vers = BibleTextUtils.generateVerses(utils, actBook, actChapter, area, getSelectedMapSorted());
                theDay.getVerses().add(vers);
                String versHtml = HTMLRendering.renderVersesASDoc(selected, utils, theDay.getVerses());
                setPlanOutputSelected(theDay, versHtml);
            }

        });
        contMenu.getItems().add(mItem);
        area.contextMenuObjectProperty().setValue(contMenu);
    }

    private List<Day> ensureFirstDay() {
        List<Day> dayList = planDays.getDay();
        if (dayList.size() == 0) {
            Day newDay = nextPlanDay();
            planList.getItems().add(newDay.getTitle());
            planDays.getDay().add(newDay);
            dayList = planDays.getDay();
        }
        return dayList;
    }

    private void storeDevotional(Day theDay) {
        byte[] base64 = Base64.getEncoder().encode(devotionalEdit.getHtmlText().getBytes());
        theDay.setDevotional(base64);
    }

    private Day nextPlanDay() {
        Day newDay = new Day();
        planDayNumber++;
        newDay.setTitle("Day " + planDayNumber);
        return newDay;
    }

    private void initChapterReader() {
        Parent parent = booksTree.getParent();
        this.borderPane = (BorderPane) parent;
        area = new FoldableStyledArea();
        chapterReader = new VirtualizedScrollPane(area);
        chapterReader.setMinSize(750, 500);
        chapterReader.setMaxSize(750, 500);
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
        boolean found = fillChapterText();
        return found;
    }

    private boolean fillChapterText() {
        boolean found = rendering.render(selected, actBookLabel, actChapter);
        footerNotes.getItems().clear();
        footerNotes.getItems().addAll(rendering.getNotes());
        footerNotes.getItems().add("Fuzzy Link matches -->");
        for (String note : rendering.getNotes()) {
            String links = "";
            try {
                links = LinkHandler.generateLinksFuzzy(utils, note);
            } catch (Exception ex) {
                Logger.trace("Something went wrong with fuzzy!!! This feature has to be enhanced");
            }
            if (!links.isEmpty()) {
                footerNotes.getItems().add(links);
            }
        }

        String [] splitted = actBookLabel.split(",");
        selectedVersesMap.clear();
        rendering.clearRendering();
        chapterTitle.setText("Book: " + splitted[1] + " Chapter: " + actChapter);
        return found;
    }

    private void showLink(List<BibleTextUtils.BookLink> links) {
        String htmlText = HTMLRendering.renderLink(utils, selected, links);
        ReadLinksDialog.showReadLinkDialog(utils, area, htmlText);
    }


    private TreeItem<String> buildBooksTree() {
        if (selected != null) {
            TreeItem<String> root = new TreeItem<>();
            root.setValue("The books");
            booksTree.setRoot(root);
            theBooks.clear();
            List<JAXBElement<BIBLEBOOK>> books = selected.getBIBLEBOOK();
            for (JAXBElement<BIBLEBOOK> book : books) {
                theBooks.add(book.getValue());
            }
            int index = 0;
            for (BIBLEBOOK theBook : theBooks) {
                String label = utils.getBookLabels().get(index);
                TreeItem item = new TreeItem(label);
                root.getChildren().add(item);
                for (JAXBElement<CHAPTER> chapter : theBook.getCHAPTER()) {
                    TreeItem<String> cItem = new TreeItem<>(chapter.getValue().getCnumber().toString());
                    item.getChildren().add(cItem);
                }
                index++;
            }
            return root;
        }
        booksTree.refresh();
        return null;
    }


    @FXML
    public void settings(ActionEvent event) {
        new SettingsDialog().showAppSettingsDialog();
    }

    @FXML
    public void bibleInfo(ActionEvent event) {
        int index = bibles.getSelectionModel().getSelectedIndex();
        BibleInfoDialog.callBibleInfoDialog(utils.getBibles().get(index)
                ,utils.getBibleHashes().get(index));
    }

    @FXML
    public void search(ActionEvent event) {
        String pattern = query.getText();
        BibleFulltextEngine engine = new BibleFulltextEngine(this.selected);
        StatisticsCollector collector = new StatisticsCollector();
        Map<BibleFulltextEngine.BibleTextKey, String> hits;
        if (searchOptions.getValue().equals(SearchOptions.SIMPLE)) {
            hits = engine.searchPlain(pattern, collector);
        } else if (searchOptions.getValue().equals(SearchOptions.EXACT)) {
            hits = engine.searchPattern(pattern, BibleFulltextEngine.INSENSITIVE, collector);
        } else  {
            hits = engine.searchPatternFuzzy(pattern, BibleFulltextEngine.INSENSITIVE, collector);
        }
        verseKeys.clear();
        resultlist.getItems().clear();
        for (Map.Entry<BibleFulltextEngine.BibleTextKey, String> entry: hits.entrySet()) {
            verseKeys.add(entry.getKey());
            resultlist.getItems().add(utils.generateVersEntry(entry.getKey(), entry.getValue()));
        }

    }

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void readFullChapter(ActionEvent event) {
        int index = resultlist.getSelectionModel().getSelectedIndex();
        BibleFulltextEngine.BibleTextKey textKey = verseKeys.get(index);
        actChapter = textKey.getChapter();
        actBookLabel = utils.getBookLabels().get(textKey.getBook() - 1);
        actBook = new BibleTextUtils.BookLabel(actBookLabel);
        showChapter();
        mainTabPane.getSelectionModel().select(readBible);
    }

    @FXML
    public void readFullChapterNote(ActionEvent event) {
        int index = notesTable.getSelectionModel().getSelectedIndex();
        Note note = noteList.getVersenote().get(index);
        Vers vers = note.getVerslink().get(0);
        fullChapterFromVers(vers);

    }

    @FXML
    public void readFullChapterHighlight(ActionEvent event) {
        int index = highlightsTab.getSelectionModel().getSelectedIndex();
        Vers vers = highlights.getHighlight().get(index);
        fullChapterFromVers(vers);

    }

    private void fullChapterFromVers(Vers vers) {
        actChapter = vers.getChapter().intValue();
        actBookLabel = utils.getBookLabels().get(vers.getBook().intValue() - 1);
        actBook = new BibleTextUtils.BookLabel(actBookLabel);
        showChapter();
        mainTabPane.getSelectionModel().select(readBible);
    }

    @FXML
    public void openPlan(ActionEvent event) {
        InputStream input = JesusMisc.showOpenDialog(event, notesTable);
        planDays = PersistenceLayer.loadPlan(input);
        String versHtml = HTMLRendering.renderVersesASDoc(selected, utils, planDays.getDay().get(0).getVerses());
        setPlanOutputSelected(planDays.getDay().get(0), versHtml);
        for(Day day:planDays.getDay()) {
            planList.getItems().add(day.getTitle());
        }
        planDayNumber = planDays.getDay().size();
        editPlanDayIndex = planDayNumber - 1;
    }

    @FXML
    public void savePlan(ActionEvent event) {
        OutputStream os = JesusMisc.showSaveDialog(event, notesTable);
        PersistenceLayer.storePlan(planDays, os);
    }

    @FXML
    public void newPlan(ActionEvent event) {
        planDayNumber = 1;
        planList.getItems().clear();
        planDays = new Plan();
        versesView.getEngine().loadContent("");
        devView.getEngine().loadContent("");
    }

    @FXML
    public void addDay(ActionEvent event) {
        Day newDay = nextPlanDay();
        planList.getItems().add(newDay.getTitle());
        planDays.getDay().add(newDay);
        String versHtml = HTMLRendering.renderVersesASDoc(selected, utils, newDay.getVerses());
        setPlanOutputSelected(newDay, versHtml);
    }

    @FXML
    public void copyToPlanDay(ActionEvent event) {
        int index = resultlist.getSelectionModel().getSelectedIndex();
        BibleFulltextEngine.BibleTextKey link = verseKeys.get(index);
        BIBLEBOOK book = theBooks.get(link.getBook() - 1);
        JAXBElement<CHAPTER> jaxbChapter = book.getCHAPTER().get(link.getChapter() - 1);
        CHAPTER chapter = jaxbChapter.getValue();
        List<Day> dayList = ensureFirstDay();
        Day theDay = dayList.get(editPlanDayIndex);
        Vers vers = new Vers();
        vers.setBook(book.getBnumber());
        vers.setChapter(chapter.getCnumber());
        vers.setVtext(resultlist.getItems().get(index));
        vers.getVers().add(BigInteger.valueOf(link.getVers()));
        theDay.getVerses().add(vers);
        String versHtml = HTMLRendering.renderVersesASDoc(selected, utils, theDay.getVerses());
        setPlanOutputSelected(theDay, versHtml);
    }

    @FXML
    public void setDevotionalText(ActionEvent event) {
        List<Day> dayList = ensureFirstDay();
        Day theDay = dayList.get(editPlanDayIndex);
        storeDevotional(theDay);
        String versHtml = HTMLRendering.renderVersesASDoc(selected, utils, theDay.getVerses());
        setPlanOutputSelected(theDay, versHtml);
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
        InputStream stream = JesusMisc.showOpenDialog(event, notesTable);
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
        OutputStream os = JesusMisc.showSaveDialog(event, notesTable);
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
        devotionalEdit.setHtmlText("<html><header/><body/></html>");
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

    @FXML
    public void copyHighlight(ActionEvent event) {
        Integer row = highlightsTab.getSelectionModel().getSelectedIndex();
        Vers vers = highlights.getHighlight().get(row);
        BIBLEBOOK book = theBooks.get(vers.getBook().intValue() - 1);
        JAXBElement<CHAPTER> jaxbChapter = book.getCHAPTER().get(vers.getChapter().intValue() - 1);
        CHAPTER chapter = jaxbChapter.getValue();
        HighlightsEntry entry = highlightsTab.getItems().get(row);
        String listText = entry.getVerseLink();
        int startIndex = 0;
        StringBuffer htmlBuffer = new StringBuffer();
        for (BigInteger versNo: vers.getVers()) {
            int endIndex = listText.indexOf("]", startIndex);
            BibleFulltextEngine.BibleTextKey link =
                    new BibleFulltextEngine
                            .BibleTextKey(vers.getBook().intValue(),
                            vers.getChapter().intValue(), versNo.intValue());
            StringBuffer buffer = HTMLRendering.buildVersHTML(link, listText.substring(startIndex, endIndex + 1), chapter);
            startIndex = endIndex + 1;
            HTMLRendering.renderVers(htmlBuffer, buffer.toString());
            copyHtmlToClip(htmlBuffer);
        }

    }

    @FXML
    public void copyHighlightToPlan(ActionEvent event) {
        Integer row = highlightsTab.getSelectionModel().getSelectedIndex();
        Vers vers = highlights.getHighlight().get(row);
        List<Day> dayList = ensureFirstDay();
        Day theDay = dayList.get(editPlanDayIndex);
        theDay.getVerses().add(vers);
        String versHtml = HTMLRendering.renderVersesASDoc(selected, utils, theDay.getVerses());
        setPlanOutputSelected(theDay, versHtml);
    }


    private void copyHtmlToClip(StringBuffer htmlBuffer) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();

        content.putHtml(htmlBuffer.toString());
        clipboard.setContent(content);
    }

    @FXML
    public void playChapter(ActionEvent event) {
        BibleTextUtils.BookLink link =
                new BibleTextUtils.BookLink(actBookLabel, actChapter, Arrays.asList(1));
        this.playBible = new PlayBible("C:\\Users\\haral\\biblebooks\\to_hear\\MP3"
                , chapterPlayView);
        this.playBible.playChapter(link);
    }


    @FXML
    public void copyNote(ActionEvent event) {
        Integer row = notesTable.getSelectionModel().getSelectedIndex();
        Note note = noteList.getVersenote().get(row);
        String noteText = note.getNote();
        List<Vers> verses = note.getVerslink();
        HTMLRendering.renderVerses(selected, utils,verses);
        StringBuffer htmlBuffer = new StringBuffer();
        htmlBuffer.append(HTMLRendering.renderVerses(selected, utils,verses));
        htmlBuffer.append("<p>" + noteText + "<p>");
        copyHtmlToClip(htmlBuffer);

    }

    @FXML
    public void copyToPlanFromNote(ActionEvent event) {
        Integer row = notesTable.getSelectionModel().getSelectedIndex();
        Note note = noteList.getVersenote().get(row);
        List<Day> dayList = ensureFirstDay();
        Day theDay = dayList.get(editPlanDayIndex);
        List<Vers> verses = note.getVerslink();
        theDay.getVerses().addAll(verses);
        String versHtml = HTMLRendering.renderVersesASDoc(selected, utils, theDay.getVerses());
        setPlanOutputSelected(theDay, versHtml);
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
    public void loadSearch(ActionEvent event) {
        InputStream is = JesusMisc.showOpenDialog(event, notesTable);
        try {
            ObjectInputStream objIN = new ObjectInputStream(is);
            this.verseKeys = (List<BibleFulltextEngine.BibleTextKey>)objIN.readObject();
            objIN.close();
            resultlist.getItems().clear();
            for (BibleFulltextEngine.BibleTextKey versKey: verseKeys) {
                BIBLEBOOK book = theBooks.get(versKey.getBook() - 1);
                CHAPTER chapter = book.getCHAPTER().get(versKey.getChapter() -1).getValue();
                Map.Entry<BibleFulltextEngine.BibleTextKey, String> entry  =
                        utils.getVersEntry(chapter, versKey);
                resultlist.getItems().add(utils.generateVersEntry(entry.getKey(), entry.getValue()));

            }
        } catch (IOException | ClassNotFoundException ex) {

        }
    }

    @FXML
    public void saveSearch(ActionEvent event) {
        OutputStream os = JesusMisc.showSaveDialog(event, notesTable);
        try {
            ObjectOutputStream objOut = new ObjectOutputStream(os);
            objOut.writeObject(this.verseKeys);
            objOut.close();
        } catch (IOException ex) {

        }
    }


    @FXML
    public void toPDF(ActionEvent event) {
        String htmlText = devotionalEdit.getHtmlText();
        OutputStream pdfOut = JesusMisc.showSaveDialog(event, notesTable);
        HTMLToPDF.convertTo(htmlText, pdfOut);
    }

    @FXML
    public void printDev(ActionEvent event) {
        try {
            HTMLToPDF.printDocument(devotionalEdit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadNotes(ActionEvent event) {
        InputStream input = JesusMisc.showOpenDialog(event, notesTable);
        Versnotes notes = PersistenceLayer.loadNotes(input);
        noteList.getVersenote().addAll(notes.getVersenote());

        loadNotesAndRender();

        notesTable.setVisible(false);
        notesTable.refresh();
        notesTable.setVisible(true);

    }

    private void loadNotesAndRender() {
        notesTable.setEditable(false);
        for (Note newNote :noteList.getVersenote()) {
            NoteTabEntry entry = getNoteTabEntry(newNote);
            notesTable.getItems().add(entry);
        }
    }

    @NotNull
    private NoteTabEntry getNoteTabEntry(Note newNote) {
        List<Vers> versList = newNote.getVerslink();
        String color = versList.get(0).getBackcolor();
        Color noteColor;
        if (color != null && !color.isEmpty()) {
            noteColor = Color.web(color);
        } else {
            noteColor = Color.CORAL;
        }

        TextRendering.storeVersRendering(versList, noteColor);
        notesTable.getSelectionModel().getSelectedItem();
        String bookLabel = utils.getBookLabels()
                .get(newNote.getVerslink().get(0).getBook().intValue() - 1 );
        Optional<BIBLEBOOK> book = utils.getBookByLabel(selected, bookLabel);
        BibleTextUtils.BookLabel bookLabClass = null;
        if (book.isPresent()) {
            bookLabClass = utils.getBookLabelAsClass(bookLabel);
            showChapter();
        }
        NoteTabEntry entry = new NoteTabEntry(utils.generateVersLink(newNote.getVerslink(), bookLabClass),
                newNote.getVerslink().get(0).getVtext()
                , newNote.getNote());
        return entry;
    }

    private void loadHighlightsAndRender() {
        highlightsTab.setEditable(false);
        for (Vers newVers :highlights.getHighlight()) {
            HighlightsEntry entry = getHighlightsEntry(newVers);
            highlightsTab.getItems().add(entry);
        }
    }

    @NotNull
    private HighlightsEntry getHighlightsEntry(Vers newVers) {
        String color = newVers.getBackcolor();
        Color noteColor;
        if (color != null && !color.isEmpty()) {
            noteColor = Color.web(color);
        } else {
            noteColor = Color.ALICEBLUE;
        }

        List<Vers> versList = new ArrayList<>();
        versList.add(newVers);
        TextRendering.storeVersRendering(versList, noteColor);
        String bookLabel = utils.getBookLabels()
                .get(newVers.getBook().intValue() - 1 );
        Optional<BIBLEBOOK> book = utils.getBookByLabel(selected, bookLabel);
        BibleTextUtils.BookLabel bookLabClass = null;
        if (book.isPresent()) {
            bookLabClass = utils.getBookLabelAsClass(bookLabel);
            showChapter();
        }
        HighlightsEntry entry = new HighlightsEntry(utils.generateVersLink(versList, bookLabClass),
                newVers.getVtext());
        return entry;
    }

    @FXML
    public void saveNotes(ActionEvent event) {
        OutputStream os = JesusMisc.showSaveDialog(event, notesTable);
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

    public enum SearchOptions {
        SIMPLE,
        EXACT,
        FUZZY
    }

}
