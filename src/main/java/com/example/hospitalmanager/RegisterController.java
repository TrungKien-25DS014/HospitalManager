package com.example.hospitalmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*; // Import đầy đủ thư viện SQL
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private Label registrationMessageLabel;
    @FXML
    private PasswordField setPassword;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Label confirmPasswordLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Image shieldImage = new Image(getClass().getResource("/images/logo-hospital.png").toString());
            brandingImageView.setImage(shieldImage);
        } catch (Exception e) {
            System.err.println("Logo not found");
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

    public boolean checkPasswordMatch() {
        return setPassword.getText().equals(confirmPassword.getText());
    }

    public boolean checkEmpty() {
        return !emailField.getText().isBlank() && !setPassword.getText().isBlank()
                && !firstNameField.getText().isBlank();
    }

    public boolean isEmailUnique() {
        DataBaseConnection connection = new DataBaseConnection();
        String verifyLogin = "SELECT COUNT(*) FROM user_account WHERE email_user = ?";
        try (Connection connectionDB = connection.getConnection();
             PreparedStatement preparedStatement = connectionDB.prepareStatement(verifyLogin)) {
                preparedStatement.setString(1, emailField.getText());
                ResultSet queryResult = preparedStatement.executeQuery();

            if (queryResult.next()) {
                return queryResult.getInt(1) == 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void registerButtonOnAction(ActionEvent e) {
        registrationMessageLabel.setText("");
        confirmPasswordLabel.setText("");
        if(!SecurityUtils.isValidEmail(emailField.getText())){
            confirmPasswordLabel.setText("Invalid email format (name@domain.com).");
            return;
        }
        if (!checkEmpty()) {
            confirmPasswordLabel.setText("Please enter all details.");
            return;
        }
        if (!checkPasswordMatch()) {
            confirmPasswordLabel.setText("Password does not match.");
            return;
        }
        if (!isEmailUnique()) {
            confirmPasswordLabel.setText("This email is already registered.");
            return;
        }
        String rawPassword = setPassword.getText();
        String hashedPassword = SecurityUtils.HashPassword(rawPassword);
        if(hashedPassword == null){
            confirmPasswordLabel.setText("Error processing password.");
            return;
        }

        DataBaseConnection connection = new DataBaseConnection();
        Connection connectionDB = null;
        String insertUserSQL = "INSERT INTO user_account (firstname_user, lastname_user, email_user, password_user) VALUES (?, ?, ?, ?)";
        String insertInfoSQL = "INSERT INTO information_user (id_user, firstname_user, lastname_user) VALUES (?, ?, ?)";
        try {
            connectionDB = connection.getConnection();
            connectionDB.setAutoCommit(false);
            PreparedStatement psUser = connectionDB.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, firstNameField.getText());
            psUser.setString(2, lastNameField.getText());
            psUser.setString(3, emailField.getText());
            psUser.setString(4, hashedPassword);
            int affectedRows = psUser.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            int newUserId = 0;
            try (ResultSet generatedKeys = psUser.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newUserId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            PreparedStatement psInfo = connectionDB.prepareStatement(insertInfoSQL);
            psInfo.setInt(1, newUserId);
            psInfo.setString(2, firstNameField.getText());
            psInfo.setString(3, lastNameField.getText());
            psInfo.executeUpdate();
            connectionDB.commit();

            registrationMessageLabel.setText("User has been registered successfully!");

        } catch (Exception ex) {
            try {
                if (connectionDB != null) {
                    connectionDB.rollback();
                    confirmPasswordLabel.setText("Registration failed! Please try again.");
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            ex.printStackTrace();
        } finally {
            try {
                if (connectionDB != null) {
                    connectionDB.setAutoCommit(true);
                    connectionDB.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }
}