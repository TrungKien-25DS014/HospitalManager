package com.example.hospitalmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.sql.*;
import java.util.ResourceBundle;
import java.net.URL;

public class LoginController implements Initializable {
    @FXML
    private Label loginMassageLabel1;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private ComboBox<String> inputType;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Image shieldImage = new Image(getClass().getResource("/images/logo-hospital.png").toString());
            brandingImageView.setImage(shieldImage);
        } catch (Exception e) {
            System.err.println("Cannot load logo image: " + e.getMessage());
        }
        ObservableList<String> departmentList = FXCollections.observableArrayList(
                "doctor",
                "patient",
                /*"receptionist",*/
                "admin"
        );
        inputType.setItems(departmentList);
    }

    public void loginButtonOnAction(ActionEvent event) {
        if (!emailField.getText().isBlank() && !passwordField.getText().isBlank()) {
            validateLogin(event);
        } else {
            loginMassageLabel1.setText("Please enter Email and Password.");
        }
    }

    public void createAccountButtonOnAction(ActionEvent event) {
        createAccountFrom();
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void validateLogin(ActionEvent event) {
        DataBaseConnection connection = new DataBaseConnection();
        Connection connectionDB = null;
        try {
            connectionDB = connection.getConnection();
            String email = emailField.getText();
            String rawPassword = passwordField.getText();

            if (email.isEmpty() || rawPassword.isEmpty()) {
                loginMassageLabel1.setText("Please enter both Email and Password.");
                return;
            }
            String hashedPassword = SecurityUtils.hashPassword(rawPassword);

            if (inputType.getValue() == null) {
                loginMassageLabel1.setText("Please select a role.");
                return;
            }

            switch ((String) inputType.getValue()){
                case "patient":
                    final String patientLoginQuery = "SELECT id_user, typeInput FROM user_account WHERE email_user = ? AND password_user = ?";
                    try (PreparedStatement ps = connectionDB.prepareStatement(patientLoginQuery)) {
                        ps.setString(1, email);
                        ps.setString(2, hashedPassword);

                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            String role = rs.getString("typeInput");
                            if ("patient".equalsIgnoreCase(role)) {
                                int idUser = rs.getInt("id_user");
                                UserSession.setUserId(idUser);
                                System.out.println("Login successful with ID: " + idUser);
                                patientUI();
                                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

                            } else {
                                loginMassageLabel1.setText("This account is not a Patient account!");
                            }
                        } else {
                            loginMassageLabel1.setText("Incorrect Email or Password!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case "doctor":
                    final String doctorLoginQuery = "SELECT id_doctor, typeInput FROM doctor_account WHERE email_doctor = ? AND password_doctor = ?";
                    try (PreparedStatement ps = connectionDB.prepareStatement(doctorLoginQuery)) {
                        ps.setString(1, email);
                        ps.setString(2, hashedPassword);

                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            String role = rs.getString("typeInput");
                            if ("doctor".equalsIgnoreCase(role)) {
                                int idDoctor = rs.getInt("id_doctor");
                                DoctorSession.setDoctorId(idDoctor);
                                System.out.println("Login successful with ID: " + idDoctor);
                                doctorUI();
                                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

                            } else {
                                loginMassageLabel1.setText("This account is not a Doctor account!");
                            }
                        } else {
                            loginMassageLabel1.setText("Incorrect Email or Password!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case "receptionist":
                    final String receptionistLoginQuery = "SELECT typeInput FROM receptionist_account WHERE email_receptionist = ? AND password_receptionist = ?";
                    if (checkAndProcessLogin(connectionDB, receptionistLoginQuery, email, rawPassword, "receptionist")) {
                        receptionistUI();
                        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        currentStage.close();
                    }else {
                        loginMassageLabel1.setText("Incorrect Email or Password.");
                    }
                    break;

                case "admin":
                    final String adminLoginQuery = "SELECT typeInput FROM admin_account WHERE email_admin = ? AND password_admin = ?";
                    if (checkAndProcessLogin(connectionDB, adminLoginQuery, email, rawPassword, "admin")) {
                        adminUI();
                        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        currentStage.close();
                    }else {
                        loginMassageLabel1.setText("Incorrect Email or Password.");
                    }
                    break;

                default:
                    loginMassageLabel1.setText("Invalid login type selected.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            loginMassageLabel1.setText("DATABASE CONNECTION ERROR");
        } finally {
            try {
                if (connectionDB != null) {
                    connectionDB.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkAndProcessLogin(Connection conn, String query, String email, String pass, String typeInput) throws SQLException {
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            String hashedPassword = SecurityUtils.hashPassword(pass);
            preparedStatement.setString(2, hashedPassword);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void createAccountFrom() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("registration-view.fxml"));
            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root, 800, 500));
            registerStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void patientUI() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Patient-view.fxml"));
            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root, 800, 500));
            registerStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void receptionistUI() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Receptionist-view.fxml"));
            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root, 800, 500));
            registerStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void adminUI() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Admin-view.fxml"));
            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root, 800, 500));
            registerStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void doctorUI() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Doctor-view.fxml"));
            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root, 800, 500));
            registerStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }
}