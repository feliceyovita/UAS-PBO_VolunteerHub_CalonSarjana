package com.example.uasvolunteerhub;

public class SessionManager {
    private static int userId;
    private static String userName;

    public static void setCurrentUser(int id, String name) {
        userId = id;
        userName = name;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getUserName() {
        return userName;
    }
}
