package splitter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FinishController extends ExcelController{
    @FXML
    private CheckBox checkBox;
    private String directory;

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void handleFinishButton() {
        if (checkBox.isSelected()){
            try {
                Desktop.getDesktop().open(new File(directory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Platform.exit();
    }
}
