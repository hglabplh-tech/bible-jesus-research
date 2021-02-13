package org.harry.jesus.fxutils;

import generated.BIBLEBOOK;
import generated.CHAPTER;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.jesajautils.httpSrv.DayVerses;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Edit verses of day dialog.
 */
public class EditVersesOfDayDialog {

    /**
     * Show the dialog to edit the veres of the day
     */
    public static void showDialog() {
        Dialog<Void> dialog = new Dialog<>();
        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CLOSE);
        GridPane grid = new GridPane();
        Label bookLabel = new Label("Book:");
        Label chapterLabel = new Label("Chapter:");
        Label verseRangeLabel = new Label("Verse Range:");
        BibleTextUtils utils = BibleTextUtils.getInstance();
        List<Tuple<BIBLEBOOK, String>> books = utils.getBibleBooksCooked(utils.getSelected());
        ComboBox<String> booksCombo = new ComboBox<>();
        for (Tuple<BIBLEBOOK, String> bookTuple: books) {
            booksCombo.getItems().add(bookTuple.getSecond());
        }
        List<Tuple<Integer, CHAPTER>> chapterList = utils.getChaptersForBookCooked(books.get(0));
        ComboBox<Integer> chaptersCombo = new ComboBox<>();
        for (Tuple<Integer, CHAPTER> chapterTuple: chapterList) {
            chaptersCombo.getItems().add(chapterTuple.getFirst());
        }
        List<Integer> versesList = utils.getVersesForChapterCooked(chapterList.get(0));
        ComboBox<Integer> versesStartCombo = new ComboBox<>();
        ComboBox<Integer> versesEndCombo = new ComboBox<>();

        versesStartCombo.getItems().addAll(versesList);
        versesEndCombo.getItems().addAll(versesList);

        grid.add(bookLabel, 0 , 0);
        grid.add(booksCombo, 1 , 0);
        grid.add(chapterLabel, 2, 0);
        grid.add(chaptersCombo, 3, 0);
        grid.add(verseRangeLabel, 0, 1);
        grid.add(versesStartCombo, 1, 1);
        grid.add(versesEndCombo, 2, 1);
        Button addButton = new Button("Add Verse");
        grid.add(addButton, 3, 1);
        ListView<BibleTextUtils.BookLink> bookLinksList = new ListView<>();
        grid.add(bookLinksList, 0, 2, 4, 1);
        List<BibleTextUtils.BookLink> verses = DayVerses.getInstance().loadVerses();
        bookLinksList.getItems().addAll(verses);
        ContextMenu versesContext = new ContextMenu();
        MenuItem item = new MenuItem("delete verse");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                BibleTextUtils.BookLink item =
                        bookLinksList.getSelectionModel().getSelectedItem();
                if (item != null) {
                    bookLinksList.getItems().remove(item);
                }
            }
        });
        versesContext.getItems().add(item);
        bookLinksList.setContextMenu(versesContext);
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = booksCombo.getSelectionModel().getSelectedIndex();
                Integer bookNo = 0;
                if (index >= 0) {
                    bookNo = books.get(index).getFirst().getBnumber().intValue();
                }
                Integer chapterNo = chaptersCombo.getValue();
                List<Integer> verses = new ArrayList<>();
                if (versesStartCombo.getValue() <= versesEndCombo.getValue()) {
                    for (Integer verseNo = versesStartCombo.getValue();
                         verseNo <= versesEndCombo.getValue(); verseNo++) {
                        verses.add(verseNo);
                    }
                }
                if (bookNo > 0 && chapterNo != null && chapterNo > 0 && verses.size() > 0) {
                    BibleTextUtils.BookLink link = new BibleTextUtils.BookLink(bookNo, chapterNo, verses);
                    bookLinksList.getItems().add(link);
                }
            }
        });
        booksCombo.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                  @Override
                  public void changed(ObservableValue<? extends Number> observable,
                                      Number oldValue, Number newValue) {
                       Integer index = newValue.intValue();
                       if (index >= 0) {
                           BibleTextUtils utils = BibleTextUtils.getInstance();
                           Tuple<BIBLEBOOK, String> book = books.get(index);
                           List<Tuple<Integer, CHAPTER>> chapters = utils.getChaptersForBookCooked(book);
                           chaptersCombo.getItems().clear();
                           chapterList.clear();
                           chapterList.addAll(chapters);
                           for(Tuple<Integer, CHAPTER> chapterTuple: chapters) {
                                chaptersCombo.getItems().add(chapterTuple.getFirst());
                           }
                           List<Integer> verses = utils.getVersesForChapterCooked(chapters.get(0));
                           versesEndCombo.getItems().clear();
                           versesStartCombo.getItems().clear();
                           versesEndCombo.getItems().addAll(verses);
                           versesStartCombo.getItems().addAll(verses);

                       }
                  }

        }
        );
        chaptersCombo.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable,
                                   Number oldValue, Number newValue) {
                   Integer index = newValue.intValue();
                   if (index >= 0) {
                       BibleTextUtils utils = BibleTextUtils.getInstance();
                       List<Integer> verses = utils.getVersesForChapterCooked(chapterList.get(index));
                       versesEndCombo.getItems().clear();
                       versesStartCombo.getItems().clear();
                       versesEndCombo.getItems().addAll(verses);
                       versesStartCombo.getItems().addAll(verses);

                   }
               }

           }
        );

        pane.setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton.equals(ButtonType.APPLY)) {
                storeTheVersesOfDay(bookLinksList);
            }
           return null;
        });

        dialog.showAndWait();
    }

    private static void storeTheVersesOfDay(ListView<BibleTextUtils.BookLink> bookLinksList) {
        List<BibleTextUtils.BookLink> toDelete = new ArrayList<>();
        for (BibleTextUtils.BookLink link: DayVerses.getInstance().getVerses()) {
            if (!bookLinksList.getItems().contains(link)) {
                toDelete.add(link);
            }
        }
        for (BibleTextUtils.BookLink link: toDelete) {
            DayVerses.getInstance().getVerses().remove(link);
        }
        for (BibleTextUtils.BookLink link: bookLinksList.getItems()) {
            if (!DayVerses.getInstance().getVerses().contains(link)) {
                DayVerses.getInstance().getVerses().add(link);
            }
        }
        DayVerses.getInstance().storeVersesOfDays();
    }

    /**
     * Quick store the verses of day.
     *
     * @param link the link
     */
    public static void quickStoreTheVersesOfDay(BibleTextUtils.BookLink link) {
        List<BibleTextUtils.BookLink> toDelete = new ArrayList<>();
        if (!DayVerses.getInstance().loadVerses().contains(link)) {
                DayVerses.getInstance().getVerses().add(link);
        }

        DayVerses.getInstance().storeVersesOfDays();
    }

}
