package com.example.hospitalmanager;

public class UserSession {
    private static int userId;
    public static void setUserId(int id) {
        userId = id;
    }
    public static int getUserId() {
        return userId;
    }

}
