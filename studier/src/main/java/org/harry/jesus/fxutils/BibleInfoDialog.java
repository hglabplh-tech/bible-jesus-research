package org.harry.jesus.fxutils;

import generated.Dictionary;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.jesajautils.configjaxbser.DictionaryRef;

/**
 * The type Bible info dialog.
 */
public class BibleInfoDialog {

    /**
     * Call bible info dialog.
     *
     * @param instance the instance
     */
    public static void callBibleInfoDialog(BibleTextUtils.BibleBookInstance instance) {
        Dialog<Void> dialog = new Dialog<>();

        DialogPane pane = new DialogPane();
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().addAll(saveButtonType, ButtonType.OK);
        String bibleName = instance.getBibleRef().getBibleID();
        String version =instance.getBible().getVersion();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        Label nameLab = new Label("BibleName: " + bibleName);
        Label versionLab = new Label("Version: " + version);
        Label hashLab = new Label("SHA256-Hash : 0x" + instance.getBibleRef().getHashValue());
        grid.add(nameLab, 0, 0);
        grid.add(versionLab, 0, 1);
        grid.add(hashLab, 0, 2);
        if (instance.getOptDictAccRefTuple().isPresent()) {
            Tuple<Dictionary, DictionaryRef> acc = instance.getOptDictAccRefTuple().get();
            DictionaryRef ref = acc.getSecond();
            Label accPathLab = new Label("Accordance File Path: " + ref.getPathToBook());
            Label accHashLab = new Label("Accordance SHA256-Hash : 0x" + ref.getHashValue());
            grid.add(accPathLab, 0, 3);
            grid.add(accHashLab, 0, 4);
        }
        dialog.setDialogPane(pane);
        pane.setContent(grid);

        dialog.showAndWait();
    }
}
