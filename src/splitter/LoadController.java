package splitter;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoadController extends ExcelController {
    @FXML
    private Button openButton;
    @FXML
    private ProgressIndicator progressIndicator;

    public void handleOpenButtonClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Window stage = source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("File Excel (*.xls, *.xlsx)", "*.xls", "*.xlsx");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            openFileTask(file, actionEvent);
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
            openFileTask(file, event);
            success = true;
        }
        event.setDropCompleted(success);

        event.consume();
    }

    private void openFileTask(File file, Event event){
        Task<Spreadsheet> task = new Task<Spreadsheet>() {
            @Override
            protected Spreadsheet call() throws Exception {
                openButton.setDisable(true);
                Spreadsheet spreadsheet = new Spreadsheet(file);
                return spreadsheet;
            }
        };
        task.setOnSucceeded(e -> {
            this.setSpreadsheet(task.getValue());
            transition(event, "/fxml/mode.fxml");
        });
        task.exceptionProperty().addListener((observable, oldValue, newValue) ->  {
            if(newValue != null) {
                openButton.setDisable(false);
                errorDialog(((Node)event.getSource()).getScene().getWindow(), "Il file deve essere un documento Excel (.xls oppure .xlsx) valido e ben formato!");
                errorDialog(((Node)event.getSource()).getScene().getWindow(), task.exceptionProperty().toString());
                System.out.println(task.exceptionProperty().toString());
            }
        });
        progressIndicator.visibleProperty().bind(task.runningProperty());
        new Thread(task).start();
    }

}
