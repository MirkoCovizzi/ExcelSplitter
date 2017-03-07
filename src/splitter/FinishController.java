package splitter;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FinishController extends ExcelController implements Initializable{
    @FXML
    private CheckBox checkBox;
    private String directory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!Desktop.isDesktopSupported()){
            checkBox.setDisable(true);
        }
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void handleFinishButton(ActionEvent event) {
        if (Desktop.isDesktopSupported()){
            if (checkBox.isSelected()){
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws IOException {
                        Desktop.getDesktop().open(new File(directory));
                        return null;
                    }
                };
                task.exceptionProperty().addListener((observable, oldValue, newValue) ->  {
                    if(newValue != null) {
                        errorDialog(((Node)event.getSource()).getScene().getWindow(), "Impossibile aprire la cartella!");
                    }
                });
                new Thread(task).start();
            }
        }
        Platform.exit();
    }

}