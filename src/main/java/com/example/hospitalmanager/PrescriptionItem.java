package com.example.hospitalmanager;

public class PrescriptionItem {
    private int stt;
    private String nameDrug;
    private int quantity;
    private double price;
    private double totalAmount;
    private String unit;

    public PrescriptionItem(int stt, String nameDrug, String unit, int quantity, double price) {
        this.stt = stt;
        this.nameDrug = nameDrug;
        this.unit = unit;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = price * quantity;
    }

    public int getStt() {
        return stt;
    }
    public String getNameDrug() {
        return nameDrug;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getUnit() {
        return unit;
    }
    public double getPrice() {
        return price;
    }
    public double getTotalAmount() {
        return totalAmount;
    }
}
