package splitter;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.stage.Window;

import java.awt.*;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessControlException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ExportController extends ExcelController implements Initializable{

    @FXML
    private Button backButton;
    @FXML
    private Button exportButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button chooseDirectoryButton;
    @FXML
    private Label directoryLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private CheckBox checkBox;
    private IndexedString indexedString;
    private String dir;
    private IndexedString directory;
    private IndexedString subdirectory;

    public void handleChooseDirectory(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Window stage = source.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if(selectedDirectory == null){
            directoryLabel.setText(this.getSpreadsheet().getDirectory());
        }else{
            directoryLabel.setText(selectedDirectory.getAbsolutePath());
            dir = selectedDirectory.getAbsolutePath();
        }
    }

    public void handleExportButton(ActionEvent actionEvent) {
        //Checking if I can write in the chosen directory
        File f = new File(dir);
        Path p = f.toPath();
        if (Files.isWritable(p)) {
            this.progressBar.setVisible(true);
            checkBox.setDisable(true);
            backButton.setDisable(true);
            exportButton.setDisable(true);
            cancelButton.setDisable(true);
            chooseDirectoryButton.setDisable(true);
            List<Spreadsheet> spreadsheetList = null;
            String path = dir;
            if (checkBox.isSelected()){
                path += File.separator + "Excel Splitter";
            }
            if (this.getPreviousFxml().equals("advanced_split.fxml")){
                spreadsheetList  = Spreadsheet.split(this.getSpreadsheet(), directory.getIndex(), subdirectory.getIndex(), indexedString.getIndex());
            } else if (this.getPreviousFxml().equals("split.fxml")) {
                spreadsheetList = Spreadsheet.split(this.getSpreadsheet(), -1, -1, indexedString.getIndex());
            }
            List<Spreadsheet> finalSpreadsheetList = spreadsheetList;
            String finalPath = path;
            Task<Void> task = new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    File f;
                    Stage stage =(Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

                    //prevent from closing during file export
                    Platform.setImplicitExit(false);
                    stage.setOnCloseRequest(event -> event.consume());

                    for (int i = 0; i < finalSpreadsheetList.size(); i++) {
                        String dirString = finalSpreadsheetList.get(i).getDirectory();
                        dirString = formatName(dirString);

                        String subdirString = finalSpreadsheetList.get(i).getSubdirectory();
                        subdirString = formatName(subdirString);

                        f = new File(finalPath + File.separator + dirString + File.separator + subdirString);
                        if (!(f.exists() && f.isDirectory())){
                            f.mkdirs();
                        }

                        String name = finalSpreadsheetList.get(i).getName();
                        name = formatName(name);

                        String fullFilePath = finalPath + File.separator + dirString + File.separator + subdirString + File.separator + name;

                        recursiveCheckedExport(finalSpreadsheetList.get(i), fullFilePath, 0);

                        updateProgress(i + 1, finalSpreadsheetList.size());
                        //Thread.sleep(100);
                    }

                    return null;
                }

            };
            task.setOnSucceeded(e -> {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("finish.fxml"));
                try {
                    Parent root = fxmlLoader.load();
                    FinishController controller = fxmlLoader.getController();
                    controller.setDirectory(finalPath);
                    Stage st_old =(Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
                    Stage st_new = new Stage();
                    st_new.setResizable(false);
                    st_new.getIcons().add(new Image(Main.class.getResourceAsStream("excel-splitter-small.png")));
                    st_new.setTitle("Excel Splitter");
                    st_old.close();
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add("theme.css");
                    st_new.setScene(scene);
                    st_new.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            progressBar.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
        } else {
            final Stage dialog = new Stage();
            dialog.setTitle("Attenzione!");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dialog.fxml"));
            try {
                Parent root = fxmlLoader.load();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner((Stage) ((Node)actionEvent.getSource()).getScene().getWindow());
                dialog.getIcons().add(new Image(Main.class.getResourceAsStream("excel-splitter-small.png")));
                dialog.setResizable(false);
                Scene dialogScene = new Scene(root);
                dialogScene.getStylesheets().add("theme.css");
                dialog.setScene(dialogScene);
                Label label = (Label)root.lookup("#label");
                label.setText("Non ho i permessi per esportare in questa cartella!");
                dialog.show();
                Toolkit.getDefaultToolkit().beep();
            } catch (IOException ioE) {
                ioE.printStackTrace();
            }
        }
    }

    private String formatName(String name){
        name = name.replaceAll(" ", "_");
        name = name.replaceAll("/", "_");
        name = name.replaceAll(",", "");
        name = name.replaceAll(":", "");
        name = name.replaceAll("<", "_");
        name = name.replaceAll(">", "_");
        name = name.replaceAll("\"", "");
        name = name.replaceAll("\\\\", "");
        name = name.replaceAll("|", "");
        name = name.replaceAll("\\?", "");
        name = name.replaceAll("\\*", "");
        if(name.length() > 128){
            name = name.substring(0, 128);
            name += "~";
        }
        return name;
    }

    private void recursiveCheckedExport(Spreadsheet s, String path, int count){
        Path p;
        if (count == 0) {
            p = Paths.get(path + ".xls");
        } else {
            p = Paths.get(path + count + ".xls");
        }
        boolean exists = Files.exists(p);
        boolean notExists = Files.notExists(p);

        if (exists) {
            recursiveCheckedExport(s, path, ++count);
        } else if (notExists) {
            if (count == 0){
                s.export(path);
            } else {
                s.export(path + count);
            }
        } else {
            System.out.println("File's status is unknown!");
        }
    }

    @Override
    public void setSpreadsheet(Spreadsheet spreadsheet) {
        super.setSpreadsheet(spreadsheet);
        this.dir = spreadsheet.getDirectory();
        directoryLabel.setText(dir);
    }

    public void setIndexedString(IndexedString indexedString){
        this.indexedString = indexedString;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setNextFxml("finish.fxml");
        this.progressBar.setVisible(false);
    }

    public IndexedString getDirectory() {
        return directory;
    }

    public void setDirectory(IndexedString directory) {
        this.directory = directory;
    }

    public IndexedString getSubdirectory() {
        return subdirectory;
    }

    public void setSubdirectory(IndexedString subdirectory) {
        this.subdirectory = subdirectory;
    }
}
