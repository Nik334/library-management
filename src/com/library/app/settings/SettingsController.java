package com.library.app.settings;

import com.library.app.database.DatabaseHandler;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SettingsController implements Initializable {

    DatabaseHandler databaseHandler;
    
    @FXML
    private TextField nDaysWithoutFine;
    @FXML
    private TextField finePerDay;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private AnchorPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        initDefaultValue();
    }    

    @FXML
    private void saveButton(ActionEvent event) {
        int nDays = Integer.parseInt(nDaysWithoutFine.getText());
        float fine = Float.parseFloat(finePerDay.getText());
        String uname = username.getText();
        String pws = password.getText();
        
        if(nDaysWithoutFine.getText().isEmpty() || finePerDay.getText().isEmpty() || uname.isEmpty() || pws.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter in all field");
            alert.showAndWait();
            return;
        }
        Preferences preferences = new Preferences();
        preferences.setnDaysWithoutFine(nDays);
        preferences.setfinePerDay(fine);
        Preferences.writePrefrencesToFile(preferences);
        
//        smt.execute("create table "+tableName +"(\n" +
//                        "uname varchar(100) primary key,\n" +
//                        "pword varchar(200)\n" +
//                        ")");
        
        String qu = "insert into librarian_login values("
                + "'" + uname + ",'"
                + "'" + pws + "'"
                + ")";
        
        if(databaseHandler.execAction(qu) && Preferences.writePrefrencesToFile(preferences))
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Settings Updated...");
            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Can't Update configration FILE");
            alert.showAndWait();
        }
    }

    @FXML
    private void cancelButton(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void initDefaultValue() {
        Preferences preferences = Preferences.getPreferences();
        nDaysWithoutFine.setText(String.valueOf(preferences.getnDaysWithoutFine()));
        finePerDay.setText(String.valueOf(preferences.getfinePerDay()));
        String qu = "select * from librarian_login";
        username.setText("");
        password.setText("");
    }
}
