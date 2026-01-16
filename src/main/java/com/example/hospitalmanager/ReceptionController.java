package com.example.hospitalmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import javafx.geometry.Insets;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class ReceptionController implements Initializable {
    @FXML
    private ImageView brandingImageView;
    @FXML
    private VBox VboxDashboard;
    @FXML
    private TextField searchIDTextField;
    @FXML
    private AnchorPane dashboardUI;
    @FXML
    private AnchorPane addUI;
    @FXML
    private Label lbFullname, lbBirthDay, lbBHYT, lbGender, lbAllergy;
    private int selectedPatientId = -1;
    @FXML
    private TextField DiagnoseTextField, symptomsTextField;
    @FXML
    private ComboBox<Drug> drugComboBox;
    @FXML
    private TextField quantityTextField;
    @FXML
    private TableColumn<PrescriptionItem, Integer> colSTT;
    @FXML
    private TableColumn<PrescriptionItem, String> colName;
    @FXML
    private TableColumn<PrescriptionItem, Integer> colQuanlity;
    @FXML
    private TableColumn<PrescriptionItem, String> colUnit;
    @FXML
    private TableColumn<PrescriptionItem, Double> colPrice;
    @FXML
    private TableColumn<PrescriptionItem, Double> colTotalAmount;
    @FXML
    private TableView<PrescriptionItem> prescriptionTableView;

    private ObservableList<PrescriptionItem> listItems = FXCollections.observableArrayList();
    private ObservableList<Drug> masterData = FXCollections.observableArrayList();
    private FilteredList<Drug> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Image shieldImage = new Image(getClass().getResource("/images/logo-hospital.png").toString());
            brandingImageView.setImage(shieldImage);
        } catch (Exception e) {
            System.err.println("Không thể tải ảnh logo.jpg: " + e.getMessage());
        }

        colSTT.setCellValueFactory(new PropertyValueFactory<>("stt"));
        colName.setCellValueFactory(new PropertyValueFactory<>("nameDrug"));
        colQuanlity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        prescriptionTableView.setItems(listItems);
        setupSearchableComboBox(drugComboBox, masterData);

        loadUserList();
        loadDrugToComboBox();

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
            boolean found = false;
            while (rs.next()) {
                found = true;
                int accountID = rs.getInt("id_user");
                String fullname = rs.getString("firstname_user") + " " + rs.getString("lastname_user");
                HBox userEntry = createUserEntry(accountID, fullname);
                VboxDashboard.getChildren().add(userEntry);
            }
            if(!found){
                Label noResultLabel = new Label("NO ID: "+ id);
                VboxDashboard.getChildren().add(noResultLabel);
            }
            rs.close();
            ps.close();
            connectionDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
                    String strDate = formatter.format(dateSQL);
                    lbBirthDay.setText(strDate);
                }else {
                    lbBirthDay.setText("N/A");
                }

            }

        }catch(Exception e){
            e.printStackTrace();
        }
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
            System.out.println("Thuốc không tồn tại trong hệ thống! Vui lòng chọn từ danh sách.");
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
        quantityTextField.clear();
        drugComboBox.setValue(null);
        drugComboBox.getEditor().clear();
    }

    public void loadUserList(){
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
                VboxDashboard.getChildren().add(userEntry);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private HBox createUserEntry(int ID, String name) {
        Button nameButton = new Button(name + " - " + ID);
        nameButton.setUserData(ID);
        nameButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333; -fx-font-size: 16px;");
        ImageView iconUser = null;
        try {
            Image icon = new Image(getClass().getResource("/images/user-icon.png").toString());
            iconUser = new ImageView(icon);
            iconUser.setFitWidth(80);
            iconUser.setFitHeight(80);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không tìm thấy icon người dùng.");
        }

        nameButton.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #333333; " +
                "-fx-font-weight: bold; " +
                "-fx-underline: true; " +
                "-fx-font-size: 18px; " +
                "-fx-alignment: center-left;");
        nameButton.setMaxWidth(100);

        AnchorPane contentPane = new AnchorPane();
        contentPane.setMinHeight(110);
        contentPane.setPrefHeight(110);
        contentPane.setMaxHeight(110);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        shadow.setRadius(5);
        shadow.setOffsetX(0);
        shadow.setOffsetY(3);
        contentPane.setEffect(shadow);
        contentPane.setStyle("-fx-background-color: #F5F5E0; -fx-background-radius: 5;");

        contentPane.getChildren().add(iconUser);
        AnchorPane.setLeftAnchor(iconUser, 10.0);
        AnchorPane.setTopAnchor(iconUser, 10.0);

        contentPane.getChildren().add(nameButton);
        AnchorPane.setLeftAnchor(nameButton, 100.0);
        AnchorPane.setTopAnchor(nameButton, 10.0);
        AnchorPane.setRightAnchor(nameButton, 10.0);

        HBox userEntryContainer = new HBox();
        userEntryContainer.getChildren().add(contentPane);
        HBox.setHgrow(contentPane, Priority.ALWAYS);
        Insets margin = new Insets(5, 10, 10, 10);

        VBox.setMargin(userEntryContainer, margin);

        nameButton.setOnAction(e -> {
            int selectedId = (int) nameButton.getUserData();
            dashboardUI.setVisible(false);
            addUI.setVisible(true);
            setPatientInfo(ID);
        });

        return userEntryContainer;
    }
    public  void dashboardButtonOnAction(ActionEvent event){
        dashboardUI.setVisible(true);
        addUI.setVisible(false);
    }

}
