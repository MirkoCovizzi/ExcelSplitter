package splitter;

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
    public void initialize(URL location, ResourceBundle resources) {
        this.forwardButton.setDisable(true);
        this.setPreviousFxml("../fxml/mode.fxml");
        this.setNextFxml("../fxml/export.fxml");
    }

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

        directoryBox.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue.equals(subdirectoryBox.getValue())){
                subdirectoryBox.getSelectionModel().selectFirst();
            }
            if (newValue.equals(columnBox.getValue())){
                columnBox.getSelectionModel().selectFirst();
            }
            subdirectoryBox.getItems().remove(newValue);
            columnBox.getItems().remove(newValue);
            if (!((IndexedString)oldValue).getString().equals("")){
                subdirectoryBox.getItems().add(oldValue);
                subdirectoryBox.getItems().sort(new IndexedStringComparator());
                columnBox.getItems().add(oldValue);
                columnBox.getItems().sort(new IndexedStringComparator());
            }
            checkForwardButton();
        });
        subdirectoryBox.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue.equals(directoryBox.getValue())){
                directoryBox.getSelectionModel().selectFirst();
            }
            if (newValue.equals(columnBox.getValue())){
                columnBox.getSelectionModel().selectFirst();
            }
            directoryBox.getItems().remove(newValue);
            columnBox.getItems().remove(newValue);
            if (!((IndexedString)oldValue).getString().equals("")){
                directoryBox.getItems().add(oldValue);
                directoryBox.getItems().sort(new IndexedStringComparator());
                columnBox.getItems().add(oldValue);
                columnBox.getItems().sort(new IndexedStringComparator());
            }
            checkForwardButton();
        });
        columnBox.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue.equals(directoryBox.getValue())){
                directoryBox.getSelectionModel().selectFirst();
            }
            if (newValue.equals(subdirectoryBox.getValue())){
                subdirectoryBox.getSelectionModel().selectFirst();
            }
            directoryBox.getItems().remove(newValue);
            subdirectoryBox.getItems().remove(newValue);
            if (!((IndexedString)oldValue).getString().equals("")){
                directoryBox.getItems().add(oldValue);
                directoryBox.getItems().sort(new IndexedStringComparator());
                subdirectoryBox.getItems().add(oldValue);
                subdirectoryBox.getItems().sort(new IndexedStringComparator());
            }
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
        comboBox.getItems().add(0, new IndexedString(-1, ""));
        comboBox.getSelectionModel().selectFirst();
    }

    public void handleDirectoryBox() {
        checkForwardButton();
    }

    public void handleSubdirectoryBox() {
        checkForwardButton();
    }

    public void handleColumnBox() {
        checkForwardButton();
    }

    private void checkForwardButton(){
        if((!((IndexedString)directoryBox.getValue()).getString().equals("") && !((IndexedString)subdirectoryBox.getValue()).getString().equals("") && !((IndexedString)columnBox.getValue()).getString().equals("") || (!((IndexedString)directoryBox.getValue()).getString().equals("") && !((IndexedString)columnBox.getValue()).getString().equals("")))) this.forwardButton.setDisable(false);
        else this.forwardButton.setDisable(true);
    }

    @Override
    public void transition(Event event, String fxml){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        try {
            Parent root = fxmlLoader.load();
            if (fxml.equals("../fxml/export.fxml")) {
                ExportController controller = fxmlLoader.getController();
                controller.setSpreadsheet(super.getSpreadsheet());
                controller.setIndexedString((IndexedString) columnBox.getValue());
                controller.setDirectory((IndexedString) directoryBox.getValue());
                controller.setSubdirectory((IndexedString) subdirectoryBox.getValue());
                controller.setPreviousFxml("../fxml/advanced_split.fxml");
            } else if (fxml.equals("../fxml/mode.fxml")) {
                ModeController controller = fxmlLoader.getController();
                controller.setSpreadsheet(super.getSpreadsheet());
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
}
