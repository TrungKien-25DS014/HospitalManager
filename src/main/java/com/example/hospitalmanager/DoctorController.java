package com.example.hospitalmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DoctorController implements Initializable {

    @FXML private ImageView brandingImageView;
    @FXML private Button patientsButton;
    @FXML private Button informationButton;
    @FXML private Button appointmentButton;
    @FXML private Button logoutButton;
    @FXML private Label labelNameDoctor;

    @FXML private AnchorPane panePatientList;
    @FXML private AnchorPane paneExamination;
    @FXML private AnchorPane paneProfile;
    @FXML private AnchorPane paneAppointment;
    @FXML private AnchorPane paneHistory;

    @FXML private VBox VboxDashboard;
    @FXML private TextField searchIDTextField;

    @FXML private Label lbFullname;
    @FXML private Label lbBirthDay;
    @FXML private Label lbBHYT;
    @FXML private Label lbGender;
    @FXML private Label lbAllergy;
    @FXML private TextField DiagnoseTextField;
    @FXML private TextField symptomsTextField;

    @FXML private ComboBox<Drug> drugComboBox;
    @FXML private TextField quantityTextField;
    @FXML private Label lblTotalAmount;
    @FXML private Button btnPay;
    @FXML private TableView<PrescriptionItem> prescriptionTableView;
    @FXML private TableColumn<PrescriptionItem, Integer> colSTT;
    @FXML private TableColumn<PrescriptionItem, String> colName;
    @FXML private TableColumn<PrescriptionItem, Integer> colQuanlity;
    @FXML private TableColumn<PrescriptionItem, String> colUnit;
    @FXML private TableColumn<PrescriptionItem, Double> colPrice;
    @FXML private TableColumn<PrescriptionItem, Double> colTotalAmount;

    @FXML private AnchorPane PanePayQR;
    @FXML private ImageView QR;
    @FXML private Button btnMarkPaid;

    @FXML private TextField genderField;
    @FXML private TextField CCCDField;
    @FXML private TextField nationalityField;
    @FXML private TextField phoneField;
    @FXML private TextField ethnicGroupField;
    @FXML private TextField addressField;
    @FXML private TextField specialtyField;
    @FXML private TextField rankField;
    @FXML private TextField licenseNoField;
    @FXML private TextField departmentField;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private Button updateButton;

    @FXML private DatePicker busyDatePicker;
    @FXML private ComboBox<String> busyTimeComboBox;
    @FXML private TableView<AppointmentModel> appointmentTableView;
    @FXML private TableColumn<AppointmentModel, String> colAppDate;
    @FXML private TableColumn<AppointmentModel, String> colAppTime;
    @FXML private TableColumn<AppointmentModel, String> colAppPatient;
    @FXML private TableColumn<AppointmentModel, String> colAppReason;
    @FXML private TableColumn<AppointmentModel, String> colAppStatus;
    @FXML private TableColumn<AppointmentModel, String> colAppAction;

    @FXML private StackPane globalAlertContainer;
    @FXML private VBox customAlertBox;
    @FXML private Label alertTitle;
    @FXML private Label customAlertMessage;

    @FXML private TableView<MedicalHistoryModel> docHistoryTableView;
    @FXML private TableColumn<MedicalHistoryModel, String> docColHistoryDate;
    @FXML private TableColumn<MedicalHistoryModel, String> docColHistoryDiagnosis;
    @FXML private TableColumn<MedicalHistoryModel, String> docColHistoryDoctor;
    @FXML private TableColumn<MedicalHistoryModel, Double> docColHistoryTotal;

    @FXML private VBox docPaneDetailContainer;
    @FXML private TableView<PrescriptionDetailModel> docTableDetailHistory;
    @FXML private TableColumn<PrescriptionDetailModel, String> docColDetailDrug;
    @FXML private TableColumn<PrescriptionDetailModel, String> docColDetailUnit;
    @FXML private TableColumn<PrescriptionDetailModel, Integer> docColDetailQty;
    @FXML private TableColumn<PrescriptionDetailModel, Double> docColDetailPrice;

    private ObservableList<PrescriptionItem> listItems = FXCollections.observableArrayList();
    private ObservableList<Drug> masterData = FXCollections.observableArrayList();
    private FilteredList<Drug> filteredData;
    private ObservableList<AppointmentModel> appointmentList = FXCollections.observableArrayList();

    private int selectedPatientId = -1;
    private int currentDoctorId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupLogo();
        this.currentDoctorId = DoctorSession.getDoctorId();
        loadDoctorDisplayName(this.currentDoctorId);

        loadUserList();
        searchIDTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            performSearch(newValue);
        });

        setupPrescriptionTable();
        loadDrugToComboBox();
        setupSearchableComboBox(drugComboBox, masterData);

        loadDoctorProfile(this.currentDoctorId);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        setEditMode(false);

        setAppointmentTable();
        busyTimeComboBox.setItems(getAvailableTimeSlots());

        docColHistoryDate.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
        docColHistoryDiagnosis.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        docColHistoryDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        docColHistoryTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        docColDetailDrug.setCellValueFactory(new PropertyValueFactory<>("drugName"));
        docColDetailQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        docColDetailUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        docColDetailPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        docPaneDetailContainer.setVisible(false);

        docHistoryTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null){
                docPaneDetailContainer.setVisible(true);
                docPaneDetailContainer.setVisible(true);
                loadDocPrescriptionDetail(newVal.getRecordID());
            }
        });

        patientsButtonOnAction(null);
    }

    private void setupLogo() {
        try {
            Image shieldImage = new Image(getClass().getResource("/images/logo-hospital.png").toString());
            brandingImageView.setImage(shieldImage);
        } catch (Exception e) {
            System.err.println("Failed to load logo image: " + e.getMessage());
        }
    }

    public void patientsButtonOnAction(ActionEvent event) {
        panePatientList.setVisible(true);
        paneExamination.setVisible(false);
        paneProfile.setVisible(false);
        paneAppointment.setVisible(false);
        paneHistory.setVisible(false);
        loadUserList();
    }

    public void informationButtonOnAction(ActionEvent event) {
        paneProfile.setVisible(true);
        panePatientList.setVisible(false);
        paneExamination.setVisible(false);
        paneAppointment.setVisible(false);
        paneHistory.setVisible(false);
    }

    public void appointmentButtonOnAction(ActionEvent event) {
        panePatientList.setVisible(false);
        paneExamination.setVisible(false);
        paneProfile.setVisible(false);
        paneAppointment.setVisible(true);
        paneHistory.setVisible(false);
        loadAppointments();
    }

    public void backToPatientList(ActionEvent event) {
        paneHistory.setVisible(false);
        panePatientList.setVisible(true);
        loadUserList();
    }

    public void closeButtonOnAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadUserList(){
        VboxDashboard.getChildren().clear();
        String sql = "SELECT id_user, firstname_user, lastname_user FROM user_account";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int accountID = rs.getInt("id_user");
                String fullName = rs.getString("firstname_user" ) + " " +rs.getString("lastname_user");
                HBox userEntry = createUserEntry(accountID, fullName);
                VboxDashboard.getChildren().add(userEntry);
            }
            connectionDB.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void searchIDOnAction(ActionEvent event){
        String id = searchIDTextField.getText().trim().toLowerCase();
        VboxDashboard.getChildren().clear();
        if(id.isEmpty()){
            loadUserList();
            return;
        }
        String sql = "SELECT id_user, firstname_user, lastname_user FROM user_account WHERE id_user LIKE ? ";
        try {
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ps.setString(1,"%" +id+ "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int accountID = rs.getInt("id_user");
                String fullname = rs.getString("firstname_user") + " " + rs.getString("lastname_user");
                HBox userEntry = createUserEntry(accountID, fullname);
                VboxDashboard.getChildren().add(userEntry);
            }
            rs.close();
            ps.close();
            connectionDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performSearch(String keyword){
        String searchKey = keyword.trim();
        VboxDashboard.getChildren().clear();
        if(keyword.isEmpty()){
            loadUserList();
            return;
        }
        String sqlSearch = "SELECT id_user, firstname_user, lastname_user FROM user_account WHERE id_user LIKE ? OR CONCAT(firstname_user,' ', lastname_user) LIKE ?";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sqlSearch);

            String searchPattern = "%" + searchKey + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ResultSet rs = ps.executeQuery();
            boolean flag = false;
            while (rs.next()){
                flag = true;
                int accountID = rs.getInt("id_user");
                String fullname = rs.getString("firstname_user") + " " + rs.getString("lastname_user");
                HBox userEntry = createUserEntry(accountID, fullname);
                VboxDashboard.getChildren().add(userEntry);
            }
            if(!flag){
                showAlert("Information", "No matching results found.", "error");
            }
            rs.close();
            ps.close();
            connectionDB.close();
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

        textContainer.setOnMouseClicked(e -> switchToExaminationView(ID));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button historyButton = new Button("View History");
        historyButton.setCursor(Cursor.HAND);
        historyButton.setUserData(ID);

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

        historyButton.setStyle(btnNormalStyle);

        historyButton.setOnMouseEntered(e -> historyButton.setStyle(btnHoverStyle));
        historyButton.setOnMouseExited(e -> historyButton.setStyle(btnNormalStyle));

        historyButton.setOnAction(e -> {
            int selectedId = (int) historyButton.getUserData();
            switchToHistoryView(selectedId, name);
        });

        userCard.getChildren().addAll(textContainer, spacer, historyButton);

        HBox outerContainer = new HBox(userCard);
        HBox.setHgrow(userCard, Priority.ALWAYS);

        VBox.setMargin(outerContainer, new Insets(5, 15, 5, 15));

        return outerContainer;
    }

    private void switchToExaminationView(int patientID) {
        panePatientList.setVisible(false);
        paneExamination.setVisible(true);
        paneProfile.setVisible(false);
        paneAppointment.setVisible(false);
        paneHistory.setVisible(false);

        listItems.clear();
        DiagnoseTextField.clear();
        symptomsTextField.clear();
        lblTotalAmount.setText("0 VND");

        setPatientInfo(patientID);
    }

    public void setPatientInfo(int patient_id){
        this.selectedPatientId = patient_id;
        String sql = "SELECT * FROM information_user where id_user = ?";
        DataBaseConnection connection = new DataBaseConnection();
        try(Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql)) {
            ps.setInt(1, patient_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                lbFullname.setText(rs.getString("firstname_user") + " " + rs.getString("lastname_user"));
                lbBHYT.setText(rs.getString("BHYT"));
                lbGender.setText(rs.getString("gender"));
                lbAllergy.setText(rs.getString("allergy"));
                java.sql.Date dateSQL = rs.getDate("birthday");
                if (dateSQL != null){
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String strDate = formatter.format(dateSQL);
                    lbBirthDay.setText(strDate);
                } else {
                    lbBirthDay.setText("N/A");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setupPrescriptionTable() {
        colSTT.setCellValueFactory(new PropertyValueFactory<>("stt"));
        colName.setCellValueFactory(new PropertyValueFactory<>("nameDrug"));
        colQuanlity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        prescriptionTableView.setItems(listItems);
    }

    public void loadDrugToComboBox(){
        String sql = "SELECT * FROM drug";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            masterData.clear();
            while(rs.next()){
                int id = rs.getInt("id_drug");
                String name = rs.getString("name_drug");
                String unit = rs.getString("unit");
                double price = rs.getDouble("price_drug");
                masterData.add(new Drug(id, name, price, unit));
            }
            connectionDB.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setupSearchableComboBox(ComboBox<Drug> comboBox, ObservableList<Drug> list) {
        this.filteredData = new FilteredList<>(list, p -> true);
        comboBox.setItems(filteredData);
        comboBox.setEditable(true);
        comboBox.setConverter(new javafx.util.StringConverter<Drug>() {
            @Override
            public String toString(Drug drug) {
                return (drug == null) ? null : drug.getName_drug();
            }
            @Override
            public Drug fromString(String string) {
                return comboBox.getItems().stream()
                        .filter(drug -> drug.getName_drug().equalsIgnoreCase(string))
                        .findFirst().orElse(null);
            }
        });

        comboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = comboBox.getEditor();
            final Drug selected = comboBox.getSelectionModel().getSelectedItem();
            if (selected == null || !selected.getName_drug().equals(editor.getText())) {
                filteredData.setPredicate(item -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return item.getName_drug().toLowerCase().contains(lowerCaseFilter);
                });
                if (!comboBox.isShowing()) {
                    comboBox.show();
                }
            }
        });
    }

    public void addButtonOnAction(ActionEvent event) {
        Object value = drugComboBox.getValue();
        Drug selectedDrug = null;
        if (value instanceof Drug) {
            selectedDrug = (Drug) value;
        } else if (value instanceof String) {
            String textInput = (String) value;
            for (Drug d : masterData) {
                if (d.getName_drug().equalsIgnoreCase(textInput)) {
                    selectedDrug = d;
                    break;
                }
            }
        }
        if (selectedDrug == null) {
            showAlert("Error", "Drug not found in the system! Please select from the list.", "error");
            return;
        }
        int quantity = 0;
        try {
            quantity = Integer.parseInt(quantityTextField.getText());
            if (quantity <= 0) return;
        } catch (NumberFormatException e) {
            return;
        }

        int stt = listItems.size() + 1;
        PrescriptionItem item = new PrescriptionItem(
                stt,
                selectedDrug.getName_drug(),
                selectedDrug.getUnit(),
                quantity,
                selectedDrug.getPrice_drug()
        );
        listItems.add(item);
        double tempTotal = 0;
        for (PrescriptionItem i : listItems) {
            tempTotal += i.getTotalAmount();
        }
        lblTotalAmount.setText(String.format("%,.0f VND", tempTotal));
        quantityTextField.clear();
        drugComboBox.setValue(null);
        drugComboBox.getEditor().clear();
    }

    public void payButtonOnAction(ActionEvent event){
        if(listItems.isEmpty()){
            return;
        }
        double tempTotal = 0;
        for(PrescriptionItem i : listItems){
            tempTotal += i.getTotalAmount();
        }
        final double finalTotal = tempTotal;
        generateQRCode(finalTotal);
        PanePayQR.setVisible(true);
    }

    public void generateQRCode(double totalAmount) {
        String bankId = "MB";
        String accountNo = "0337616003";
        String template = "compact";
        String content = "payment";
        long amount = (long) totalAmount;
        String url = String.format("https://img.vietqr.io/image/%s-%s-%s.png?amount=%d&addInfo=%s",
                bankId, accountNo, template, amount, content);
        Image image = new Image(url, true);
        image.errorProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                System.out.println("Failed to load QR image: " + image.getException());
            }
        });
        QR.setImage(image);
    }

    public void cancelPayQR(ActionEvent e){
        PanePayQR.setVisible(false);
    }

    public void btnMarkPaid(ActionEvent e){
        String sql = "INSERT INTO medical_records (id_user, symptoms, diagnosis, total_amount, visitDate, id_doctor) values (?, ?, ?, ?, NOW(), ?) ";
        DataBaseConnection connectionDB = new DataBaseConnection();
        Connection connection = null;
        ResultSet rsKeys = null;
        PreparedStatement psRecord = null;
        PreparedStatement psDetail = null;
        try{
            connection = connectionDB.getConnection();
            connection.setAutoCommit(false);
            psRecord = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            psRecord.setInt(1, this.selectedPatientId);
            psRecord.setString(2, symptomsTextField.getText());
            psRecord.setString(3, DiagnoseTextField.getText());

            double total = 0;
            for(PrescriptionItem i : listItems){
                total += i.getTotalAmount();
            }
            psRecord.setDouble(4, total);
            psRecord.setInt(5, this.currentDoctorId);
            int affectedRows = psRecord.executeUpdate();
            if(affectedRows == 0){
                connection.rollback();
                showAlert("Error","Failed to save medical record.","error");
                return;
            }
            rsKeys = psRecord.getGeneratedKeys();
            if (rsKeys.next()){
                int idRecord = rsKeys.getInt(1);
                String sqlDetail = "INSERT INTO prescription_details (record_id, name_drug, quantity, unit, price) VALUES (?, ?, ?, ?, ?)";
                psDetail = connection.prepareStatement(sqlDetail);
                for(PrescriptionItem i : listItems){
                    psDetail.setInt(1, idRecord);
                    psDetail.setString(2, i.getNameDrug());
                    psDetail.setInt(3,i.getQuantity());
                    psDetail.setString(4,i.getUnit());
                    psDetail.setDouble(5, i.getPrice());
                    psDetail.addBatch();
                }
                psDetail.executeBatch();
                connection.commit();

                PanePayQR.setVisible(false);
                listItems.clear();
                symptomsTextField.clear();
                DiagnoseTextField.clear();
                lblTotalAmount.setText("0 VND");
                showAlert("Success","Payment and medical record saved successfully!", "success");
            } else {
                connection.rollback();
                showAlert("Error","Failed to retrieve medical record ID.", "error");
            }
        }catch(Exception exception){
            exception.printStackTrace();
            try{
                if(connection != null) connection.rollback();
            }catch(Exception exception1){
                exception1.printStackTrace();
            }
        }finally {
            try {
                if (rsKeys != null) rsKeys.close();
                if (psRecord != null) psRecord.close();
                if (psDetail != null) psDetail.close();
                if (connection != null) connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void switchToHistoryView(int patientID, String patientName){
        panePatientList.setVisible(false);
        paneExamination.setVisible(false);
        paneProfile.setVisible(false);
        paneAppointment.setVisible(false);
        paneHistory.setVisible(true);

        docPaneDetailContainer.setVisible(false);
        docTableDetailHistory.getItems().clear();

        loadPatientHistoryForDoctor(patientID);
    }

    public void loadPatientHistoryForDoctor(int patientID){
        ObservableList<MedicalHistoryModel> HistoryListPatient = FXCollections.observableArrayList();

        HistoryListPatient.clear();
        String sql = "SELECT M.record_id, M.visitDate, M.diagnosis, D.firstname_doctor, D.lastname_doctor, M.total_amount\n" +
                "FROM medical_records M INNER jOIN doctor_account D ON M.id_doctor = D.id_doctor WHERE id_user = ? ORDER BY M.visitDate DESC";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ps.setInt(1, patientID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int id = rs.getInt("record_id");
                String diagnosis = rs.getString("diagnosis");
                String nameDoctor = rs.getString("firstname_doctor") +" "+ rs.getString("lastname_doctor");
                double total_amount = rs.getDouble("total_amount");
                java.sql.Timestamp timestamp = rs.getTimestamp("visitDate");
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                String dateStr = sdf.format(timestamp);
                HistoryListPatient.add( new MedicalHistoryModel(id, dateStr, diagnosis, nameDoctor, total_amount));
            }
            docHistoryTableView.setItems(HistoryListPatient);
            connectionDB.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void loadDocPrescriptionDetail(int recordID){
        ObservableList<PrescriptionDetailModel> detailList = FXCollections.observableArrayList();
        detailList.clear();
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

                detailList.add(new PrescriptionDetailModel(drugName, quantity, unit, price));
            }
            docTableDetailHistory.setItems(detailList);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void loadAppointments(){
        appointmentList.clear();
        String sql = "SELECT U.firstname_user, U.lastname_user, A.id_appointment, A.appointment_date, A.appointment_time, A.reason, A.status " +
                "FROM user_account U INNER JOIN appointments A ON U.id_user=A.id_user " +
                "WHERE A.id_doctor = ? AND A.status IN('Confirmed', 'Pending') " +
                "ORDER BY A.appointment_date ASC, A.appointment_time ASC";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ps.setInt(1, this.currentDoctorId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id_appointment");
                java.sql.Date dateSQL = rs.getDate("appointment_date");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dateStr = "";
                if (dateSQL != null) {
                    dateStr = dateFormat.format(dateSQL);
                }
                String time = rs.getString("appointment_time");
                String namePatient = rs.getString("firstname_user") + " " + rs.getString("lastname_user");
                String reason = rs.getString("reason");
                String status = rs.getString("status");
                appointmentList.add(new AppointmentModel(id, dateStr, time, namePatient, reason, status));
            }
            connectionDB.close();
        }catch(Exception E){
            E.printStackTrace();
        }
    }

    public void setAppointmentTable(){
        colAppDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colAppTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colAppPatient.setCellValueFactory(new PropertyValueFactory<>("namePatient"));
        colAppReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colAppStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        Callback<TableColumn<AppointmentModel, String>, TableCell<AppointmentModel, String>> cellFactory = (param) -> {
            return new TableCell<AppointmentModel, String>(){
                @Override
                public void updateItem(String item, boolean empty){
                    super.updateItem(item, empty);
                    if(empty){
                        setGraphic(null);
                        setText(null);
                    }else{
                        Button btDone = new Button("✓");
                        btDone.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

                        Button btLate = new Button("X");
                        btLate.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

                        AppointmentModel currentItem = getTableView().getItems().get(getIndex());

                        btDone.setOnAction(event -> {
                            processAppointment(currentItem.getId(),"Completed", null);
                            appointmentList.remove(currentItem);
                        });
                        btLate.setOnAction(event -> {
                            processAppointment(currentItem.getId(),"Cancelled", "Late arrival");
                            appointmentList.remove(currentItem);
                        });

                        HBox buttons = new HBox(10, btDone, btLate);
                        buttons.setStyle("-fx-alignment: CENTER;");
                        setGraphic(buttons);
                        setText(null);
                    }
                }
            };
        };
        colAppAction.setCellFactory(cellFactory);
        appointmentTableView.setItems(appointmentList);
    }

    public void processAppointment(int appointmentId, String newStatus, String cancelReason){
        String sql;
        if(cancelReason != null) {
            sql = "UPDATE appointments set status = ?, reason = ? WHERE id_appointment = ?";
        }else{
            sql = "UPDATE appointments SET status = ? WHERE id_appointment = ?";
        }
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps= connectionDB.prepareStatement(sql);
            ps.setString(1, newStatus);

            if(cancelReason != null){
                ps.setString(2,cancelReason);
                ps.setInt(3,appointmentId);
            }else{
                ps.setInt(2,appointmentId);
            }

            ps.executeUpdate();
            connectionDB.close();
        }catch(Exception e){
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage(), "error");
        }
    }

    @FXML
    public void handleMarkBusy(ActionEvent event) {
        if(busyTimeComboBox.getValue() == null || busyDatePicker.getValue() == null){
            showAlert("Error", "Please select date and time!", "error");
            return;
        }
        LocalDate date = busyDatePicker.getValue();
        String time = busyTimeComboBox.getValue();
        if (isSlotBooked(this.currentDoctorId, date, time)) {
            showAlert("Error", "This slot is already booked by a patient!", "error");
            return;
        }
        String sql = "INSERT INTO appointments (id_doctor, id_user, appointment_date, appointment_time, reason, status) VALUES (?, NULL, ?, ?, 'Doctor Busy', 'Busy')";

        try {
            DataBaseConnection connection = new DataBaseConnection();
            Connection conn = connection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, this.currentDoctorId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setString(3, time);

            ps.executeUpdate();
            conn.close();

            showAlert("Success", "Schedule blocked successfully.", "success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObservableList<String> getAvailableTimeSlots(){
        ObservableList<String> times = FXCollections.observableArrayList();
        times.add("7:30");
        for(int i = 8; i < 11; i++){
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
                int count = rs.getInt(1);
                connectionDB.close();
                return count > 0;
            }
            connectionDB.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void loadDoctorDisplayName(int id){
        String sql = "SELECT * FROM doctor_account WHERE id_doctor = ?";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                String fullName = rs.getString("firstname_doctor" ) + " " +rs.getString("lastname_doctor");
                labelNameDoctor.setText(fullName +" - ID: "+ id);
            }
            connectionDB.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void loadDoctorProfile(int idDoctor){
        String sql = "SELECT * FROM information_doctor WHERE id_doctor = ?";
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Connection connectionDB = connection.getConnection();
            PreparedStatement ps = connectionDB.prepareStatement(sql);
            ps.setInt(1, idDoctor);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                genderField.setText(rs.getString("gender"));
                CCCDField.setText(rs.getString("CCCD"));
                nationalityField.setText(rs.getString("nationality"));
                phoneField.setText(rs.getString("phone"));
                ethnicGroupField.setText(rs.getString("ethnicGroup"));
                addressField.setText(rs.getString("address"));
                specialtyField.setText(rs.getString("specialty"));
                rankField.setText(rs.getString("rank"));
                licenseNoField.setText(rs.getString("licenseNo"));
                departmentField.setText(rs.getString("department"));
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
        loadDoctorProfile(this.currentDoctorId);
        setEditMode(false);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        updateButton.setVisible(true);
    }

    public void saveButtonOnAction(ActionEvent e){
        setEditMode(false);
        String updateSQL = "UPDATE information_doctor SET gender=?, CCCD=?, nationality=?, phone=?, ethnicGroup=?, address=?, specialty=?, `rank`=?, licenseNo=?, department=? WHERE id_doctor = ?";
        DataBaseConnection connection = new DataBaseConnection();
        Connection connectionDB = connection.getConnection();
        try(PreparedStatement preparedStatement = connectionDB.prepareStatement(updateSQL)){
            preparedStatement.setString(1,genderField.getText());
            preparedStatement.setString(2,CCCDField.getText());
            preparedStatement.setString(3,nationalityField.getText());
            preparedStatement.setString(4,phoneField.getText());
            preparedStatement.setString(5, ethnicGroupField.getText());
            preparedStatement.setString(6, addressField.getText());
            preparedStatement.setString(7,specialtyField.getText());
            preparedStatement.setString(8, rankField.getText());
            preparedStatement.setString(9, licenseNoField.getText());
            preparedStatement.setString(10, departmentField.getText());
            preparedStatement.setInt(11, this.currentDoctorId);
            preparedStatement.executeUpdate();
            connectionDB.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        updateButton.setVisible(true);
    }

    public void setEditMode(boolean isEditMode){
        genderField.setEditable(isEditMode);
        CCCDField.setEditable(isEditMode);
        nationalityField.setEditable(isEditMode);
        phoneField.setEditable(isEditMode);
        ethnicGroupField.setEditable(isEditMode);
        addressField.setEditable(isEditMode);
        specialtyField.setEditable(isEditMode);
        rankField.setEditable(isEditMode);
        departmentField.setEditable(isEditMode);
        licenseNoField.setEditable(isEditMode);
        updateButton.setDisable(isEditMode);
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