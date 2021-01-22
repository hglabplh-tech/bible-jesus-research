package org.harry.jesus.fxutils.controls;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.apache.commons.io.IOUtils;
import org.harry.jesus.fxutils.ReadLinksDialog;
import org.harry.jesus.fxutils.event.DeployDictionary;
import org.harry.jesus.fxutils.event.SetLinkEvent;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.HTMLRendering;
import org.harry.jesus.jesajautils.LinkHandler;
import org.harry.jesus.jesajautils.configjaxbser.DictionaryRef;
import org.harry.jesus.synchjeremia.BibleThreadPool;
import org.harry.jesus.synchjeremia.HistoryEntry;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * The type Read functions control.
 */
public class ReadFunctionsControl extends GridPane {



    
    private Button prevChapter = new Button("<--");

    private Button nextChapter = new Button("-->");

    private TextField chapterInfo = new TextField();

    private ChoiceBox<HistoryEntry> historyChoice = new ChoiceBox<>();

    private ChoiceBox<BibleTextUtils.DictionaryInstance> dictionaries = new ChoiceBox<>();

    /**
     * Instantiates a new Read functions control.
     */
    public ReadFunctionsControl() {
        this.add(prevChapter, 0, 0);
        this.add(nextChapter, 2, 0);
        this.add(chapterInfo, 1, 0);
        this.add(historyChoice, 1, 2);
        this.add(dictionaries, 2, 2);
        dictionaries.addEventHandler(DeployDictionary.DEPLOY_DICT_EVENT, event -> {
            DictionaryRef dictRef = event.getDictRef();
            String dictDir = BibleThreadPool.getContext()
                    .getAppSettings()
                    .getBaseConfig()
                    .getDictionariesDir();
            try {
                FileInputStream inStream = new FileInputStream(dictRef.getPathToBook());
                File outFile = new File(dictDir, dictRef.getFilename() + ".xml");
                File htmlFile = new File(dictDir, dictRef.getFilename() + ".html");
                FileOutputStream outStream = new FileOutputStream(outFile);
                IOUtils.copy(inStream, outStream);
                if (htmlFile.exists()) {
                    htmlFile.delete();
                }
                BibleTextUtils.getInstance()
                        .loadAccordancesDownLoaded(new File(dictDir),
                                BibleThreadPool.getContext());
                dictionaries.getItems().clear();
                dictionaries.getItems().addAll(BibleTextUtils.getInstance().getDictInstances());
            } catch (IOException ex) {
                Logger.trace(ex);
                Logger.trace("Refresh dictionaries failed with : " + ex.getMessage());
            }

        });
    }

    /**
     * Gets history choice.
     *
     * @return the history choice
     */
    public ChoiceBox<HistoryEntry> getHistoryChoice() {
        return historyChoice;
    }

    /**
     * Gets dictionaries.
     *
     * @return the dictionaries
     */
    public ChoiceBox<BibleTextUtils.DictionaryInstance> getDictionaries() {
        return dictionaries;
    }

    /**
     * Gets chapter info.
     *
     * @return the chapter info
     */
    public TextField getChapterInfo() {
        return chapterInfo;
    }

    /**
     * Gets prev chapter.
     *
     * @return the prev chapter
     */
    public Button getPrevChapter() {
        return prevChapter;
    }

    /**
     * Gets next chapter.
     *
     * @return the next chapter
     */
    public Button getNextChapter() {
        return nextChapter;
    }
}
