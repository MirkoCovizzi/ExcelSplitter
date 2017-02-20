package splitter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadController extends ExcelController {

    public void handleOpenButtonClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Window stage = source.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("File Excel (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                this.setSpreadsheet(new Spreadsheet(file));
                transition(actionEvent, "../fxml/mode.fxml");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("The file is not a proper .xls file.");
                e.printStackTrace();
            }
        }
    }

    public void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }

        event.consume();
    }

    public void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        List<File> files = (ArrayList<File>) db.getContent(DataFormat.FILES);

        boolean success = false;
        if (files != null) {
            File file = files.get(0);
            try {
                this.setSpreadsheet(new Spreadsheet(file));
                Timeline timeline = new Timeline();
                timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1),
                        event_t -> {
                            transition(event, "../fxml/mode.fxml");
                        }));
                timeline.play();
            } catch (IOException e) {
                System.out.println("The file is not a proper .xls file.");
                final Stage dialog = new Stage();
                dialog.setTitle("Attenzione!");
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/dialog.fxml"));
                try {
                    Parent root = fxmlLoader.load();
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.initOwner((Stage) ((Node)event.getSource()).getScene().getWindow());
                    dialog.getIcons().add(new Image(Main.class.getResourceAsStream("../res/excel-splitter-small.png")));
                    dialog.setResizable(false);
                    Scene dialogScene = new Scene(root);
                    dialogScene.getStylesheets().add("res/theme.css");
                    dialog.setScene(dialogScene);
                    Label label = (Label)root.lookup("#label");
                    label.setText("Il file deve essere un documento Excel (.xls) valido!");
                    dialog.show();
                    Toolkit.getDefaultToolkit().beep();
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            }
            success = true;
        }
        event.setDropCompleted(success);

        event.consume();
    }

}
