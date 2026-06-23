package repository;

import database.DatabaseConfig;
import model.Beverage;
import model.Food;
import model.MenuItem;
import model.Order;
import model.OrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    public boolean saveOrder(Order order) {
        String insertOrder = "INSERT INTO orders (customer_name, total_price, payment_method, status, discount_amount, promo_code) VALUES (?, ?, ?, ?, ?, ?);";
        String insertOrderItem = "INSERT INTO order_items (order_id, menu_item_id, quantity, subtotal) VALUES (?, ?, ?, ?);";
        
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Mulai Transaksi

            // 1. Simpan header pesanan
            try (PreparedStatement pstmtOrder = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                pstmtOrder.setString(1, order.getCustomerName());
                pstmtOrder.setDouble(2, order.getTotalPrice());
                pstmtOrder.setString(3, order.getPaymentMethod());
                pstmtOrder.setString(4, order.getStatus());
                pstmtOrder.setDouble(5, order.getDiscountAmount());
                pstmtOrder.setString(6, order.getPromoCode());
                
                int affectedRows = pstmtOrder.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Gagal membuat data pesanan.");
                }

                // Ambil ID pesanan yang baru dibuat
                try (ResultSet generatedKeys = pstmtOrder.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        order.setId(orderId);
                        
                        // 2. Simpan setiap item pesanan
                        try (PreparedStatement pstmtItem = conn.prepareStatement(insertOrderItem)) {
                            for (OrderItem item : order.getOrderItems()) {
                                pstmtItem.setInt(1, orderId);
                                pstmtItem.setInt(2, item.getMenuItem().getId());
                                pstmtItem.setInt(3, item.getQuantity());
                                pstmtItem.setDouble(4, item.calculateSubtotal());
                                pstmtItem.addBatch();
                            }
                            pstmtItem.executeBatch();
                        }
                    } else {
                        throw new SQLException("Gagal mendapatkan ID pesanan.");
                    }
                }
            }

            conn.commit(); // Commit Transaksi jika sukses semua
            return true;
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan pesanan, melakukan rollback: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Gagal rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Gagal menutup koneksi: " + e.getMessage());
                }
            }
        }
    }

    public List<Order> getAllOrders() {
        return getOrdersWithFilter(null);
    }

    public List<Order> getOrdersByCustomer(String customerName) {
        return getOrdersWithFilter(customerName);
    }

    private List<Order> getOrdersWithFilter(String customerName) {
        List<Order> orders = new ArrayList<>();
        String sqlOrder = "SELECT * FROM orders";
        if (customerName != null) {
            sqlOrder += " WHERE customer_name = ?";
        }
        sqlOrder += " ORDER BY order_date DESC;";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmtOrder = conn.prepareStatement(sqlOrder)) {
            
            if (customerName != null) {
                pstmtOrder.setString(1, customerName);
            }

            try (ResultSet rsOrder = pstmtOrder.executeQuery()) {
                while (rsOrder.next()) {
                    int orderId = rsOrder.getInt("id");
                    String name = rsOrder.getString("customer_name");
                    String date = rsOrder.getString("order_date");
                    double total = rsOrder.getDouble("total_price");
                    String payMethod = rsOrder.getString("payment_method");
                    String status = rsOrder.getString("status");
                    double discount = rsOrder.getDouble("discount_amount");
                    String promo = rsOrder.getString("promo_code");

                    Order order = new Order(orderId, name, date, total, payMethod, status, discount, promo != null ? promo : "");
                    
                    // Ambil detail items untuk order ini
                    List<OrderItem> items = getOrderItems(orderId, conn);
                    order.setOrderItems(items);

                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data pesanan: " + e.getMessage());
        }
        return orders;
    }

    private List<OrderItem> getOrderItems(int orderId, Connection conn) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sqlItems = "SELECT oi.quantity, oi.subtotal, mi.id, mi.name, mi.price, mi.category, mi.detail "
                + "FROM order_items oi "
                + "JOIN menu_items mi ON oi.menu_item_id = mi.id "
                + "WHERE oi.order_id = ?;";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlItems)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int qty = rs.getInt("quantity");
                    int itemId = rs.getInt("id");
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    String cat = rs.getString("category");
                    String detail = rs.getString("detail");

                    MenuItem menuItem;
                    if ("FOOD".equalsIgnoreCase(cat)) {
                        menuItem = new Food(itemId, name, price, detail);
                    } else {
                        menuItem = new Beverage(itemId, name, price, detail);
                    }

                    items.add(new OrderItem(menuItem, qty));
                }
            }
        }
        return items;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?;";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error update order status: " + e.getMessage());
            return false;
        }
    }
 
    public model.SalesReport getSalesReport() {
        double totalRevenue = 0.0;
        double totalDiscounts = 0.0;
        int completedOrdersCount = 0;
        java.util.List<model.SalesReport.BestSeller> bestSellers = new java.util.ArrayList<>();
 
        String sqlSummary = "SELECT "
                + "SUM(total_price) AS revenue, "
                + "SUM(discount_amount) AS discounts, "
                + "COUNT(*) AS orders_count "
                + "FROM orders "
                + "WHERE status = 'COMPLETED';";
 
        String sqlBestSellers = "SELECT mi.name, SUM(oi.quantity) AS qty "
                + "FROM order_items oi "
                + "JOIN orders o ON oi.order_id = o.id "
                + "JOIN menu_items mi ON oi.menu_item_id = mi.id "
                + "WHERE o.status = 'COMPLETED' "
                + "GROUP BY mi.id, mi.name "
                + "ORDER BY qty DESC "
                + "LIMIT 5;";
 
        try (Connection conn = DatabaseConfig.getConnection()) {
            // 1. Get overall metrics
            try (PreparedStatement pstmtSummary = conn.prepareStatement(sqlSummary);
                 ResultSet rsSummary = pstmtSummary.executeQuery()) {
                if (rsSummary.next()) {
                    totalRevenue = rsSummary.getDouble("revenue");
                    totalDiscounts = rsSummary.getDouble("discounts");
                    completedOrdersCount = rsSummary.getInt("orders_count");
                }
            }
 
            // 2. Get top selling items
            try (PreparedStatement pstmtBest = conn.prepareStatement(sqlBestSellers);
                 ResultSet rsBest = pstmtBest.executeQuery()) {
                while (rsBest.next()) {
                    bestSellers.add(new model.SalesReport.BestSeller(
                        rsBest.getString("name"),
                        rsBest.getInt("qty")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat laporan penjualan: " + e.getMessage());
        }
 
        return new model.SalesReport(totalRevenue, totalDiscounts, completedOrdersCount, bestSellers);
    }
}
