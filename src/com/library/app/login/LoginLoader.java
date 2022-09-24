package com.library.app.login;

import com.library.app.database.DatabaseHandler;
import com.library.app.util.LibraryUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginLoader extends Application
{

    public static void main(String[] args) {
        launch(args);
    }    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root =FXMLLoader.load(getClass().getResource("userlogin.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
        LibraryUtil.setStageIcon(primaryStage);
        new Thread(() -> {
            DatabaseHandler.getInstance();
        }).start();
    }
    
}
