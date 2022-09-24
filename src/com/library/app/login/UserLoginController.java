package com.library.app.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.library.app.dashboard.DashboardController;
import com.library.app.database.DatabaseHandler;
import com.library.app.util.LibraryUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.codec.digest.DigestUtils;

public class UserLoginController implements Initializable {

    DatabaseHandler databaseHandler;
    
    @FXML
    private JFXTextField userNameLibrarian;
    @FXML
    private JFXTextField passwordLibrarian;
    @FXML
    private JFXButton login;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void librarainLogin(ActionEvent event) {
        if(userNameLibrarian.getText().isEmpty() || passwordLibrarian.getText().isEmpty())
        {
            Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("Please enter Username and password");
            alert1.showAndWait();
            return;
        }
        
        String exUname = null , expws = null;
        String uname = userNameLibrarian.getText();
        String pws = DigestUtils.shaHex(passwordLibrarian.getText());
        String qu = "select * from librarian_login where uname = '"+ uname +"' && pword = '" + pws +"'";
        Boolean flag = false;
        
        ResultSet rs = databaseHandler.execQuery(qu);
        try {
            while(rs.next())
            {
                exUname = rs.getString("uname");
                expws = rs.getString("pword");
            }
            
            if(uname.equals(exUname) && pws.equals(expws)) {
                flag = true;
            } else flag = false;
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(flag)
        {
            closeStage();
            loadWindow();
        }
        else
        {
            Alert alert1 =  new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("Login failed...");
            alert1.showAndWait();
        }
    }
    
    private void closeStage() {
        ((Stage)userNameLibrarian.getScene().getWindow()).close();
    }

    void loadWindow()
    {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/com/library/app/main/Main.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Librarian Dashboard");
            stage.setScene(new Scene(parent));
            LibraryUtil.setStageIcon(stage);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
