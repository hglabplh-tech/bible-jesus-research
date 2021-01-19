package org.harry.jesus;

import generated.BIBLEBOOK;
import generated.CHAPTER;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.*;

import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import jesus.harry.org.highlights._1.Highlights;
import jesus.harry.org.plan._1.Day;
import jesus.harry.org.plan._1.Plan;
import jesus.harry.org.versnotes._1.Note;
import jesus.harry.org.versnotes._1.Vers;
import jesus.harry.org.versnotes._1.Versnotes;
import org.fxmisc.richtext.model.*;
import org.harry.jesus.danielpersistence.PersistenceLayer;
import org.harry.jesus.fxutils.*;
import org.harry.jesus.fxutils.controls.BibleStudyCompoundControl;
import org.harry.jesus.fxutils.controls.HTMLEditorExt;
import org.harry.jesus.fxutils.controls.media.MediaControl;
import org.harry.jesus.fxutils.controls.media.PlayBible;
import org.harry.jesus.fxutils.graphics.ImageMaker;
import org.harry.jesus.fxutils.graphics.RandomGraphics;
import org.harry.jesus.jesajautils.*;
import org.harry.jesus.jesajautils.browse.TextStyle;
import org.harry.jesus.jesajautils.editor.HTMLToPDF;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.harry.jesus.jesajautils.fulltext.StatisticsCollector;

import org.harry.jesus.synchjeremia.*;
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
    ListView<String> bibles;

    @FXML
    ChoiceBox<HistoryEntry> history;

    @FXML
    ListView<String> footerNotes;

    @FXML
    TreeView<String> booksTree;

    @FXML
    TextField query;

    @FXML
    ListView<String> resultlist;


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




    @FXML ChoiceBox<BibleTextUtils.DictionaryInstance> dictionaries;

    BorderPane borderPane = null;

    BibleTextUtils utils;

    TextRendering rendering = null;

    BibleThreadPool.ThreadBean context = null;



    List<BibleFulltextEngine.BibleTextKey> verseKeys;

    Versnotes noteList;

    Highlights highlights;

    Plan planDays = new Plan();



    int planDayNumber = 0;

    int editPlanDayIndex = 0;

    int selectedIndex = 0;

    private PlayBible playBible;

    private MediaControl mediaControl;

    private BibleStudyCompoundControl bibleStudy;




    @FXML
    public void initialize() {
        printDev.setDisable(true);
        initListeners();
        HTMLEditorExt editorExt = new HTMLEditorExt(devotionalEdit);
        context = BibleThreadPool.getContext();


        VBox.setVgrow(mainTabPane, Priority.ALWAYS);

        SynchThread.loadRendering(context);
        SynchThread.loadHistory(context);
        SynchThread.loadNotes(context);
        SynchThread.loadHighlights(context);
        String mediaPath = context.getAppSettings().getBaseConfig().getMediaPath();
        SynchThread.loadApplicationSettings(context);
        utils = BibleTextUtils.getInstance();

        if (utils.getBibleInstances().size() > 0) {
            utils.setSelected(utils.getBibleInstances().get(0).getBible());
        }
        searchOptions.getItems().addAll(SearchOptions.SIMPLE,
                SearchOptions.EXACT,
                SearchOptions.FUZZY);
        searchOptions.getSelectionModel().select(0);
        noteList = context.getNoteList();
        verseKeys = context.getVerseKeys();
        highlights = context.getHighlights();


        TextOps<String, TextStyle> styledTextOps = SegmentOps.styledTextOps();



        if (utils.getSelected() != null) {
            BibleTextUtils utils = BibleTextUtils.getInstance();
            bibleStudy =
                    new BibleStudyCompoundControl(utils.getSelected(),
                            utils.getBookLabels().get(0));
            initAreaContextMenu();
            readBible.setContent(bibleStudy);
            bibleStudy.setMinWidth(1198);
            bibleStudy.setMinHeight(795);
            loadNotesAndRender();
            loadHighlightsAndRender();

        } else {
            new SettingsDialog().showAppSettingsDialog();
            System.exit(0);
        }

    }

    private void initListeners() {
        planList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                editPlanDayIndex = newValue.intValue();
                Day theDay = planDays.getDay().get(editPlanDayIndex);
                String versHtml = HTMLRendering.renderVersesASDoc(bibleStudy.getSelected(), utils, theDay.getVerses(), null);
                setPlanOutputSelected(theDay, versHtml);


            }

        });


    }

    private void fireLinkEvent(BibleTextUtils.BookLink link) {
        try {
            SetLinkEvent event = new SetLinkEvent(link);
            this.bibleStudy.getArea().fireEvent(event);
        } catch (Exception ex) {
            org.pmw.tinylog.Logger.trace(ex);
            org.pmw.tinylog.Logger.trace("cannot send Link Event");
        }
    }



    private void initAreaContextMenu() {
        ContextMenu contMenu = new ContextMenu();
        MenuItem mItem = new MenuItem();
        mItem.setText("Search accordance Selected word");
        contMenu.getItems().add(mItem);
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                bibleStudy.getArea().emitSearxchToAll();
            }

        });
        mItem = new MenuItem();
        mItem.setText("Highlight selected Verses");
        contMenu.getItems().add(mItem);
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                highlightsTab.getSelectionModel().setCellSelectionEnabled(true);
                highlightsTab.setEditable(false);
                Optional<Color> resultColor = ColorDialog.callColorDialog();
                List<Vers> versList = new ArrayList<>();
                Vers vers = BibleTextUtils.generateVerses(utils,
                        bibleStudy.getActBook(),
                        bibleStudy.getActChapter(),
                        bibleStudy.getArea(),
                        bibleStudy.getSelectedMapSorted());
                if (resultColor.isPresent()) {
                    for (Integer key: bibleStudy.getSelectedMapSorted().keySet()) {
                        IndexRange range = bibleStudy.getSelectedMapSorted().get(key);
                        TextRendering.setRangeColor(bibleStudy.getArea(),
                                resultColor.get().toString(), range);
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
                bibleStudy.clearSelected();
            }
        });
        mItem = new MenuItem();
        mItem.setText("Create Verses Image");
        contMenu.getItems().add(mItem);
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                List<Vers> versList = new ArrayList<>();
                Vers vers = BibleTextUtils.generateVerses(utils,
                        bibleStudy.getActBook(),
                        bibleStudy.getActChapter(),
                        bibleStudy.getArea(),
                        bibleStudy.getSelectedMapSorted());
                Optional<Image> result = new CreatePictureDialog().showPictureCreateDialog(vers);
                if (result.isPresent()) {
                    // only when generating sample images
                    //RandomGraphics.paint();
                    ImageMaker.saveToFile(result.get(), null);
                }
                bibleStudy.clearSelected();
            }

        });
        mItem = new MenuItem();
        mItem.setText("createNote");
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Note theNote = new Note();
                Vers vers = BibleTextUtils.generateVerses(utils,
                        bibleStudy.getActBook(),
                        bibleStudy.getActChapter(),
                        bibleStudy.getArea(),
                        bibleStudy.getSelectedMapSorted());
                theNote.getVerslink().add(vers);

                Optional<Note> newNote = CreateNoteDialog.showNoteCreateDialog(theNote);
                if (newNote.isPresent()) {
                    Color noteColor = null;

                    notesTable.getSelectionModel().setCellSelectionEnabled(true);
                    notesTable.setEditable(false);

                    notesTable.getSelectionModel().getSelectedItem();
                    NoteTabEntry entry = new NoteTabEntry(utils.generateVersLink(newNote.get().getVerslink(),
                            bibleStudy.getActBook()),
                            newNote.get().getVerslink().get(0).getVtext()
                            , newNote.get().getNote());
                    notesTable.getItems().add(entry);
                    List<Vers> versList = newNote.get().getVerslink();
                    TextRendering.storeVersRendering(versList, noteColor);
                    for (IndexRange range: bibleStudy.getSelectedMapSorted().values()) {
                        TextRendering.setRangeColor(bibleStudy.getArea(),
                                newNote.get().getVerslink().get(0).getBackcolor(), range);
                    }
                    noteList.getVersenote().add(newNote.get());
                    notesTable.setVisible(false);
                    notesTable.refresh();
                    notesTable.setVisible(true);
                    bibleStudy.clearSelected();

                }


            }
        });
        contMenu.getItems().add(mItem);
        mItem = new MenuItem();
        mItem.setText("Copy");
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Note theNote = new Note();

                Vers vers = BibleTextUtils.generateVerses(utils,
                        bibleStudy.getActBook(),
                        bibleStudy.getActChapter(),
                        bibleStudy.getArea(),
                        bibleStudy.getSelectedMapSorted());
                List<Vers> verses = new ArrayList<>();
                verses.add(vers);
                String versHtml = HTMLRendering.renderVerses(bibleStudy.getSelected(), utils, verses);
                copyHtmlToClip(new StringBuffer(versHtml), DataFormat.HTML);
                bibleStudy.clearSelected();
            }
        });
        contMenu.getItems().add(mItem);
        mItem = new MenuItem();
        mItem.setText("copy Link to last PlanDay");
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                List<Day> dayList = ensureFirstDay();
                Day theDay = dayList.get(dayList.size() - 1);
                Vers vers = BibleTextUtils.generateVerses(utils,
                        bibleStudy.getActBook(),
                        bibleStudy.getActChapter(),
                        bibleStudy.getArea(),
                        bibleStudy.getSelectedMapSorted());
                theDay.getVerses().add(vers);
                String versHtml = HTMLRendering.renderVersesASDoc(bibleStudy.getSelected(), utils, theDay.getVerses(), null);
                setPlanOutputSelected(theDay, versHtml);
                bibleStudy.clearSelected();
            }

        });
        contMenu.getItems().add(mItem);
        bibleStudy.getArea().contextMenuObjectProperty().setValue(contMenu);
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


    @FXML
    public void showVersePics(ActionEvent event) {
        ShowPicGallery.showTheVersePictures();
    }

    @FXML
    public void about(ActionEvent event) {

    }

    @FXML
    public void appHelp(ActionEvent event) {

        HelpDialog.showHelp("/AppHelp.html");
    }


    @FXML
    public void settings(ActionEvent event) {
        new SettingsDialog().showAppSettingsDialog();
        if (rendering == null) {
            rendering = new TextRendering(utils,
                    bibleStudy.getArea(),
                    bibleStudy.getActBookLabel(),
                    bibleStudy.getActChapter());
        }
        bibleStudy.showChapter();
    }

    @FXML
    public void bibleInfo(ActionEvent event) {
        int index = bibles.getSelectionModel().getSelectedIndex();
        BibleInfoDialog.callBibleInfoDialog(utils.getBibleInstances().get(index));
    }

    @FXML
    public void search(ActionEvent event) {
        String pattern = query.getText();
        BibleFulltextEngine engine = new BibleFulltextEngine(bibleStudy.getSelected());
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
       bibleStudy.setActChapter(textKey.getChapter());
       bibleStudy.setActBookLabel(utils.getBookLabels().get(textKey.getBook() - 1));
        bibleStudy.setActBook(new BibleTextUtils.BookLabel(bibleStudy.getActBookLabel()));
        BibleTextUtils.BookLink link = new BibleTextUtils.BookLink(bibleStudy.getActBookLabel(),
                bibleStudy.getActChapter(), Arrays.asList(textKey.getVers()));
        fireLinkEvent(link);
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
        bibleStudy.setActChapter(vers.getChapter().intValue());
        bibleStudy.setActBookLabel(utils.getBookLabels().get(vers.getBook().intValue() - 1));
        bibleStudy.setActBook(new BibleTextUtils.BookLabel(bibleStudy.getActBookLabel()));
        List<Integer> array = new ArrayList<>();
        vers.getVers().forEach(e -> array.add(e.intValue()));
        BibleTextUtils.BookLink link = new BibleTextUtils.BookLink(
                bibleStudy.getActBookLabel(),
                bibleStudy.getActChapter(), array);
        fireLinkEvent(link);
        mainTabPane.getSelectionModel().select(readBible);
    }

    @FXML
    public void openPlan(ActionEvent event) {
        InputStream input = JesusMisc.showOpenDialog(notesTable);
        planDays = PersistenceLayer.loadPlan(input);
        String versHtml = HTMLRendering.renderVersesASDoc(bibleStudy.getSelected(), utils, planDays.getDay().get(0).getVerses(), null);
        setPlanOutputSelected(planDays.getDay().get(0), versHtml);
        for(Day day:planDays.getDay()) {
            planList.getItems().add(day.getTitle());
        }
        planDayNumber = planDays.getDay().size();
        editPlanDayIndex = planDayNumber - 1;
    }

    @FXML
    public void savePlan(ActionEvent event) {
        OutputStream os = JesusMisc.showSaveDialog(notesTable);
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
        String versHtml = HTMLRendering.renderVersesASDoc(bibleStudy.getSelected(), utils, newDay.getVerses(), null);
        setPlanOutputSelected(newDay, versHtml);
    }

    @FXML
    public void copyToPlanDay(ActionEvent event) {
        int index = resultlist.getSelectionModel().getSelectedIndex();
        BibleFulltextEngine.BibleTextKey link = verseKeys.get(index);
        BIBLEBOOK book = bibleStudy.getTheBooks().get(link.getBook() - 1);
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
        String versHtml = HTMLRendering.renderVersesASDoc(bibleStudy.getSelected(), utils, theDay.getVerses(), null);
        setPlanOutputSelected(theDay, versHtml);
    }

    @FXML
    public void setDevotionalText(ActionEvent event) {
        List<Day> dayList = ensureFirstDay();
        Day theDay = dayList.get(editPlanDayIndex);
        storeDevotional(theDay);
        String versHtml = HTMLRendering.renderVersesASDoc(bibleStudy.getSelected(), utils, theDay.getVerses(), null);
        setPlanOutputSelected(theDay, versHtml);
    }



    @FXML
    public void genDictHTML(ActionEvent event) {
        String dictDir = BibleThreadPool.getContext()
                .getAppSettings().getBaseConfig().getDictionariesDir();
        GenDictHTMLScene.generateDictHTML(utils, new File(dictDir));
    }

    @FXML
    public void loadDev(ActionEvent event) {
        byte [] buffer = new byte[4096];
        InputStream stream = JesusMisc.showOpenDialog(notesTable);
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
        OutputStream os = JesusMisc.showSaveDialog(notesTable);
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
        BIBLEBOOK book = bibleStudy.getTheBooks().get(link.getBook() - 1);
        JAXBElement<CHAPTER> jaxbChapter = book.getCHAPTER().get(link.getChapter() - 1);
        CHAPTER chapter = jaxbChapter.getValue();
        String listText = resultlist.getItems().get(index);
        int endIndex = listText.indexOf("]");
        StringBuffer buffer = HTMLRendering.buildVersHTML(link, listText.substring(0, endIndex + 1), chapter);
        HTMLRendering.renderVers(htmlBuffer, buffer.toString(),null);

        copyHtmlToClip(htmlBuffer, DataFormat.HTML);


    }

    @FXML
    public void copyHighlight(ActionEvent event) {
        Integer row = highlightsTab.getSelectionModel().getSelectedIndex();
        Vers vers = highlights.getHighlight().get(row);
        BIBLEBOOK book = bibleStudy.getTheBooks().get(vers.getBook().intValue() - 1);
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
            HTMLRendering.renderVers(htmlBuffer, buffer.toString(), null);
            copyHtmlToClip(htmlBuffer, DataFormat.HTML);
        }

    }

    @FXML
    public void copyHighlightToPlan(ActionEvent event) {
        Integer row = highlightsTab.getSelectionModel().getSelectedIndex();
        Vers vers = highlights.getHighlight().get(row);
        List<Day> dayList = ensureFirstDay();
        Day theDay = dayList.get(editPlanDayIndex);
        theDay.getVerses().add(vers);
        String versHtml = HTMLRendering.renderVersesASDoc(bibleStudy.getSelected(), utils, theDay.getVerses(), null);
        setPlanOutputSelected(theDay, versHtml);
    }


    private void copyHtmlToClip(StringBuffer htmlBuffer, DataFormat format) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();

        content.put(format, htmlBuffer.toString());
        clipboard.setContent(content);
    }




    @FXML
    public void copyNote(ActionEvent event) {
        Integer row = notesTable.getSelectionModel().getSelectedIndex();
        Note note = noteList.getVersenote().get(row);
        String noteText = note.getNote();
        List<Vers> verses = note.getVerslink();
        StringBuffer htmlBuffer = new StringBuffer();
        htmlBuffer.append(
                HTMLRendering.renderVersesASDoc(bibleStudy.getSelected(),
                        utils,verses, noteText));
        copyHtmlToClip(htmlBuffer, DataFormat.HTML);

    }

    @FXML
    public void copyToPlanFromNote(ActionEvent event) {
        Integer row = notesTable.getSelectionModel().getSelectedIndex();
        Note note = noteList.getVersenote().get(row);
        List<Day> dayList = ensureFirstDay();
        Day theDay = dayList.get(editPlanDayIndex);
        List<Vers> verses = note.getVerslink();
        theDay.getVerses().addAll(verses);
        String versHtml = HTMLRendering.renderVersesASDoc(bibleStudy.getSelected(), utils, theDay.getVerses(), null);
        setPlanOutputSelected(theDay, versHtml);
    }

    @FXML
    public void loadSearch(ActionEvent event) {
        InputStream is = JesusMisc.showOpenDialog(notesTable);
        try {
            ObjectInputStream objIN = new ObjectInputStream(is);
            this.verseKeys = (List<BibleFulltextEngine.BibleTextKey>)objIN.readObject();
            objIN.close();
            resultlist.getItems().clear();
            for (BibleFulltextEngine.BibleTextKey versKey: verseKeys) {
                BIBLEBOOK book = bibleStudy.getTheBooks().get(versKey.getBook() - 1);
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
        OutputStream os = JesusMisc.showSaveDialog(notesTable);
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
        OutputStream pdfOut = JesusMisc.showSaveDialog(notesTable);
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
        InputStream input = JesusMisc.showOpenDialog(notesTable);
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
        Optional<BIBLEBOOK> book = utils.getBookByLabel(bibleStudy.getSelected(), bookLabel);
        BibleTextUtils.BookLabel bookLabClass = null;
        if (book.isPresent()) {
            bookLabClass = utils.getBookLabelAsClass(bookLabel);
            bibleStudy.showChapter();
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
        Optional<BIBLEBOOK> book = utils.getBookByLabel(bibleStudy.getSelected(), bookLabel);
        BibleTextUtils.BookLabel bookLabClass = null;
        if (book.isPresent()) {
            bookLabClass = utils.getBookLabelAsClass(bookLabel);
            bibleStudy.showChapter();
        }
        HighlightsEntry entry = new HighlightsEntry(utils.generateVersLink(versList, bookLabClass),
                newVers.getVtext());
        return entry;
    }

    @FXML
    public void saveNotes(ActionEvent event) {
        OutputStream os = JesusMisc.showSaveDialog(notesTable);
        PersistenceLayer.storeNotes(noteList, os);
    }



    public enum SearchOptions {
        SIMPLE,
        EXACT,
        FUZZY
    }

}
