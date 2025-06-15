package com.example.uasvolunteerhub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // Sesuaikan dengan database Anda
    private static final String URL = "jdbc:mysql://localhost:3306/volunteerhub"; // Nama database: volunteerhub
    private static final String USERNAME = "root"; // Username database
    private static final String PASSWORD = ""; // Password database (kosong untuk XAMPP default)

    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Make sure to add MySQL Connector/J to your project.", e);
        }
    }

    // Method untuk test koneksi
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}