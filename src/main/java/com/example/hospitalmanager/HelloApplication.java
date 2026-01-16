package com.example.hospitalmanager; // Thay đổi package này nếu cần

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private static final String FXML_FILE = "Login-view.fxml";

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_FILE));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 900, 600);
            primaryStage.setTitle("Hệ Thống Quản Lý Bệnh Viện");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Lỗi: Không thể load file FXML: " + FXML_FILE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}