package com.example.hospitalmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    @FXML
    private AnchorPane listDoctorUI, listPatientUI, dashboardUI;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private VBox VBoxListDoctor;
    @FXML
    private Label label;
    @FXML
    private VBox VBoxlistPatient;
    @FXML
    private BarChart<String, Number> monthlyChart;
    @FXML
    private BarChart<String, Number> yearlyChart;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Image shieldImage = new Image(getClass().getResource("/images/logo-hospital.png").toString());
            brandingImageView.setImage(shieldImage);
        } catch (Exception e) {
            System.err.println("Không thể tải ảnh logo.jpg: " + e.getMessage());
        }
        dashboardUI.setVisible(true);
        listDoctorUI.setVisible(false);
        listPatientUI.setVisible(false);
        label.setText("Dashboard Overview");

        loadDoctorList();
        loadPatientList();
        loadDashboardData();
    }

    public void closeButtonOnAction(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login-view.fxml"));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setWidth(800);
            stage.setHeight(500);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void dashboardButtonOnAction(ActionEvent event) {
        dashboardUI.setVisible(true);
        listDoctorUI.setVisible(false);
        listPatientUI.setVisible(false);
        label.setText("Dashboard Overview");
    }

    public void listDoctorButtonOnAction(ActionEvent event){
        dashboardUI.setVisible(false);
        listDoctorUI.setVisible(true);
        listPatientUI.setVisible(false);
        label.setText("Doctor Management");
    }

    public void listPatientButtonOnAction(ActionEvent event){
        dashboardUI.setVisible(false);
        listDoctorUI.setVisible(false);
        listPatientUI.setVisible(true);
        label.setText("Patient Management");
    }

    public void loadPatientList(){
        String sql = "SELECT id_user, firstname_user, lastname_user From user_account";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int accountID = rs.getInt("id_user");
                String fullName = rs.getString("firstname_user" ) + " " +rs.getString("lastname_user");
                HBox userEntry = createUserEntry(accountID, fullName);
                VBoxlistPatient.getChildren().add(userEntry);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void loadDoctorList(){
        String sql = "SELECT id_doctor, firstname_doctor, lastname_doctor From doctor_account";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int accountID = rs.getInt("id_doctor");
                String fullName = rs.getString("firstname_doctor" ) + " " +rs.getString("lastname_doctor");
                HBox userEntry = createUserEntry(accountID, fullName);
                VBoxListDoctor.getChildren().add(userEntry);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private HBox createUserEntry(int ID, String name) {
        String COLOR_NAVY = "#0A2647";
        String COLOR_WHITE = "#FFFFFF";
        String COLOR_GRAY_TEXT = "#607D8B";
        String COLOR_BORDER = "#E0E0E0";

        HBox userCard = new HBox();
        userCard.setAlignment(Pos.CENTER_LEFT);
        userCard.setSpacing(15);
        userCard.setPadding(new Insets(10, 15, 10, 15));

        userCard.setStyle(
                "-fx-background-color: " + COLOR_WHITE + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + COLOR_BORDER + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );

        DropShadow cardShadow = new DropShadow();
        cardShadow.setBlurType(BlurType.THREE_PASS_BOX);
        cardShadow.setColor(Color.rgb(0, 0, 0, 0.15));
        cardShadow.setRadius(10);
        cardShadow.setOffsetY(4);
        cardShadow.setOffsetX(0);
        userCard.setEffect(cardShadow);

        userCard.setOnMouseEntered(e -> cardShadow.setColor(Color.rgb(0, 0, 0, 0.25)));
        userCard.setOnMouseExited(e -> cardShadow.setColor(Color.rgb(0, 0, 0, 0.15)));

        ImageView iconUser = new ImageView();
        try {
            String imagePath = getClass().getResource("/images/user-icon.png").toExternalForm();
            Image icon = new Image(imagePath);
            iconUser.setImage(icon);
            iconUser.setFitWidth(56);
            iconUser.setFitHeight(56);
            iconUser.setPreserveRatio(true);
            iconUser.setSmooth(true);

            Circle clip = new Circle(28, 28, 28);
            iconUser.setClip(clip);

        } catch (Exception e) {
            Circle placeholder = new Circle(28, Color.LIGHTGRAY);
            userCard.getChildren().add(placeholder);
        }

        if (iconUser.getImage() != null) {
            userCard.getChildren().add(iconUser);
        }

        Label nameLabel = new Label(name);
        nameLabel.setTextFill(Color.web(COLOR_NAVY));
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        Label idLabel = new Label("ID: " + ID);
        idLabel.setTextFill(Color.web(COLOR_GRAY_TEXT));
        idLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));

        VBox textContainer = new VBox(3, nameLabel, idLabel);
        textContainer.setAlignment(Pos.CENTER_LEFT);
        textContainer.setCursor(Cursor.HAND);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String btnNormalStyle =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + COLOR_NAVY + ";" +
                        "-fx-border-color: " + COLOR_NAVY + ";" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;";

        String btnHoverStyle =
                "-fx-background-color: " + COLOR_NAVY + ";" +
                        "-fx-text-fill: " + COLOR_WHITE + ";" +
                        "-fx-border-color: " + COLOR_NAVY + ";" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;";

        userCard.getChildren().addAll(textContainer, spacer);

        HBox outerContainer = new HBox(userCard);
        HBox.setHgrow(userCard, Priority.ALWAYS);

        VBox.setMargin(outerContainer, new Insets(5, 15, 5, 15));

        return outerContainer;
    }

    public void loadDashboardData() {
        XYChart.Series<String, Number> seriesMonth = new XYChart.Series<>();
        seriesMonth.setName("2025");
        seriesMonth.getData().add(new XYChart.Data<>("Jan", 12000));
        seriesMonth.getData().add(new XYChart.Data<>("Feb", 15000));
        seriesMonth.getData().add(new XYChart.Data<>("Mar", 11000));
        seriesMonth.getData().add(new XYChart.Data<>("Apr", 18500));
        monthlyChart.getData().add(seriesMonth);

        XYChart.Series<String, Number> seriesYear = new XYChart.Series<>();
        seriesYear.setName("Total Revenue");
        seriesYear.getData().add(new XYChart.Data<>("2023", 150000));
        seriesYear.getData().add(new XYChart.Data<>("2024", 210000));
        seriesYear.getData().add(new XYChart.Data<>("2025", 56500));
        yearlyChart.getData().add(seriesYear);
    }
}
