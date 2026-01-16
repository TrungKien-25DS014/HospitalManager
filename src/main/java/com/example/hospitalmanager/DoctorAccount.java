package com.example.hospitalmanager;

public class DoctorAccount {
     private int idDoctor;
     private String nameDoctor;
     public DoctorAccount(int idDoctor, String nameDoctor){
         this.idDoctor=idDoctor;
         this.nameDoctor=nameDoctor;
     }

    public int getIdDoctor() {
        return idDoctor;
    }
    public String getNameDoctor(){
         return nameDoctor;
    }
    @Override
    public String toString(){
         return this.nameDoctor;
    }
}
