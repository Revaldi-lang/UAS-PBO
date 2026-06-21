package repository;

import database.DatabaseConfig;
import model.Admin;
import model.Customer;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public User login(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?;";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String dbUsername = rs.getString("username");
                    String dbPassword = rs.getString("password");
                    String role = rs.getString("role");

                    // Polymorphic construction based on role
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        return new Admin(id, dbUsername, dbPassword);
                    } else {
                        return new Customer(id, dbUsername, dbPassword);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat login: " + e.getMessage());
        }
        return null;
    }

    public boolean register(String username, String password) {
        String insert = "INSERT INTO users (username, password, role) VALUES (?, ?, 'CUSTOMER');";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insert)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            // Biasanya karena username UNIQUE constraint violation
            System.err.println("Error saat registrasi: " + e.getMessage());
            return false;
        }
    }
}
