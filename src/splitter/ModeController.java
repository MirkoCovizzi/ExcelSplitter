package splitter;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ModeController extends ExcelController implements Initializable{

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setPreviousFxml("/fxml/load.fxml");
    }

    public void handleSimpleButton(ActionEvent actionEvent) {
        transition(actionEvent, "/fxml/split.fxml");
    }

    public void handleAdvancedButton(ActionEvent actionEvent) {
        transition(actionEvent, "/fxml/advanced_split.fxml");
    }

    public void handleHelpButton(ActionEvent actionEvent) {
        final Stage dialog = new Stage();
        dialog.setTitle("Info");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/help.fxml"));
        try {
            Parent root = fxmlLoader.load();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(((Node)actionEvent.getSource()).getScene().getWindow());
            dialog.getIcons().add(new Image(Main.class.getResourceAsStream("/res/excel-splitter-small.png")));
            dialog.setResizable(false);
            Scene dialogScene = new Scene(root);
            dialogScene.getStylesheets().add("/css/theme.css");
            dialog.setScene(dialogScene);
            Label label = (Label)root.lookup("#label");
            label.setText("Modalità Semplice: Effettua uno split su di una colonna, senza creazione di cartelle o sottocartelle.\n" +
                    "Modalità Avanzata: Effettua uno split su più colonne, creando cartelle e sottocartelle.");
            dialog.show();
            Toolkit.getDefaultToolkit().beep();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
