package org.harry.jesus.fxutils.controls;

import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.Dictionary;
import generated.XMLBIBLE;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import jesus.harry.org.plan._1.Day;
import jesus.harry.org.versnotes._1.Note;
import jesus.harry.org.versnotes._1.Vers;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.GenericStyledArea;
import org.harry.jesus.fxutils.*;
import org.harry.jesus.fxutils.controls.media.MediaControl;
import org.harry.jesus.fxutils.controls.media.PlayBible;
import org.harry.jesus.jesajautils.*;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.jesajautils.browse.ParStyle;
import org.harry.jesus.jesajautils.browse.TextStyle;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.harry.jesus.synchjeremia.*;
import org.tinylog.Logger;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.math.BigInteger;
import java.util.*;

public class BibleStudyCompoundControl extends BorderPane {

    private FoldableStyledArea area;

    private VirtualizedScrollPane<GenericStyledArea<ParStyle, String, TextStyle>> chapterReader;

    private ListView<String> biblesList;

    private TreeView<String> booksTree;

    private ListView<String> studyNotes;

    private ReadFunctionsControl topControls = new ReadFunctionsControl();

    private BibleTextUtils utils;

    private XMLBIBLE selected = null;

    private String actBookLabel;

    private MediaView chapterPlayView = new MediaView();

    private PlayBible playBible;

    private MediaControl mediaControl;

    private BibleTextUtils.BookLabel actBook = null;

    private BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();

    private List<BIBLEBOOK> theBooks = new ArrayList<>();

    private Integer actChapter = 1;

    private int selectedIndex = 0;

    private Optional<Tuple<Dictionary, AccordanceRef>> optAccordance = Optional.empty();

    private TextRendering rendering = null;

    Map<Integer, IndexRange> selectedVersesMap = new LinkedHashMap<>();



    public BibleStudyCompoundControl(BibleTextUtils utils, XMLBIBLE selected, String actBookLabel) {
        this.utils = utils;
        this.selected = selected;
        this.actBookLabel = actBookLabel;
        this.actBook = utils.getBookLabelAsClass(actBookLabel);
        this.initChapterReader();
        this.initBibleBox();
        this.initBookTree();
        this.initStudyNotes();
        this.setTop(topControls);
        initialize(utils);

    }

    private void initialize(BibleTextUtils utils) {
        List<String> bibleNames = utils.getBibleInfos();

        for (String name: bibleNames) {
            biblesList.getItems().add(name);
        }
        topControls.getHistoryChoice().getItems().addAll(context.getHistory());
        utils.loadAccordancesDownLoaded(
                new File(ApplicationProperties.getApplicationAccordanceDir()),
                context);
        topControls.getDictionaries().getItems().addAll(utils.getDictInstances());
        this.initListeners();
        TreeItem<String> root = buildBooksTree();
        this.showRoot();
    }

    private void showRoot() {
        rendering = new TextRendering(utils, this.area, actBookLabel, actChapter);
        fillTextArea();
    }

    private void fillTextArea() {
        rendering.render(selected, utils.getBookLabels().get(0), actChapter);
        actBookLabel = utils.getBookLabels().get(0);
        actBook = utils.getBookLabelAsClass(actBookLabel);
        studyNotes.getItems().clear();
        studyNotes.getItems().addAll(rendering.getNotes());
        String [] splitted = actBookLabel.split(",");
        topControls.getChapterInfo().setText("Book: " + splitted[1] + " Chapter: " + actChapter);
    }



    private void initChapterReader() {
        area = new FoldableStyledArea();
        chapterReader = new VirtualizedScrollPane(area);
        this.setCenter(chapterReader);

    }

    private void initBibleBox() {
        biblesList = new ListView<>();
        this.setRight(biblesList);
        biblesList.getSelectionModel().selectFirst();
    }

    private void initBookTree() {
        booksTree = new TreeView<>();
        this.setLeft(booksTree);
    }

    private void initStudyNotes() {
        studyNotes = new ListView<>();
        this.setBottom(studyNotes);
    }

    public void initMediaView() {
        String mediaPath = context.getSettings().getProperty(ApplicationProperties.AUDIO_PATH);
        this.playBible = new PlayBible(mediaPath
                , chapterPlayView);
        BibleTextUtils.BookLink link =
                new BibleTextUtils.BookLink(actBookLabel, actChapter, Arrays.asList(1));
        MediaPlayer mp = playBible.playChapter(link);
        if (mp != null) {
            mediaControl = new MediaControl(mp, chapterPlayView);
            topControls.add(mediaControl, 0, 1, 1, 2);
        }
    }

    private void initListeners() {
        booksTree.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<TreeItem<String>>() {

                    @Override
                    public void changed(ObservableValue<? extends TreeItem<String>> observableValue, TreeItem<String> old_val, TreeItem<String> new_val) {
                        if (new_val != null) {
                            String value = new_val.getValue();
                            if (value.equals("Book Information")) {
                                rendering = new TextRendering(utils, area, actBookLabel, actChapter);
                                rendering.showBibleInfo(selected);
                                return;
                            }
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
                                jumpToBookAndChapter(bookLabel, chapter);
                            }
                        }
                    }
                });
        biblesList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                selectedIndex = t1.intValue();
                BibleTextUtils.BibleBookInstance instance = utils.getBibleInstances().get(t1.intValue());
                selected = instance.getBible();

                optAccordance = instance.getOptDictAccRefTuple();
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
        studyNotes.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                String note = studyNotes.getItems().get(t1.intValue());
                List<BibleTextUtils.BookLink> links = LinkHandler.parseLinks(utils, note);
                if (links.size() > 0) {
                    showLink(links);
                }
            }

        });
        topControls.getPrevChapter().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                topControls.getNextChapter().setDisable(false);
                actChapter = actChapter - 1;
                boolean found = showChapter();
                if (!found) {
                    int index = utils.getBookLabels().indexOf(actBookLabel);
                    index--;
                    if (index >= 0) {
                        actBookLabel = utils.getBookLabels().get(index);
                        actChapter  = selected.getBIBLEBOOK().get(index).getValue().getCHAPTER().size();
                        found = showChapter();
                        if (!found) {
                            topControls.getPrevChapter().setDisable(true);
                        }
                    } else {
                        topControls.getPrevChapter().setDisable(true);
                    }
                }
            }
        });
        topControls.getNextChapter().setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                topControls.getPrevChapter().setDisable(false);
                actChapter = actChapter + 1;
                boolean found = showChapter();
                if (!found) {
                    int index = utils.getBookLabels().indexOf(actBookLabel);
                    index++;
                    if (index < 66) {
                        actBookLabel = utils.getBookLabels().get(index);
                        actChapter = 1;
                        found = showChapter();
                        if (!found) {
                            topControls.getNextChapter().setDisable(true);
                        }
                    } else {
                        topControls.getNextChapter().setDisable(true);
                    }
                }
            }
        });

        topControls.getHistoryChoice().getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        if (newValue.intValue() >= 0
                                && newValue.intValue() < topControls.getHistoryChoice().getItems().size()) {
                            HistoryEntry entry = topControls.getHistoryChoice()
                                    .getItems().get(newValue.intValue());
                            BibleTextUtils.BookLink link = entry.getBookLink();
                            jumpToBookAndChapter(link.getBookLabel(), link.getChapter());
                        }
                    }
                });

        topControls.getDictionaries().getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                                Number newValue) {
                BibleTextUtils.DictionaryInstance entry =
                        topControls.getDictionaries().getItems().get(newValue.intValue());
                ViewAccordanceDialog.showAccordanceDialog(utils, area,
                        entry.getDictionaryRef().getFilename());
            }
        });


        area.addEventHandler(SetLinkEvent.SET_LINK_EVENT, event -> {
            BibleTextUtils.BookLink link = event.getLink();
            jumpToBookAndChapter(link.getBookLabel(), link.getChapter());
            setSelectedVersVisible(link);
        });
        area.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    IndexRange range = area.getSelection();
                    Map.Entry<Integer, IndexRange> versPointer =
                            rendering.selectVerseByGivenRange(range, true);
                    if (selectedVersesMap.get(versPointer.getKey()) != null) {
                        selectedVersesMap.remove(versPointer.getKey(), versPointer.getValue());
                        rendering.selectVerseByGivenRange(range, false);

                    } else {
                        selectedVersesMap.put(versPointer.getKey(), versPointer.getValue());
                    }
                }
            }
        });
    }

    private TreeItem<String> buildBooksTree() {
        if (selected != null) {
            TreeItem<String> root = new TreeItem<>();
            root.setValue("The books");
            booksTree.setRoot(root);
            TreeItem item = new TreeItem("Book Information");
            root.getChildren().add(item);
            theBooks.clear();
            List<JAXBElement<BIBLEBOOK>> books = selected.getBIBLEBOOK();
            for (JAXBElement<BIBLEBOOK> book : books) {
                theBooks.add(book.getValue());
            }

            for (BIBLEBOOK theBook : theBooks) {
                String label = utils.getBookLabels().get(theBook.getBnumber().intValue() - 1);
                item = new TreeItem(label);
                root.getChildren().add(item);
                for (JAXBElement<CHAPTER> chapter : theBook.getCHAPTER()) {
                    TreeItem<String> cItem = new TreeItem<>(chapter.getValue().getCnumber().toString());
                    item.getChildren().add(cItem);
                }

            }
            return root;
        }
        booksTree.refresh();
        return null;
    }

    private void jumpToBookAndChapter(String bookLabel, int chapter) {
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

    private void jumpToVers(IndexRange range) {
        area.selectRange(range.getStart(), range.getEnd());
        area.setParVisableSelection();
    }

    private void setSelectedVersVisible(BibleTextUtils.BookLink link) {
        IndexRange range = rendering.getChapterMap().get(link.getVerses().get(0));
        rendering.selectVerseColorByGivenRange(range, Color.CORAL);
        jumpToVers(range);
    }

    public boolean showChapter() {
        rendering = new TextRendering(utils, this.area, actBookLabel, actChapter);
        HistoryEntry entry = createHistoryEntry();
        if (!context.getHistory().contains(entry)) {
            context.addHistoryEntry(entry);
        }
        if (!topControls.getHistoryChoice().getItems().contains(entry)) {
            topControls.getHistoryChoice().getItems().add(0, entry);
            topControls.getHistoryChoice().getSelectionModel().select(entry);
        }
        boolean found = fillChapterText();
        return found;
    }

    private HistoryEntry createHistoryEntry() {
        BibleTextUtils.BookLink link = new BibleTextUtils.BookLink(actBookLabel, actChapter);
        Calendar date = Calendar.getInstance();
        Optional<CHAPTER> chapter =
                utils.getChapter(utils.getBooks(selected).get(actBook.getBookNumber() - 1),
                        actChapter);
        if (chapter.isPresent()) {
            Map<BibleFulltextEngine.BibleTextKey, String> verses =
                    utils.getVerses(chapter.get(), actBook.getBookNumber());
            Optional<Map.Entry<BibleFulltextEngine.BibleTextKey, String>> vers =
                    verses.entrySet().stream().filter(e -> e.getKey().getVers() == 1).findFirst();
            if (vers.isPresent()) {
                HistoryEntry entry = new HistoryEntry(vers.get().getValue(), date, link);
                return entry;
            }
        }
        return null;
    }


    private void showLink(List<BibleTextUtils.BookLink> links) {
        String htmlText = HTMLRendering.renderLink(utils, selected, links);
        ReadLinksDialog.showReadLinkDialog(utils, area, htmlText);
    }

    private boolean fillChapterText() {
        boolean found = rendering.render(selected, actBookLabel, actChapter);
        if (found) {
            studyNotes.getItems().clear();
            studyNotes.getItems().addAll(rendering.getNotes());
            studyNotes.getItems().add("Fuzzy Link matches -->");
            for (String note : rendering.getNotes()) {
                String links = "";
                try {
                    links = LinkHandler.generateLinksFuzzy(utils, note);
                } catch (Exception ex) {
                    Logger.trace("Something went wrong with fuzzy!!! This feature has to be enhanced");
                }
                if (!links.isEmpty()) {
                    studyNotes.getItems().add(links);
                }
            }
            String[] splitted = actBookLabel.split(",");
            selectedVersesMap.clear();
            rendering.clearRendering();
            topControls.getChapterInfo().setText("Book: " + splitted[1] + " Chapter: " + actChapter);
            initMediaView();
        }
        return found;
    }


    public Map<Integer, IndexRange> getSelectedMapSorted() {
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

    public XMLBIBLE getSelected() {
        return selected;
    }

    public MediaView getChapterPlayView() {
        return chapterPlayView;
    }
}