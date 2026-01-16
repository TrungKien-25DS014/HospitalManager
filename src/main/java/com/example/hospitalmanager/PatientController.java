package com.example.hospitalmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;


public class PatientController implements Initializable {

    private int currentUserId;

    @FXML private Button informationButton;
    @FXML private Button appointmentButton;
    @FXML private Button medicalHistoryButton;
    @FXML private Button LogOutButton;
    @FXML private ImageView brandingImageView;
    @FXML private Label labelNameUser;

    @FXML private AnchorPane paneInformation;
    @FXML private AnchorPane paneAppointment;
    @FXML private AnchorPane paneMedicalHistory;

    @FXML private TextField genderField, CCCDField, phoneField, ethnicGroupField, addressField, BHYTField, allergyField;
    @FXML private DatePicker birthDayField;
    @FXML private Button cancelButton, saveButton, updateButton;

    @FXML private ComboBox<String> timeComboBox;
    @FXML private ComboBox<DoctorAccount> doctorComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextArea reasonTextArea;

    @FXML private StackPane globalAlertContainer;
    @FXML private VBox customAlertBox;
    @FXML private Label alertTitle;
    @FXML private Label customAlertMessage;

    @FXML private TableView<MedicalHistoryModel> historyTableView;
    @FXML private TableColumn<MedicalHistoryModel, String> colHistoryDate;
    @FXML private TableColumn<MedicalHistoryModel, String> colHistoryDiagnosis;
    @FXML private TableColumn<MedicalHistoryModel, String> colHistoryDoctor;
    @FXML private TableColumn<MedicalHistoryModel, Double> colHistoryTotal;

    @FXML private VBox paneDetailContainer;
    @FXML private TableView<PrescriptionDetailModel> tableDetailHistory;
    @FXML private TableColumn<PrescriptionDetailModel, String> colDetailDrug;
    @FXML private TableColumn<PrescriptionDetailModel, String> colDetailUnit;
    @FXML private TableColumn<PrescriptionDetailModel, Integer> colDetailQty;
    @FXML private TableColumn<PrescriptionDetailModel, Double> colDetailPrice;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupLogo();

        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        setEditMode(false);

        this.currentUserId = UserSession.getUserId();
        loadUserDisplayName(this.currentUserId);
        loadUserProfile(this.currentUserId);

        timeComboBox.setItems(getAvailableTimeSlots());
        loadDoctorsToComboBox();

        paneDetailContainer.setVisible(false);

        colHistoryDate.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
        colHistoryDiagnosis.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        colHistoryDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colHistoryTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        loadMyHistory();

        colDetailDrug.setCellValueFactory(new PropertyValueFactory<>("drugName"));
        colDetailQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colDetailUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colDetailPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        historyTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection)->{
            if(newSelection != null){
                    paneDetailContainer.setVisible(true);
                loadPrescriptionDetail(newSelection.getRecordID());
            }
        });

    }

    private void setupLogo() {
        try {
            Image shieldImage = new Image(getClass().getResource("/images/logo-hospital.png").toString());
            brandingImageView.setImage(shieldImage);
        } catch (Exception e) {
            System.err.println("Failed to load logo image: " + e.getMessage());
        }
    }

    public void informationButtonOnAction(ActionEvent event){
        paneInformation.setVisible(true);
        paneAppointment.setVisible(false);
        paneMedicalHistory.setVisible(false);
    }

    public void appointmentButtonOnAction(ActionEvent event){
        paneInformation.setVisible(false);
        paneAppointment.setVisible(true);
        paneMedicalHistory.setVisible(false);
    }

    public void medicalHistoryButtonOnAction(ActionEvent event){
        paneInformation.setVisible(false);
        paneAppointment.setVisible(false);
        paneMedicalHistory.setVisible(true);
        loadMyHistory();
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

    private void loadUserDisplayName(int id){
        String sql = "SELECT * FROM user_account WHERE id_user = ?";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String fullName = rs.getString("firstname_user" ) + " " +rs.getString("lastname_user");
                labelNameUser.setText(fullName +"-"+id);
            }
            connectionDB.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void loadUserProfile(int idUser){
        String sql = "SELECT * FROM information_user WHERE id_user = ?";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                genderField.setText(rs.getString("gender"));
                CCCDField.setText(rs.getString("CCCD"));
                phoneField.setText(rs.getString("phone"));
                ethnicGroupField.setText(rs.getString("ethnicGroup"));
                addressField.setText(rs.getString("address"));
                BHYTField.setText(rs.getString("BHYT"));
                allergyField.setText(rs.getString("allergy"));

                java.sql.Date dateSQL = rs.getDate("birthday");
                if (dateSQL != null) {
                    birthDayField.setValue(dateSQL.toLocalDate());
                } else {
                    birthDayField.setValue(null);
                }
            }
            connectionDB.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateButtonOnAction(ActionEvent e){
        setEditMode(true);
        cancelButton.setVisible(true);
        saveButton.setVisible(true);
        updateButton.setVisible(false);
    }

    public void cancelButtonOnAction(ActionEvent e){
        setEditMode(false);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        updateButton.setVisible(true);
        loadUserProfile(this.currentUserId);
    }

    public void saveButtonOnAction(ActionEvent e){
        setEditMode(false);
        String insertSQL = "UPDATE information_user SET gender=?, CCCD=?, birthday=?, phone=?, ethnicGroup=?, address=?, BHYT=?, allergy=? WHERE id_user = ?";
        DataBaseConnection connection = new DataBaseConnection();
        try(Connection connectionDB = connection.getConnection();
            PreparedStatement preparedStatement = connectionDB.prepareStatement(insertSQL)){

            preparedStatement.setString(1,genderField.getText());
            preparedStatement.setString(2,CCCDField.getText());

            LocalDate localDate = birthDayField.getValue();
            if (localDate != null) {
                preparedStatement.setDate(3, java.sql.Date.valueOf(localDate));
            } else {
                preparedStatement.setNull(3, java.sql.Types.DATE);
            }

            preparedStatement.setString(4,phoneField.getText());
            preparedStatement.setString(5, ethnicGroupField.getText());
            preparedStatement.setString(6, addressField.getText());
            preparedStatement.setString(7,BHYTField.getText());
            preparedStatement.setString(8, allergyField.getText());
            preparedStatement.setInt(9, this.currentUserId);

            preparedStatement.executeUpdate();
        }catch(Exception ex){
            ex.printStackTrace();
            showAlert("Error", "Failed to save information: " + ex.getMessage(), "error");
        }

        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        updateButton.setVisible(true);
    }

    public void setEditMode(boolean isEditMode){
        genderField.setEditable(isEditMode);
        CCCDField.setEditable(isEditMode);
        birthDayField.setEditable(isEditMode);
        phoneField.setEditable(isEditMode);
        ethnicGroupField.setEditable(isEditMode);
        addressField.setEditable(isEditMode);
        BHYTField.setEditable(isEditMode);
        allergyField.setEditable(isEditMode);

        updateButton.setDisable(isEditMode);
        cancelButton.setDisable(!isEditMode);
        saveButton.setDisable(!isEditMode);
    }

    private void loadDoctorsToComboBox(){
        ObservableList<DoctorAccount> listDoctor = FXCollections.observableArrayList();
        String sql = "SELECT id_doctor, firstname_doctor, lastname_doctor FROM doctor_account";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int doctorID = rs.getInt("id_doctor");
                String fullname = rs.getString("firstname_doctor") + " " + rs.getString("lastname_doctor");
                listDoctor.add(new DoctorAccount(doctorID, fullname));
            }
            doctorComboBox.setItems(listDoctor);
            connectionDB.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private ObservableList<String> getAvailableTimeSlots(){
        ObservableList<String> times = FXCollections.observableArrayList();
        times.add("7:30");
        for(int i = 8; i<11; i++){
            times.add(String.format("%02d:00",i));
            times.add(String.format("%02d:30",i));
        }
        for(int i = 13; i < 19; i++){
            times.add(String.format("%02d:00",i));
            times.add(String.format("%02d:30",i));
        }
        return times;
    }

    private boolean isSlotBooked(int doctorID, LocalDate Date, String timeToCheck ){
        String sql = "SELECT COUNT(*) FROM appointments " +
                "WHERE id_doctor = ? " +
                "AND appointment_date = ? " +
                "AND status != 'Cancelled' " +
                "AND ABS(TIMESTAMPDIFF(MINUTE, STR_TO_DATE(appointment_time, '%H:%i'), STR_TO_DATE(?, '%H:%i'))) < 15";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);

            ps.setInt(1, doctorID);
            ps.setDate(2, java.sql.Date.valueOf(Date));
            ps.setString(3, timeToCheck);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                boolean booked = rs.getInt(1) > 0;
                connectionDB.close();
                return booked;
            }
            connectionDB.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void handleBookAppointment(ActionEvent event){
        if (doctorComboBox.getValue() == null || datePicker.getValue() == null || timeComboBox.getValue() == null) {
            showAlert("Error", "Please fill in all information!", "error");
            return;
        }
        int doctorID = doctorComboBox.getValue().getIdDoctor();
        LocalDate date = datePicker.getValue();
        String timeString = timeComboBox.getValue();
        String reason = reasonTextArea.getText();

        if(isSlotBooked(doctorID, date, timeString)){
            showAlert("Warning", "This slot is booked or too close to another appointment (<15m). Please select another time!", "error");
            return;
        }

        LocalTime time = LocalTime.parse(timeString);
        LocalDateTime appointmentDateTime = LocalDateTime.of(date,time);
        LocalDateTime currentDateTime = LocalDateTime.now();

        if(appointmentDateTime.isBefore(currentDateTime)){
            showAlert("Error", "Cannot book in the past! Please select a valid time.", "error");
            return;
        }

        String sql = "INSERT INTO appointments (id_doctor, id_user, appointment_date, appointment_time, reason, status) VALUES (?, ?, ?, ?, ?, 'Pending')";
        try {
            DataBaseConnection connection = new DataBaseConnection();
            Connection conn = connection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, doctorID);
            ps.setInt(2, this.currentUserId);
            ps.setDate(3, java.sql.Date.valueOf(date));
            ps.setString(4, timeString);
            ps.setString(5, reason);

            ps.executeUpdate();
            conn.close();

            showAlert("Success", "Appointment booked successfully!", "success");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage(), "error");
        }
    }
    private ObservableList<MedicalHistoryModel> HistoryList = FXCollections.observableArrayList();

    public void loadMyHistory(){
        HistoryList.clear();
        String sql = "SELECT M.record_id, M.visitDate, M.diagnosis, D.firstname_doctor, D.lastname_doctor, M.total_amount\n" +
                    "FROM medical_records M INNER jOIN doctor_account D ON M.id_doctor = D.id_doctor WHERE id_user = ? ORDER BY M.visitDate DESC";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ps.setInt(1, this.currentUserId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int id = rs.getInt("record_id");
                String diagnosis = rs.getString("diagnosis");
                String nameDoctor = rs.getString("firstname_doctor") +" "+ rs.getString("lastname_doctor");
                double total_amount = rs.getDouble("total_amount");
                java.sql.Timestamp timestamp = rs.getTimestamp("visitDate");
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                String dateStr = sdf.format(timestamp);
                HistoryList.add( new MedicalHistoryModel(id, dateStr, diagnosis, nameDoctor, total_amount));
            }
            historyTableView.setItems(HistoryList);
            connectionDB.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    ObservableList<PrescriptionDetailModel> PrescriptionDetail = FXCollections.observableArrayList();
    public void loadPrescriptionDetail(int recordID){
        PrescriptionDetail.clear();
        String sql = "SELECT name_drug, quantity, unit, price FROM prescription_details WHERE record_id = ?";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ps.setInt(1, recordID);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                String drugName = rs.getString("name_drug");
                int quantity = rs.getInt("quantity");
                String unit = rs.getString("unit");
                double price = rs.getDouble("price");

                PrescriptionDetail.add(new PrescriptionDetailModel(drugName, quantity, unit, price));
            }
            tableDetailHistory.setItems(PrescriptionDetail);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, String type) {
        customAlertMessage.setText(message);
        alertTitle.setText(title);
        if (type.equalsIgnoreCase("success")) {
            alertTitle.setTextFill(javafx.scene.paint.Color.web("#28a745"));
        } else {
            alertTitle.setTextFill(javafx.scene.paint.Color.web("#dc3545"));
        }

        globalAlertContainer.setVisible(true);
        globalAlertContainer.setMouseTransparent(false);
        customAlertBox.toFront();
    }

    public void closeCustomAlert(ActionEvent event) {
        globalAlertContainer.setVisible(false);
        globalAlertContainer.setMouseTransparent(true);
    }
}