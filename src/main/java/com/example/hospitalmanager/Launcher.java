package com.example.hospitalmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher {
    public void start(Stage stage) throws IOException {
        // Tên file FXML phải khớp
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));

        // Thiết lập Scene (kích thước)
        Scene scene = new Scene(fxmlLoader.load(), 350, 300);

        stage.setTitle("Bệnh Viện Manager");
        stage.setScene(scene);

        // Hiển thị cửa sổ (BẮT BUỘC)
        stage.show();
    }

    public static void main(String[] args) {
       new Launcher();

    }
}
