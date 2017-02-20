package splitter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ExcelController {
    private Spreadsheet spreadsheet;
    private String previousFxml;
    private String nextFxml;

    public Spreadsheet getSpreadsheet() {
        return spreadsheet;
    }

    public void setSpreadsheet(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
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
            st_new.getIcons().add(new Image(Main.class.getResourceAsStream("excel-splitter-small.png")));
            st_new.setTitle("Excel Splitter");
            Scene scene = new Scene(root);
            scene.getStylesheets().add("theme.css");
            st_new.setScene(scene);
            st_old.close();
            st_new.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void handleBackButton(ActionEvent actionEvent) {
        transition(actionEvent, this.getPreviousFxml());
    }

    public void handleForwardButton(ActionEvent actionEvent) {
        transition(actionEvent, this.getNextFxml());
    }

    public void handleCancelButton() {
        Platform.exit();
    }
}
