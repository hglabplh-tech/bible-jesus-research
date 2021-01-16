package org.harry.jesus.fxutils.controls;

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

public class DictBibleSettingsCompound extends BorderPane  {

    ListView<BibleRef> bibleRefs = new ListView<>();

    ListView<DictionaryRef> dictRefs= new ListView<>();

    final ObservableList<AssocData> dictRefList = FXCollections.observableArrayList();

    TableView<AssocData> assoc = new TableView<>();

    CheckBox selectBible = new CheckBox("auto-select bible when load assoc dictionary");

    CheckBox selectDictionary = new CheckBox("auto-load dictionary when selecting assoc bible");

    public DictBibleSettingsCompound() {
        TableColumn colDict = new TableColumn("Dictionaries");
        TableColumn colBibles = new TableColumn("Bibles");
        assoc.getColumns().add(colDict);
        assoc.getColumns().add(colBibles);
        colDict.setCellValueFactory(
                new PropertyValueFactory<AssocData, String>("dictionaryID"));
        colBibles.setCellValueFactory(
                new PropertyValueFactory<AssocData, String>("bibleID"));
        assoc.setItems(dictRefList);
        this.setCenter(assoc);
        this.setRight(bibleRefs);
        this.setLeft(dictRefs);
        BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();
        BiblesDictConfig dictConfig = context.getAppSettings()
                .getDictConfig();
        Map<DictionaryRef, BibleRef> refsAssocMap = dictConfig.getDictBibleMapping();
        for (Map.Entry<DictionaryRef, BibleRef> entry: refsAssocMap.entrySet()) {
            AssocData newAssoc = new AssocData(
                    entry.getKey().getDictionaryID(),
                    entry.getValue().getBibleID());
            assoc.getItems().add(newAssoc);
        }


        BibleTextUtils utils = BibleTextUtils.getInstance();
        for(BibleTextUtils.BibleBookInstance bible: utils.getBibleInstances()) {
            bibleRefs.getItems().add(bible.getBibleRef());
        }

        for(BibleTextUtils.DictionaryInstance dictInstance: utils.getDictInstances()) {
            dictRefs.getItems().add(dictInstance.getDictionaryRef());
        }
        GridPane grid = new GridPane();
        Button addToAssoc = new Button("Add selected to assoc");
        grid.add(addToAssoc, 0, 0);
        Button removeFromAssoc = new Button("Remove selected from assoc");
        grid.add(removeFromAssoc, 1, 0);

        grid.add(selectBible, 0, 1);
        grid.add(selectDictionary, 1, 1);
        selectDictionary.setSelected(dictConfig.getSelectDictionary());
        selectBible.setSelected(dictConfig.getSelectBible());
        this.setBottom(grid);
        addToAssoc.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DictionaryRef dictRef = dictRefs.getSelectionModel().getSelectedItem();
                if (dictRef == null) {
                    dictRef = dictRefs.getItems().get(0);
                }
                BibleRef bibleRef = bibleRefs.getSelectionModel().getSelectedItem();
                if (bibleRef == null) {
                    bibleRef = bibleRefs.getItems().get(0);
                }
                AssocData newAssoc = new AssocData(dictRef.getDictionaryID(), bibleRef.getBibleID());
                assoc.getItems().add(newAssoc);
                assoc.refresh();
            }
        });
        removeFromAssoc.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AssocData data = assoc.getSelectionModel().getSelectedItem();
                if (data != null) {
                    assoc.getItems().remove(data);
                    assoc.refresh();
                }
            }
        });

    }


    public ListView<BibleRef> getBibleRefs() {
        return this.bibleRefs;
    }

    public ListView<DictionaryRef> getDictRefs() {
        return this.dictRefs;
    }

    public ObservableList<AssocData> getAssocData() {
        return this.dictRefList;
    }

    public CheckBox getSelectBible() {
        return selectBible;
    }

    public CheckBox getSelectDictionary() {
        return selectDictionary;
    }

    public static class AssocData {
        private final SimpleStringProperty dictionaryID = new SimpleStringProperty("");
        private final SimpleStringProperty bibleID = new SimpleStringProperty("");

        public AssocData () {
            this("", "");
        }

        public AssocData (String dicionaryID, String bibleID) {
            setDictionaryID(dicionaryID);
            setBibleID(bibleID);

        }

        public String getDictionaryID() {
            return dictionaryID.get();
        }

        public void setDictionaryID(String dictID) {
            dictionaryID.set(dictID);
        }

        public String getBibleID() {
            return this.bibleID.get();
        }

        public void setBibleID(String bibleID) {
            this.bibleID.set(bibleID);
        }


    }

}
