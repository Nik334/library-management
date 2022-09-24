package com.library.app.addMember;

import com.library.app.database.DatabaseHandler;
import com.library.app.listMember.ListMemberController;
import com.library.app.listMember.ListMemberController.Member;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddMemberController implements Initializable {
    
    DatabaseHandler databaseHandler;
    Boolean isEditable = Boolean.FALSE;
    
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField name;
    @FXML
    private TextField id;
    @FXML
    private TextField mobile;
    @FXML
    private TextField email;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void addMember(ActionEvent event) {
        String mName = name.getText();
        String mId = id.getText();
        String mMobile = mobile.getText();
        String mEmail = email.getText();
        if(mName.isEmpty() || mId.isEmpty() || mMobile.isEmpty() || mEmail.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter in all field");
            alert.showAndWait();
            return;
        }
        
        if(isEditable)
        {
            updateMember();
            return;
        }
        
//         smt.execute("create table "+tableName +"(\n" +
//                        "id varchar(100) primary key,\n" +
//                        "name varchar(200),\n" +
//                        "mobile varchar(200),\n" +
//                        "email varchar(200)\n" +
//                        ")");
        String qu = "INSERT INTO member values ("
                + "'"+ mId+"',"
                + "'"+ mName+"',"
                + "'"+ mMobile+"',"
                + "'"+ mEmail+"'"
                + ")";
        
        System.out.println(qu);
        if(databaseHandler.execAction(qu))
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Successfully Added");
            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed");
            alert.showAndWait();
        }
        clearField();
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    public void inflateUI(ListMemberController.Member member) {
        name.setText(member.getName());
        id.setText(member.getId());
        mobile.setText(member.getMobile());
        email.setText(member.getEmail());
        id.setEditable(false);
        isEditable = Boolean.TRUE;
    }

    private void updateMember() {
        Member m = new Member(name.getText(),id.getText(),mobile.getText(),email.getText());
        if(databaseHandler.updateMember(m))
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Member successfully Updated");
            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Can't Update Memebr");
            alert.showAndWait();
        }
    }

    private void clearField() {
        
        name.setText("");
        id.setText("");
        mobile.setText("");
        email.setText("");
    }

    
}
