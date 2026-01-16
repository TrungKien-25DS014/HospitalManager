package com.example.hospitalmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class Drug{
    @FXML
    private ComboBox<String> listDrug;
    private int id_drug;
    private String name_drug;
    private double price_drug;
    private String unit;
    public Drug(int id_drug, String name_drug, double price_drug, String unit){
        this.id_drug = id_drug;
        this.name_drug = name_drug;
        this.price_drug = price_drug;
        this.unit = unit;
    }

    public int getId_drug() {
        return this.id_drug;
    }
    public String getName_drug(){
        return this.name_drug;
    }
    public double getPrice_drug(){
        return this.price_drug;
    }
    public String getUnit(){
        return this.unit;
    }

    @Override
    public String toString(){
        return this.name_drug;
    }

}