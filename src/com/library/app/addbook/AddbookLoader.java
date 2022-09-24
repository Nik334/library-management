package com.library.app.addbook;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddbookLoader extends Application{

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addBook.fxml"));
        AnchorPane addBook = loader.load();

        Scene scene = new Scene(addBook);

        primaryStage.setScene(scene);
//        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
        primaryStage.setTitle("AddBook");
        primaryStage.show();
    }
    
}
