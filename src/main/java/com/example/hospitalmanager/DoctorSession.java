package com.example.hospitalmanager;

public class DoctorSession {
    private static int idDoctor;
    public static void setDoctorId(int id) {
        idDoctor = id;
    }
    public static int getDoctorId() {
        return idDoctor;
    }
}
