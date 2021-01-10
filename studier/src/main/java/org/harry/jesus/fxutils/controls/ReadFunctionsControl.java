package org.harry.jesus.fxutils.controls;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.harry.jesus.fxutils.ReadLinksDialog;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.HTMLRendering;
import org.harry.jesus.jesajautils.LinkHandler;
import org.harry.jesus.synchjeremia.HistoryEntry;
import org.tinylog.Logger;

import java.util.List;

public class ReadFunctionsControl extends GridPane {



    
    private Button prevChapter = new Button("<--");

    private Button nextChapter = new Button("-->");

    private TextField chapterInfo = new TextField();

    private ChoiceBox<HistoryEntry> historyChoice = new ChoiceBox<>();

    private ChoiceBox<BibleTextUtils.DictionaryInstance> dictionaries = new ChoiceBox<>();

    public ReadFunctionsControl() {
        this.add(prevChapter, 0, 0);
        this.add(nextChapter, 2, 0);
        this.add(chapterInfo, 1, 0);
        this.add(historyChoice, 1, 2);
        this.add(dictionaries, 2, 2);
    }

    public ChoiceBox<HistoryEntry> getHistoryChoice() {
        return historyChoice;
    }

    public ChoiceBox<BibleTextUtils.DictionaryInstance> getDictionaries() {
        return dictionaries;
    }

    public TextField getChapterInfo() {
        return chapterInfo;
    }

    public Button getPrevChapter() {
        return prevChapter;
    }

    public Button getNextChapter() {
        return nextChapter;
    }
}
