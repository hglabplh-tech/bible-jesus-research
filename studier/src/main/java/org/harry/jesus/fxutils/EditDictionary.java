package org.harry.jesus.fxutils;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.harry.jesus.BibleStudy;
import org.harry.jesus.fxutils.controls.EditDictCompoundControl;
import org.harry.jesus.fxutils.controls.ShowPictureControl;

/**
 * The type Edit dictionary.
 */
public class EditDictionary {

    /**
     * Show edit dictionary.
     *
     * @param nodeFromPrimary the node from primary
     */
    public static void showEditDictionary(Node nodeFromPrimary) {
        Node node = BibleStudy.windowsMap.get("editDict");
        if (node != null) {
            node.getScene().getWindow().requestFocus();
            return;
        }
        Stage stage = new Stage();
        stage.setTitle("Edit Dictionary");
        VBox topWin = new VBox();
        topWin.getChildren().add(new EditDictCompoundControl(nodeFromPrimary));
        Scene secondScene = new Scene(topWin);
        stage.setScene(secondScene);
        BibleStudy.windowsMap.put("editDict", topWin);
        stage.setOnCloseRequest(event -> {
            System.out.println("Stage is closing");
            BibleStudy.windowsMap.remove("editDict");
        });
        stage.show();
    }
}
