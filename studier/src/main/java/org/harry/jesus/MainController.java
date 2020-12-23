package org.harry.jesus;

import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.XMLBIBLE;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import javafx.scene.paint.Color;
import jesus.harry.org.versnotes._1.Note;
import jesus.harry.org.versnotes._1.Vers;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.model.*;
import org.harry.jesus.fxutils.CreateNoteDialog;
import org.harry.jesus.fxutils.NoteTabEntry;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.TextRendering;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.jesajautils.browse.LinkedImage;
import org.harry.jesus.jesajautils.browse.ParStyle;
import org.harry.jesus.jesajautils.browse.TextStyle;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.harry.jesus.jesajautils.fulltext.StatisticsCollector;
import org.jetbrains.annotations.NotNull;
import org.reactfx.util.Either;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
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
    VirtualizedScrollPane<GenericStyledArea<ParStyle, Either<String, LinkedImage>, TextStyle>> chapterReader;

    @FXML private TableView<NoteTabEntry> notesTable;
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
                for (IndexRange range: selectedVersesMap.values()) {
                    area.setStyle(range.getStart(),
                            range.getEnd(),
                            TextStyle.backgroundColor(Color.GREEN));
                }
            }
        });
        mItem = new MenuItem();
        mItem.setText("createNote");
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Note theNote = new Note();

                for (Integer versNo: selectedVersesMap.keySet()) {
                    Vers vers = new Vers();
                    vers.getVers().add(BigInteger.valueOf((long)versNo));
                    vers.setChapter(BigInteger.valueOf(actChapter));
                    vers.setBook(BigInteger.valueOf(actBook.getBookNumber()));
                    IndexRange range = selectedVersesMap.get(versNo);
                    String vText = area.getText(range);
                    vers.setVtext(vText);
                    theNote.getVerslink().add(vers);
                }
                Optional<Note> newNote = CreateNoteDialog.showNoteCreateDialog(theNote);
                if (newNote.isPresent()) {
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
    }

    private void initChapterReader() {
        Parent parent = booksTree.getParent();
        this.borderPane = (BorderPane) parent;
        area = new FoldableStyledArea();
        chapterReader = new VirtualizedScrollPane(area);
        chapterReader.setMinSize(400, 400);
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
        chapterTitle.setText("Book: " + splitted[1] + " Chapter: " + actChapter);
        return found;
    }

    private void showLink(List<BibleTextUtils.BookLink> links) {
        String chapter = rendering.render(selected, links);
        footerNotes.getItems().clear();
        footerNotes.getItems().addAll(rendering.getNotes());
    }

    @NotNull
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
            resultlist.getItems().add(generateVersEntry(entry.getKey(), entry.getValue()));
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

    }

    @FXML
    public void prevChapter(ActionEvent event) {
        actChapter = actChapter - 1;
        boolean found = showChapter();
        if (!found) {

        }
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

    private String generateVersEntry (BibleFulltextEngine.BibleTextKey key, String versText) {
        List<String> csv = utils.getBookLabels();
        Optional<String> book = csv.stream()
                .filter(e -> e.contains(key.getBook().toString()))
                .findFirst();
        if (book.isPresent()) {
            String [] split = book.get().split(",");
            String versLink = "[" + split[1] + " " + key.getChapter() + "," + key.getVers() + "]: ";
            String result = versLink + versText;
            return result;
        } else {
            return versText;
        }
    }
}
