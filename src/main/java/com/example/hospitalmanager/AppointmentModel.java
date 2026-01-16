package com.example.hospitalmanager;

public class AppointmentModel {
    private int id;
    private String date;
    private String time;
    private String namePatient;
    private String reason;
    private String status;
    public AppointmentModel(int id, String date, String time, String namePatient, String reason, String status){
        this.id=id;
        this.date=date;
        this.time=time;
        this.namePatient=namePatient;
        this.reason=reason;
        this.status=status;
    }
    public int getId(){
        return this.id;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public String getNamePatient() {
        return namePatient;
    }
    public String getReason(){
        return reason;
    }
    public String getStatus(){
        return status;
    }
}
