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
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

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
    @FXML Label detailNameLabel, detailIdLabel, detailRoleLabel, detailGenderLabel, detailDobLabel, detailNationalityLabel, detailCccdLabel, detailEthnicLabel, detailPhoneLabel, detailEmailLabel, detailAddressLabel, detailSpecialtyLabel, detailRankLabel, detailDepartmentLabel, detailLicenseLabel, detailInsuranceLabel, detailAllergyLabel;
    @FXML
    private BarChart<String, Number> monthlyChart;
    @FXML
    private BarChart<String, Number> yearlyChart;
    @FXML private AnchorPane paneDetail;
    @FXML private Button BtnBackFromDetail;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Image shieldImage = new Image(getClass().getResource("/images/logo-hospital.png").toString());
            brandingImageView.setImage(shieldImage);
        } catch (Exception e) {
            System.err.println("Cannot load logo image: " + e.getMessage());
        }
        dashboardUI.setVisible(true);
        listDoctorUI.setVisible(false);
        listPatientUI.setVisible(false);
        label.setText("Dashboard Overview");

        paneDetail.setVisible(false);

        loadDoctorList();
        loadPatientList();
        loadDashboardData();
    }

    public void BtnBackFromDetailAction(ActionEvent event){
        paneDetail.setVisible(false);
        if (label.getText().equals("Doctor Management")) listDoctorUI.setVisible(true);
        else if (label.getText().equals("Patient Management")) listPatientUI.setVisible(true);
        else dashboardUI.setVisible(true);
    }

    private void showDoctorDetails(int doctorID){
        String sql = "SELECT * FROM information_doctor I INNER JOIN doctor_account A on I.id_doctor = A.id_doctor WHERE A.id_doctor = ?";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ps.setInt(1, doctorID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                listPatientUI.setVisible(false);
                listDoctorUI.setVisible(false);
                dashboardUI.setVisible(false);
                paneDetail.setVisible(true);

                detailNameLabel.setText(rs.getString("firstname_doctor") +" "+ rs.getString("lastname_doctor"));
                detailIdLabel.setText("ID: " + rs.getInt("id_doctor"));
                detailRoleLabel.setText("Doctor");
                detailGenderLabel.setText(rs.getString("gender"));
                detailDobLabel.setText("N/A");
                detailNationalityLabel.setText(rs.getString("nationality"));
                detailCccdLabel.setText(rs.getString("cccd"));
                detailEthnicLabel.setText(rs.getString("ethnicgroup"));
                detailPhoneLabel.setText(rs.getString("phone"));
                detailEmailLabel.setText(rs.getString("email_doctor"));
                detailAddressLabel.setText(rs.getString("address"));
                detailSpecialtyLabel.setText(rs.getString("specialty"));
                detailDepartmentLabel.setText(rs.getString("department"));
                detailRankLabel.setText(rs.getString("rank"));
                detailLicenseLabel.setText(rs.getString("licenseNo"));
                detailInsuranceLabel.setText("N/A");
                detailAllergyLabel.setText("N/A");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void showPatientDetails(int doctorID){
        String sql = "SELECT * FROM information_user I INNER JOIN user_account A ON I.id_user = A.id_user WHERE A.id_user = ?";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ps.setInt(1, doctorID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                listPatientUI.setVisible(false);
                listDoctorUI.setVisible(false);
                dashboardUI.setVisible(false);
                paneDetail.setVisible(true);

                detailNameLabel.setText(rs.getString("firstname_user") +" "+ rs.getString("lastname_user"));
                detailIdLabel.setText("ID: " + rs.getInt("id_user"));
                detailRoleLabel.setText("Patient");
                detailGenderLabel.setText(rs.getString("gender"));
                detailDobLabel.setText("Date of Birth");
                detailNationalityLabel.setText("Viet Nam");
                detailCccdLabel.setText(rs.getString("cccd"));
                detailEthnicLabel.setText(rs.getString("ethnicGroup"));
                detailPhoneLabel.setText(rs.getString("phone"));
                detailEmailLabel.setText(rs.getString("email_user"));
                detailAddressLabel.setText(rs.getString("address"));
                detailSpecialtyLabel.setText("N/A");
                detailDepartmentLabel.setText("N/A");
                detailRankLabel.setText("N/A");
                detailLicenseLabel.setText("N/A");
                detailInsuranceLabel.setText(rs.getString("BHYT"));
                detailAllergyLabel.setText(rs.getString("allergy"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
                userEntry.setOnMouseClicked(event -> {
                    showPatientDetails(accountID);
                });
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
                userEntry.setOnMouseClicked(event -> {
                    showDoctorDetails(accountID);
                });
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

        userCard.getChildren().addAll(textContainer, spacer);

        HBox outerContainer = new HBox(userCard);
        HBox.setHgrow(userCard, Priority.ALWAYS);

        VBox.setMargin(outerContainer, new Insets(5, 15, 5, 15));

        return outerContainer;
    }

    public void loadDashboardData() {
        loadYearlyData();
        loadMonthlyData(2025);
    }

    private void loadMonthlyData(int year) {
        monthlyChart.getData().clear();
        monthlyChart.setAnimated(false);

        DataBaseConnection connection = new DataBaseConnection();
        Connection connectionDB = connection.getConnection();

        XYChart.Series<String, Number> seriesMonth = new XYChart.Series<>();
        Map<Integer, Double> dataMapMonth = new TreeMap<>();
        for (int i = 1; i <= 12; i++) {
            dataMapMonth.put(i, 0.0);
        }

        seriesMonth.setName("Revenue " + year);

        String monthSql = "SELECT MONTH(visitDate) as thang, sum(total_amount) as totalMonth FROM medical_records WHERE YEAR(visitDate) = ? GROUP BY MONTH(visitDate) ORDER BY thang ASC";
        try{
            PreparedStatement psMonth = connectionDB.prepareStatement(monthSql);
            psMonth.setInt(1,year);
            ResultSet rsMonth = psMonth.executeQuery();
            while(rsMonth.next()){
                int month = rsMonth.getInt("thang");
                double revenue = rsMonth.getDouble("totalMonth");
                if (dataMapMonth.containsKey(month)){
                    dataMapMonth.put(month, revenue);
                }
            }
            for(Map.Entry<Integer, Double> entry: dataMapMonth.entrySet()){
                String monthName = getMonthName(entry.getKey());
                Number revenue = entry.getValue();
                seriesMonth.getData().add(new XYChart.Data<>(monthName, revenue));
            }
            monthlyChart.getData().add(seriesMonth);
            psMonth.close();
            connectionDB.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void loadYearlyData(){
        yearlyChart.getData().clear();

        DataBaseConnection connection = new DataBaseConnection();
        Connection connectionDB = connection.getConnection();

        XYChart.Series<String, Number> seriesYear = new XYChart.Series<>();
        Map<Integer, Double> dataMap = new TreeMap<>();
        for (int i = 2025; i <= 2030; i++) {
            dataMap.put(i, 0.0);
        }
        seriesYear.setName("2025-2030");

        String YearSql = "SELECT YEAR(visitDate) as nam, sum(total_amount) as totalYear FROM medical_records WHERE YEAR(visitDate) BETWEEN 2025 AND 2030 GROUP BY YEAR(visitDate) ORDER BY nam ASC";
        try{
            PreparedStatement psYear = connectionDB.prepareStatement(YearSql);
            ResultSet rsYear = psYear.executeQuery();

            while(rsYear.next()){
                int year = rsYear.getInt("nam");
                double totalYear = rsYear.getDouble("totalYear");
                if(dataMap.containsKey(year)){
                    dataMap.put(year, totalYear);
                }
            }
            for(Map.Entry<Integer, Double> entry : dataMap.entrySet()){
                String yearString = String.valueOf(entry.getKey());
                Number revenue = entry.getValue();
                seriesYear.getData().add(new XYChart.Data<>(yearString, revenue));
            }
            yearlyChart.getData().add(seriesYear);
            for(XYChart.Data<String, Number> data : seriesYear.getData()){
                Node node = data.getNode();
                if(node != null){
                    node.setCursor(Cursor.HAND);
                    node.setOnMouseClicked(e ->{
                        String yearClick = data.getXValue();
                        int yearChoosen = Integer.parseInt(yearClick);
                        loadMonthlyData(yearChoosen);
                    });
                }
            }
            psYear.close();
            connectionDB.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String getMonthName(int month) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        if (month >= 1 && month <= 12) {
            return monthNames[month - 1];
        }
        return String.valueOf(month);
    }
}