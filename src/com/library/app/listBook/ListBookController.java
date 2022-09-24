package com.library.app.listBook;

import com.library.app.addbook.AddBookController;
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
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ListBookController implements Initializable {

    ObservableList<Book> list = FXCollections.observableArrayList();
    DatabaseHandler databaseHandler;
    
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> idCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> publisherCol;
    @FXML
    private TableColumn<Book, String> codeCol;
    @FXML
    private TableColumn<Book, Boolean> availbilityCol;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        initCol();
        loadData();
    }    

    private void initCol() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        availbilityCol.setCellValueFactory(new PropertyValueFactory<>("avalibility"));
    }

    private void loadData() {
        clearTable();
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "select * from book";
        ResultSet rs = handler.execQuery(qu);
        try {
            while(rs.next())
            {
                String title = rs.getString("title");
                String author = rs.getString("author");
                String id = rs.getString("id");
                String publisher = rs.getString("publisher");
                String code = rs.getString("intcode");
                Boolean avali = rs.getBoolean("isAvail");
                
                list.add(new Book(title, id, author, publisher, code, avali));
            }
        } catch (Exception e) {
//            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null,e);
        }
        tableView.setItems(list);
    }

    @FXML
    private void handleDeleteOption(ActionEvent event) {
        Book selectedBook = tableView.getSelectionModel().getSelectedItem();
        if(selectedBook == null){
            Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("No book selected");
            alert1.showAndWait();
            return;
        }
        
        if(databaseHandler.isBookAlreadyIssued(selectedBook))
        {
            Alert alert1 =  new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("This book is already issued and cannot be deleted");
            alert1.showAndWait();
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to delete the book " + selectedBook.getTitle() + "?");

        Optional<ButtonType> reponse = alert.showAndWait();
        if(reponse.get() == ButtonType.OK)
        {
            if(databaseHandler.deleteBook(selectedBook)){
                Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book delected Successfully...");
                list.remove(selectedBook);
                alert1.showAndWait();
            }
            else
            {
                Alert alert1 =  new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Book delection Failed");
                alert1.showAndWait();
            }
        }
        else
        {
            Alert alert1 =  new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("Book delection cancelled...");
            alert1.showAndWait();
        }
    }

    @FXML
    private void handleEditOption(ActionEvent event) {
        Book selectedBook = tableView.getSelectionModel().getSelectedItem();
        if(selectedBook == null){
            Alert alert1 =  new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("No book selected");
            alert1.showAndWait();
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/app/addbook/addBook.fxml"));
            Parent parent = loader.load();
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(parent));
            AddBookController controller = (AddBookController) loader.getController();
            controller.inflateUI(selectedBook);
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
    private void handleRefreshOption(ActionEvent event) {
        loadData();
    }

    private void clearTable() {
        list.clear();
    }
    
    public static class Book
    {
        private final SimpleStringProperty title;
        private final SimpleStringProperty id;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
        private final SimpleStringProperty code;
        private final SimpleBooleanProperty avalibility;
        
        public Book(String title, String id, String author, String publisher, String code, Boolean avali)
        {
            this.title = new SimpleStringProperty(title);
            this.id = new SimpleStringProperty(id);
            this.author = new SimpleStringProperty(author);
            this.publisher = new SimpleStringProperty(publisher);
            this.code = new SimpleStringProperty(code);
            this.avalibility = new SimpleBooleanProperty(avali);
        }
        
        public String getTitle()
        {
            return title.get();
        }
        public String getId()
        {
            return id.get();
        }
        public String getAuthor()
        {
            return author.get();
        }
        public String getPublisher()
        {
            return publisher.get();
        }
        public String getCode()
        {
            return code.get();
        }
        public Boolean getAvalibility()
        {
            return avalibility.get();
        }
    }
}
