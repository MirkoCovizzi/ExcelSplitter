package splitter;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdvancedSplitController extends ExcelController implements Initializable{
    @FXML
    private ChoiceBox directoryBox;
    @FXML
    private ChoiceBox subdirectoryBox;
    @FXML
    private ChoiceBox columnBox;
    @FXML
    private Button forwardButton;

    private List<IndexedString> indexedStringList = new ArrayList<>();

    @Override
    public void setSpreadsheet(Spreadsheet spreadsheet) {
        super.setSpreadsheet(spreadsheet);
        IndexedString indexedString;

        for(int i = 0; i < spreadsheet.getColumns().size(); i++){
            indexedString = new IndexedString(i, spreadsheet.getColumns().get(i));
            indexedStringList.add(i, indexedString);
        }
        resetChoiceBox(directoryBox);
        resetChoiceBox(subdirectoryBox);
        resetChoiceBox(columnBox);
    }

    @Override
    public void transition(Event event, String fxml){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        try {
            Parent root = fxmlLoader.load();
            if (fxml.equals("../fxml/export.fxml")) {
                ExportController controller = fxmlLoader.getController();
                controller.setSpreadsheet(this.getSpreadsheet());
                controller.setIndexedString((IndexedString) columnBox.getValue());
                controller.setDirectory((IndexedString) directoryBox.getValue());
                if (subdirectoryBox.getValue() == null){
                    controller.setSubdirectory(new IndexedString(-1, ""));
                } else {
                    controller.setSubdirectory((IndexedString) subdirectoryBox.getValue());
                }
                controller.setPreviousFxml("../fxml/advanced_split.fxml");
            } else if (fxml.equals("../fxml/mode.fxml")) {
                ModeController controller = fxmlLoader.getController();
                controller.setSpreadsheet(this.getSpreadsheet());
            }
            Stage st_old =(Stage) ((Node)event.getSource()).getScene().getWindow();
            Stage st_new = new Stage();
            st_new.setResizable(false);
            st_new.getIcons().add(new Image(Main.class.getResourceAsStream("../res/excel-splitter-small.png")));
            st_new.setTitle("Excel Splitter");
            Scene scene = new Scene(root);
            scene.getStylesheets().add("css/theme.css");
            st_new.setScene(scene);
            st_new.setX(st_old.getX());
            st_new.setY(st_old.getY());
            st_old.close();
            st_new.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.forwardButton.setDisable(true);
        this.setPreviousFxml("../fxml/mode.fxml");
        this.setNextFxml("../fxml/export.fxml");
        directoryBox.valueProperty().addListener((ov, t, t1) -> {
            if (t != null) {
                resetChoiceBox(subdirectoryBox);
                resetChoiceBox(columnBox);
            }
            subdirectoryBox.getItems().remove(t1);
            columnBox.getItems().remove(t1);
            checkForwardButton();
        });
        subdirectoryBox.valueProperty().addListener((ov, t, t1) -> {
            if (t != null) {
                resetChoiceBox(columnBox);
            }
            columnBox.getItems().remove(t1);
            checkForwardButton();
        });
    }

    private void resetChoiceBox(ChoiceBox comboBox){
        for (int i = 0; i < comboBox.getItems().size(); i++){
            comboBox.getItems().remove(i);
        }
        for(int i = 0; i < indexedStringList.size(); i++){
            comboBox.getItems().add(i, indexedStringList.get(i));
        }
        if (comboBox.equals(subdirectoryBox)){
            comboBox.getItems().add(0, null);
        }
    }

    public void handleDirectoryBox(ActionEvent actionEvent) {
        checkForwardButton();
    }

    public void handleSubdirectoryBox(ActionEvent actionEvent) {
        checkForwardButton();
    }

    public void handleColumnBox(ActionEvent actionEvent) {
        checkForwardButton();
    }

    private void checkForwardButton(){
        if((!directoryBox.getSelectionModel().isEmpty() && !subdirectoryBox.getSelectionModel().isEmpty() && !columnBox.getSelectionModel().isEmpty()) || (!directoryBox.getSelectionModel().isEmpty() && !columnBox.getSelectionModel().isEmpty())) this.forwardButton.setDisable(false);
        else this.forwardButton.setDisable(true);
    }
}
