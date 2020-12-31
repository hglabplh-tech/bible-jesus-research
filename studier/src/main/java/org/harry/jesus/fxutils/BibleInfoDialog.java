package org.harry.jesus.fxutils;

import generated.XMLBIBLE;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class BibleInfoDialog {

    public static void callBibleInfoDialog(XMLBIBLE bible, String hash) {
        Dialog<Void> dialog = new Dialog<>();

        DialogPane pane = new DialogPane();
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().addAll(saveButtonType, ButtonType.OK);
        String bibleName = bible.getBiblename();
        String version = bible.getVersion();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        Label nameLab = new Label("BibleName: " + bibleName);
        Label versionLab = new Label("Version: " + version);
        Label hashLab = new Label("SHA256-Hash : 0x" + hash);
        grid.add(nameLab, 0, 0);
        grid.add(versionLab, 0, 1);
        grid.add(hashLab, 0, 2);
        dialog.setDialogPane(pane);
        pane.setContent(grid);

        dialog.showAndWait();
    }
}
