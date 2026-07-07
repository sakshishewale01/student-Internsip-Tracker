package db;

import java.sql.*;
import javax.swing.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/internship_tracker?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password"; // Change to your MySQL password
    
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "MySQL Driver not found!\nPlease add mysql-connector-j.jar to lib folder.", 
                "Driver Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Database Connection Failed!\n" + e.getMessage() + 
                "\n\nMake sure:\n1. MySQL is running\n2. Database 'internship_tracker' exists\n3. Password is correct in DBConnection.java", 
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}