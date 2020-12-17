package org.harry.jesus;

import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.XMLBIBLE;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import javafx.scene.paint.Color;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.model.*;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.TextRendering;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.jesajautils.browse.LinkedImage;
import org.harry.jesus.jesajautils.browse.ParStyle;
import org.harry.jesus.jesajautils.browse.TextStyle;
import org.jetbrains.annotations.NotNull;
import org.reactfx.util.Either;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML
    ListView<String> bibles;

    @FXML
    ListView<String> footerNotes;

    @FXML
    TreeView<String> booksTree;

    @FXML
    VirtualizedScrollPane<GenericStyledArea<ParStyle, Either<String, LinkedImage>, TextStyle>> chapterReader;


    BorderPane borderPane = null;

    BibleTextUtils utils;

    List<BIBLEBOOK> theBooks = new ArrayList<>();

    FoldableStyledArea area;

    XMLBIBLE selected = null;

    @FXML
    public void initialize() {
        Parent parent = booksTree.getParent();
        this.borderPane = (BorderPane) parent;
        booksTree.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<TreeItem<String>>() {

                    @Override
                    public void changed(ObservableValue<? extends TreeItem<String>> observableValue, TreeItem<String> old_val, TreeItem<String> new_val) {
                        String value = new_val.getValue();
                        String regex = "[0-9]+";
                        TextRendering rendering = new TextRendering(utils, area);
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
                            rendering.render(selected, bookLabel, chapter);
                            footerNotes.getItems().clear();
                            footerNotes.getItems().addAll(rendering.getNotes());
                        }
                    }
                });
        bibles.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                selected = utils.getBibles().get(t1.intValue());
                TreeItem<String> root = buildBooksTree();
                TextRendering rendering = new TextRendering(utils, area);
                String chapter = rendering.render(selected, utils.getBookLabels().get(0), 1);
                footerNotes.getItems().clear();
                footerNotes.getItems().addAll(rendering.getNotes());
            }
        });
        footerNotes.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                String note = footerNotes.getItems().get(t1.intValue());
                List<BibleTextUtils.BookLink> links = utils.parseLinks(note);
                if (links.size() > 0) {
                    TextRendering rendering = new TextRendering(utils, area);
                    String chapter = rendering.render(selected, links);
                    footerNotes.getItems().clear();
                    footerNotes.getItems().addAll(rendering.getNotes());
                }
            }

        });


        area = new FoldableStyledArea();
        TextOps<String, TextStyle> styledTextOps = SegmentOps.styledTextOps();
        chapterReader = new VirtualizedScrollPane(area);
        chapterReader.setMinSize(1000, 400);
        ContextMenu contMenu = new ContextMenu();
        MenuItem mItem = new MenuItem();
        mItem.setText("setColor");
        contMenu.getItems().add(mItem);
        area.contextMenuObjectProperty().setValue(contMenu);
        mItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                area.setStyle(area.getSelection().getStart(),
                        area.getSelection().getEnd(),
                        TextStyle.backgroundColor(Color.GREEN));
            }
        });
        this.borderPane.setCenter(chapterReader);
        utils = new BibleTextUtils();
        List<String> bibleNames = utils.getBibleInfos();

        for (String name: bibleNames) {
            bibles.getItems().add(name);
        }
        bibles.getSelectionModel().selectFirst();

        TreeItem<String> root = buildBooksTree();
        TextRendering rendering = new TextRendering(utils, this.area);
        String chapter = rendering.render(selected, utils.getBookLabels().get(0), 1);
        root.getChildren().addAll();
        System.out.println("second");
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
    public void openPlan(ActionEvent event) {

    }

    @FXML
    public void savePlan(ActionEvent event) {

    }

    @FXML
    public void newPlan(ActionEvent event) {

    }
}
