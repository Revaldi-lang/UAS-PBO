package repository;

import database.DatabaseConfig;
import model.Beverage;
import model.Food;
import model.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuRepository {

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        String query = "SELECT * FROM menu_items;";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
                String detail = rs.getString("detail");

                // Polymorphism: instantiate Food or Beverage based on DB category
                if ("FOOD".equalsIgnoreCase(category)) {
                    items.add(new Food(id, name, price, detail));
                } else if ("BEVERAGE".equalsIgnoreCase(category)) {
                    items.add(new Beverage(id, name, price, detail));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error get menu items: " + e.getMessage());
        }
        return items;
    }

    public boolean addMenuItem(MenuItem item) {
        String insert = "INSERT INTO menu_items (name, price, category, detail) VALUES (?, ?, ?, ?);";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insert)) {
            
            pstmt.setString(1, item.getName());
            pstmt.setDouble(2, item.getPrice());
            pstmt.setString(3, item.getCategory());
            pstmt.setString(4, item.getDetailInfo().replace("Tingkat Kepedasan: ", "").replace("Suhu & Ukuran: ", ""));
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error add menu item: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMenuItem(MenuItem item) {
        String update = "UPDATE menu_items SET name = ?, price = ?, category = ?, detail = ? WHERE id = ?;";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(update)) {
            
            pstmt.setString(1, item.getName());
            pstmt.setDouble(2, item.getPrice());
            pstmt.setString(3, item.getCategory());
            pstmt.setString(4, item.getDetailInfo().replace("Tingkat Kepedasan: ", "").replace("Suhu & Ukuran: ", ""));
            pstmt.setInt(5, item.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error update menu item: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMenuItem(int id) {
        String delete = "DELETE FROM menu_items WHERE id = ?;";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(delete)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error delete menu item: " + e.getMessage());
            return false;
        }
    }
}
