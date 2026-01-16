package com.example.hospitalmanager;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataBaseConnection {
    public Connection databaseLink;
    static final String URL = "jdbc:mysql://localhost:3306/login_inte";
    private static final String databaseUser = "root";
    private static final String databasePassword = "Kien123@";
    public Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(URL, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return databaseLink;
    }
}

