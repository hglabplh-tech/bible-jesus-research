package org.harry.jesus.fxutils.controls;

import generated.XMLBIBLE;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.configjaxbser.BibleRef;
import org.harry.jesus.jesajautils.configjaxbser.BiblesDictConfig;
import org.harry.jesus.jesajautils.configjaxbser.DictionaryRef;
import org.harry.jesus.synchjeremia.BibleThreadPool;

import java.util.Map;
import java.util.Optional;

/**
 * The settings for miscellaneous parameters for the application
 * e.g. for the verse of the day
 */
public class MiscBibleSettingsCompound extends BorderPane  {

    /**
     * The Bible refs.
     */
    private ListView<BibleRef> bibleRefs = new ListView<>();

    /**
     * The checkbox if verse of day is random
     */
    private CheckBox verseRandom = null;


    private Optional<XMLBIBLE> optBible = Optional.empty();


    /**
     * Instantiates a new Miscellaneous Settings Compound control
     */
    public MiscBibleSettingsCompound() {

        this.setCenter(bibleRefs);

        BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();
        BiblesDictConfig dictConfig = context.getAppSettings()
                .getDictConfig();

        BibleTextUtils utils = BibleTextUtils.getInstance();
        for(BibleTextUtils.BibleBookInstance bible: utils.getBibleInstances()) {
            bibleRefs.getItems().add(bible.getBibleRef());
        }
        GridPane grid = new GridPane();
        Button selectBible = new Button("Add selected to assoc");
        grid.add(selectBible, 0, 0);
        TextField selectBibleField = new TextField();
        selectBibleField.setEditable(false);
        grid.add(selectBibleField, 1, 0);
        String selected = context.getAppSettings()
                .getDictConfig()
                .getVerseOfDayBibleId();
        if (selected != null) {
            selectBibleField.setText(selected);
        }
        verseRandom = new CheckBox("Get Verse of day random / sequential (not checked)");
        Boolean verseRand = context.getAppSettings()
                .getDictConfig()
                .getVerseOfDayRandom();
        verseRandom.setSelected(verseRand);
        grid.add(verseRandom, 2, 0);
        selectBible.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                BibleRef selected = bibleRefs.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    selectBibleField.setText(selected.toString());
                    Optional<BibleTextUtils.BibleBookInstance> optInst =
                            BibleTextUtils.getInstance().getBibleInstances().stream()
                            .filter(e -> e.getBibleRef().equals(selected)).findFirst();
                    optInst.ifPresent(e -> optBible = Optional.of(e.getBible()));
                }
            }
        });

        this.setBottom(grid);
    }


    /**
     * returns the probably selected XML Bible for the verse of the day
     *
     * @return the bible refs
     */
    public Optional<XMLBIBLE> getSelectedBible() {
        return this.optBible;
    }

    public Boolean getVerseRandom() {
        return verseRandom.isSelected();
    }


}
