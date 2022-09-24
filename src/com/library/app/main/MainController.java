package com.library.app.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.library.app.dashboard.DashboardController;
import com.library.app.database.DatabaseHandler;
import com.library.app.util.LibraryUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController implements Initializable {

    DatabaseHandler databaseHandler;
    Boolean isReadyForSubmission = false;
    
    @FXML
    private JFXTextField bookIdInput;
    @FXML
    private Label bookName;
    @FXML
    private Label bookAthor;
    @FXML
    private Label bookPublisher;
    @FXML
    private JFXTextField memberIdInput;
    @FXML
    private Label memberMobile;
    @FXML
    private Label memberEmail;
    @FXML
    private JFXButton issueButton;
    @FXML
    private HBox rs;
    @FXML
    private JFXButton addBook;
    @FXML
    private JFXButton viewBookTable;
    @FXML
    private JFXButton viewMemberTable;
    @FXML
    private JFXButton logout;
    @FXML
    private JFXButton addMember;
    @FXML
    private JFXTextField bookID;
    @FXML
    private ListView<String> issueDataList;
    @FXML
    private Label bookStatus;
    @FXML
    private Label memberName;
    @FXML
    private HBox memberInfo;
    @FXML
    private StackPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
    }    
    
    @FXML
    private void loadAddMember(ActionEvent event) {
        loadWindow("/com/library/app/addMember/addMember.fxml", "Add New Member");
    }

    @FXML
    private void loadAddBook(ActionEvent event) {
        loadWindow("/com/library/app/addbook/addBook.fxml", "Add New Book");
    }

    @FXML
    private void loadMemberTable(ActionEvent event) {
        loadWindow("/com/library/app/listMember/listMember.fxml", "List Member");
    }

    @FXML
    private void loadBookTable(ActionEvent event) {
        loadWindow("/com/library/app/listBook/listBook.fxml", "List Book");
    }
    
    @FXML
    private void loadLogout(ActionEvent event) {
        loadWindow("/com/library/app/login/userlogin.fxml", "Login");
        ((Stage)bookIdInput.getScene().getWindow()).close();
        
    }
    
    public void loadWindow(String loc, String title)
    {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            LibraryUtil.setStageIcon(stage);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @FXML
    private void loadBookInfo2(ActionEvent event) {
        
        String id = bookID.getText();
        if(id.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please enter Book Id");
            alert.showAndWait();
            return;
        }
        ObservableList<String> issueData = FXCollections.observableArrayList();
        String qu = "select * from issue where bookid ='"+ id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        isReadyForSubmission = false;
        try {
            while(rs.next())
            {
                String mBookId = id;
                String mMemeberId = rs.getString("memberid");
                Timestamp mIssueTime = rs.getTimestamp("issuetime");
                int mRenewCount = rs.getInt("renewcount");
                
                issueData.add("Issue Date and Time => " + mIssueTime.toGMTString());
                issueData.add("Renew Book => " + mRenewCount);
                
                String str = "select * from book where id= '"+ mBookId + "'";
                ResultSet rs1 = databaseHandler.execQuery(str);
                while(rs1.next())
                {
                    issueData.add("----------Book Information----------");
                    issueData.add("\t Book Id => " + rs1.getString("id"));
                    issueData.add("\t Book Name => " + rs1.getString("title"));
                    issueData.add("\t Author => " + rs1.getString("author"));
                    issueData.add("\t Publisher => " + rs1.getString("publisher"));
                    issueData.add("\t Code => " + rs1.getString("intcode"));
                }
                
                String str2 = "select * from member where id = '"+ mMemeberId +"'";
                rs1 = databaseHandler.execQuery(str2);
                while(rs1.next())
                {
                    issueData.add("----------Member Information----------");
                    issueData.add("\t Member Id => " + rs1.getString("id"));
                    issueData.add("\t Member Name => " + rs1.getString("name"));
                    issueData.add("\t Mobile => " + rs1.getString("mobile"));
                    issueData.add("\t Email => " + rs1.getString("email"));
                }
                isReadyForSubmission = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!isReadyForSubmission)
        {
            Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("Book Not Found !");
            alert1.showAndWait();
        }
        issueDataList.getItems().setAll(issueData);
    }

    @FXML
    private void loadRenew(ActionEvent event) {
        if(!isReadyForSubmission)
        {
            Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("Please select a book for submit");
            alert1.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to Renew the book ?");
        
        Optional<ButtonType> reponse = alert.showAndWait();
        if(reponse.get() == ButtonType.OK)
        {
            String qu = "update issue set issuetime = CURRENT_TIMESTAMP, renewcount = renewcount + 1 where bookid ='" + bookID.getText() + "'";
            if(databaseHandler.execAction(qu))
            {
                Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book has been Renewed");
                alert1.showAndWait();
            }
            else
            {
                Alert alert1 =  new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Renew Failed");
                alert1.showAndWait();
            }
        }
        else
        {
            Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled");
            alert1.setHeaderText(null);
            alert1.setContentText("Book Renew Cancelled");
            alert1.showAndWait();
        }
    }

    @FXML
    private void loadSubmission(ActionEvent event) {
        if(!isReadyForSubmission)
        {
            Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("Please select a book for submit");
            alert1.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to return the book ?");
        
        Optional<ButtonType> reponse = alert.showAndWait();
        if(reponse.get() == ButtonType.OK)
        {
            String id = bookID.getText();
            String qu = "delete from issue where bookid= '"+ id + "'";
            String qu1 = "update book set isAvail = true where id = '" + id + "'";
            if(databaseHandler.execAction(qu) && databaseHandler.execAction(qu1))
            {
                Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book has been Submitted");
                alert1.showAndWait();
            }
            else
            {
                Alert alert1 =  new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Submission Failed");
                alert1.showAndWait();
            }
        }
        else
        {
            Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled");
            alert1.setHeaderText(null);
            alert1.setContentText("Book Submission Cancelled");
            alert1.showAndWait();
        }
    }

    @FXML
    private void loadBookInfo(ActionEvent event) {
        clearBookCache();
        String id = bookIdInput.getText();
        String qu = "SELECT * From book where id='"+ id +"'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;
        try {
            while(rs.next())
            {
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                String bPublisher = rs.getString("publisher");
                Boolean bStatus = rs.getBoolean("isAvail");
                
                bookName.setText(bName);
                bookAthor.setText(bAuthor);
                bookPublisher.setText(bPublisher);
                bookStatus.setText((bStatus)?"Available" : "Not Available");
                flag = true;
            }
            if(!flag)
            {
                bookAthor.setText("No Such book Available");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void clearBookCache()
    {
        bookName.setText("");
        bookAthor.setText("");
        bookPublisher.setText("");
        bookStatus.setText("");
    }

    @FXML
    private void loadMemberInfo(ActionEvent event) {
        clearMemberCache();
        String mId = memberIdInput.getText();
        String qu = "SELECT * From member where id='"+ mId +"'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;
        try {
            while(rs.next())
            {
                String mName = rs.getString("name");
                String mMobile = rs.getString("mobile");
                String mEmail= rs.getString("email");
                
                memberName.setText(mName);
                memberMobile.setText(mMobile);
                memberEmail.setText(mEmail);
                flag = true;
            }
            if(!flag)
            {
                memberMobile.setText("No Such member Available");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void clearMemberCache()
    {
        memberName.setText("");
        memberMobile.setText("");
        memberEmail.setText("");
    }

    @FXML
    private void loadIssueOperation(ActionEvent event) {
        String bookId = bookIdInput.getText();
        String memberId = memberIdInput.getText();
        
        if(bookId.isEmpty() || memberId.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please select Book Id and Member Id");
            alert.showAndWait();
            return;
        }
        String str1 = "select * from book where id = '" + bookId + "'";
        String str2 = "select * from member where id = '" + memberId + "'";
        loadBookInfo(event);
        loadMemberInfo(event);
        if(databaseHandler.execQueryCount(str1) > 0 && databaseHandler.execQueryCount(str2) > 0)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure want to issue the book " + bookName.getText() +" \n to "+ memberName.getText()+ "?");

            Optional<ButtonType> reponse = alert.showAndWait();
            if(reponse.get() == ButtonType.OK)
            {
                String qu = "insert into issue (bookid , memberid) values("
                        + "'" + bookId + "',"
                        + "'" + memberId+ "'"
                        + ")";
                String qu1 = "update book set isAvail = false where id = '"+ bookId +"'";
                if(databaseHandler.execAction(qu) && databaseHandler.execAction(qu1))
                {
                    Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Success");
                    alert1.setHeaderText(null);
                    alert1.setContentText("Book issued Succesfully...");
                    alert1.showAndWait();
                }
                else
                {
                    Alert alert1 =  new Alert(Alert.AlertType.ERROR);
                    alert1.setTitle("Failed");
                    alert1.setHeaderText(null);
                    alert1.setContentText("Book issued Failed");
                    alert1.showAndWait();
                }
            }
            else
            {
                Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Cancelled");
                alert1.setHeaderText(null);
                alert1.setContentText("Book issued Cancelled");
                alert1.showAndWait();
            }
        }
        else
        {
            Alert alert1 =  new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("Book Id or Member Id not found...");
            alert1.showAndWait();
        }
    }

    @FXML
    private void loadClose(ActionEvent event) {
        ((Stage)rootPane.getScene().getWindow()).close();
    }

    @FXML
    private void loadFullScreen(ActionEvent event) {
        Stage stage = ((Stage)rootPane.getScene().getWindow());
        stage.setFullScreen(!stage.isFullScreen());
    }
}
