package org.harry.jesus.fxutils;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.harry.jesus.fxutils.controls.EditDictCompoundControl;
import org.harry.jesus.fxutils.controls.ShowPictureControl;

public class EditDictionary {

    public static void showEditDictionary(Node nodeFromPrimary) {
        Stage stage = new Stage();
        stage.setTitle("Edit Dictionary");
        VBox topWin = new VBox();
        topWin.getChildren().add(new EditDictCompoundControl(nodeFromPrimary));
        Scene secondScene = new Scene(topWin);
        stage.setScene(secondScene);
        stage.show();
    }
}
