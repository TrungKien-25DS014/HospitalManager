package com.example.hospitalmanager;

public class PrescriptionDetailModel {
    private String drugName;
    private int quantity;
    private String unit;
    private double price;
    public PrescriptionDetailModel(String drugName, int quantity, String unit, double price){
        this.drugName=drugName;
        this.quantity=quantity;
        this.unit=unit;
        this.price=price;
    }
    public String getDrugName(){
        return drugName;
    }
    public int getQuantity(){
        return quantity;
    }
    public String getUnit(){
        return unit;
    }
    public double getPrice(){return price;}

}
