package repository;

import database.DatabaseConfig;
import model.Promo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PromoRepository {

    public Promo getPromoByCode(String code) {
        if (code == null) return null;
        String sql = "SELECT * FROM promos WHERE UPPER(code) = ?;";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, code.toUpperCase().trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Promo(
                        rs.getString("code"),
                        rs.getDouble("discount_percent"),
                        rs.getDouble("max_discount"),
                        rs.getDouble("min_purchase")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat data promo: " + e.getMessage());
        }
        return null;
    }
}
