package com.library.app.listMember;

import com.library.app.addMember.AddMemberController;
import com.library.app.dashboard.DashboardController;
import com.library.app.database.DatabaseHandler;
import com.library.app.util.LibraryUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ListMemberController implements Initializable {

    ObservableList<Member> list = FXCollections.observableArrayList();
    DatabaseHandler databaseHandler;
    
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Member> tableView;
    @FXML
    private TableColumn<Member, String> idCol;
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, String> mobileCol;
    @FXML
    private TableColumn<Member, String> emailCol;
    @FXML
    private TableColumn<Member, Button> details;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        initCol();
        loadData();
    }
    
    private void initCol() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        details.setCellValueFactory(new PropertyValueFactory<>("details"));
    }

    private void loadData() {
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "select * from member";
        ResultSet rs = handler.execQuery(qu);
        try {
            while(rs.next())
            {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String id = rs.getString("id");
                String mobile = rs.getString("mobile");
                
                list.add(new Member(name, id, mobile, email));
            }
        } catch (Exception e) {
//            Logger.getLogger(AddBookLayoutController.class.getName()).log(Level.SEVERE, null,e);
        }
        
        tableView.getItems().setAll(list);
    }

    @FXML
    private void handleRefreshOption(ActionEvent event) {
        list.clear();
        loadData();
    }

    @FXML
    private void handleEditOption(ActionEvent event) {
        Member selectedMember = tableView.getSelectionModel().getSelectedItem();
        if(selectedMember == null){
            Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("No book selected");
            alert1.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/app/addMember/addMember.fxml"));
            Parent parent = loader.load();
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(parent));
            AddMemberController controller = (AddMemberController) loader.getController();
            controller.inflateUI(selectedMember);
            LibraryUtil.setStageIcon(stage);
            stage.show();
            stage.setOnCloseRequest((e -> {
                handleRefreshOption(event);
            }));
        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleDeleteOption(ActionEvent event) {
        Member selectedMember = tableView.getSelectionModel().getSelectedItem();
        if(selectedMember == null){
            Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("No book selected");
            alert1.showAndWait();
            return;
        }
        
        if(databaseHandler.isMemberHaveBook(selectedMember))
        {
            Alert alert1 =  new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("Book Still have not returned");
            alert1.showAndWait();
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to delete the book " + selectedMember.getName()+ "?");

        Optional<ButtonType> reponse = alert.showAndWait();
        if(reponse.get() == ButtonType.OK)
        {
            if(databaseHandler.deleteMember(selectedMember)){
                Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Member delected Successfully...");
                list.remove(selectedMember);
                alert1.showAndWait();
            }
            else
            {
                Alert alert1 =  new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Member delection Failed");
                alert1.showAndWait();
            }
        }
        else
        {
            Alert alert1 =  new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("Member delection cancelled...");
            alert1.showAndWait();
        }
        handleRefreshOption(event);
    }
    
    
    public static class Member
    {
        private final SimpleStringProperty name;
        private final SimpleStringProperty id;
        private final SimpleStringProperty mobile;
        private final SimpleStringProperty email;
        
        public Member(String name, String id, String mobile, String email)
        {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty(id);
            this.mobile = new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);
        }
        
        public String getName()
        {
            return name.get();
        }
        public String getId()
        {
            return id.get();
        }
        public String getMobile()
        {
            return mobile.get();
        }
        public String getEmail()
        {
            return email.get();
        }
    }
    
}
