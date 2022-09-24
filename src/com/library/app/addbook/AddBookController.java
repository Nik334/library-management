package com.library.app.addbook;

import com.library.app.database.DatabaseHandler;
import com.library.app.listBook.ListBookController;
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

public class AddBookController implements Initializable {

    DatabaseHandler databaseHandler;
    Boolean isInEditMode = Boolean.FALSE;
    
    @FXML
    private TextField title;
    @FXML
    private TextField id;
    @FXML
    private TextField author;
    @FXML
    private TextField publisher;
    @FXML
    private TextField code;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    
    private AnchorPane rootPane;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
    }    

    @FXML
    public void addBook(ActionEvent event) {
        String bookId = id.getText();
        String booktitle = title.getText();
        String bookauthor = author.getText();
        String bookpublisher = publisher.getText();
        String bookcode = code.getText();
        
        if(bookId.isEmpty()||bookauthor.isEmpty()||booktitle.isEmpty()||bookpublisher.isEmpty()||bookcode.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter in all field");
            alert.showAndWait();
            return;
        }
        
        if(isInEditMode)
        {
            updateBook();
            return;
        }
//        smt.execute("create table "+tableName +"(\n" +
//                        "id varchar(100) primary key,\n" +
//                        "title varchar(200),\n" +
//                        "author varchar(200),\n" +
//                        "publisher varchar(200),\n" +
//                        "intcode varchar(200),\n" +
//                        "isAvail boolean default true\n" +
//                        ")");
        String qu = "INSERT INTO book values ("
                + "'"+ bookId+"',"
                + "'"+ booktitle+"',"
                + "'"+ bookauthor+"',"
                + "'"+ bookpublisher+"',"
                + "'"+ bookcode+"',"
                + "true"
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

    public void inflateUI(ListBookController.Book selectedBook) {
        id.setText(selectedBook.getId());
        title.setText(selectedBook.getTitle());
        author.setText(selectedBook.getAuthor());
        publisher.setText(selectedBook.getPublisher());
        code.setText(selectedBook.getCode());
        isInEditMode = Boolean.TRUE;
    }

    private void updateBook() {
        ListBookController.Book book = new ListBookController.Book(title.getText(),id.getText(),author.getText(),publisher.getText(),code.getText(),true);
        if(databaseHandler.updateBook(book))
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Book successfully Updated");
            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Can't Update Book");
            alert.showAndWait();
        }
    }

    private void clearField() {
        id.setText("");
        title.setText("");
        author.setText("");
        publisher.setText("");
        code.setText("");
    }
    

}
