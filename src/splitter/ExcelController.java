package splitter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.*;
import java.io.IOException;

public class ExcelController {
    private Spreadsheet spreadsheet;
    private String previousFxml;
    private String nextFxml;

    public void setSpreadsheet(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
    }

    public Spreadsheet getSpreadsheet() {
        return spreadsheet;
    }

    public String getPreviousFxml() {
        return previousFxml;
    }

    public void setPreviousFxml(String previousFxml) {
        this.previousFxml = previousFxml;
    }

    public String getNextFxml() {
        return nextFxml;
    }

    public void setNextFxml(String nextFxml) {
        this.nextFxml = nextFxml;
    }

    public void transition(Event event, String fxml){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        try {
            Parent root = fxmlLoader.load();
            ExcelController controller = fxmlLoader.getController();
            controller.setSpreadsheet(this.getSpreadsheet());
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

    public void handleBackButton(ActionEvent actionEvent) {
        transition(actionEvent, this.getPreviousFxml());
    }

    public void handleForwardButton(ActionEvent actionEvent) {
        transition(actionEvent, this.getNextFxml());
    }

    public void handleCancelButton() {
        Platform.exit();
    }

    public void errorDialog(Window initOwner, String string){
        final Stage dialog = new Stage();
        dialog.setTitle("Attenzione!");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/dialog.fxml"));
        try {
            Parent root = fxmlLoader.load();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(initOwner);
            dialog.getIcons().add(new Image(Main.class.getResourceAsStream("../res/excel-splitter-small.png")));
            dialog.setResizable(false);
            Scene dialogScene = new Scene(root);
            dialogScene.getStylesheets().add("css/theme.css");
            dialog.setScene(dialogScene);
            Label label = (Label)root.lookup("#label");
            label.setText(string);
            Button button = (Button)root.lookup("#button");
            button.setOnAction(event -> dialog.close());
            dialog.show();
            Toolkit.getDefaultToolkit().beep();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
