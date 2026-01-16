package com.example.hospitalmanager;

import java.sql.Date;

public class MedicalHistoryModel {
    private int recordID;
    private String visitDate;
    private String diagnosis;
    private String doctorName;
    private double totalAmount;
    public MedicalHistoryModel(int recordID, String visitDate, String diagnosis, String doctorName, double totalAmount){
        this.recordID=recordID;
        this.visitDate=visitDate;
        this.diagnosis=diagnosis;
        this.doctorName=doctorName;
        this.totalAmount=totalAmount;
    }

    public int getRecordID() {
        return recordID;
    }

    public String getVisitDate() {return visitDate;}
    public String getDiagnosis(){return diagnosis;}
    public String getDoctorName(){return doctorName;}
    public double getTotalAmount(){return totalAmount;}
}
