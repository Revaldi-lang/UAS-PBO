package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String DB_URL = "jdbc:sqlite:food_ordering.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver tidak ditemukan!");
            e.printStackTrace();
        }
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            
            // 1. Buat Tabel Users
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT UNIQUE NOT NULL,"
                    + "password TEXT NOT NULL,"
                    + "role TEXT NOT NULL" // ADMIN or CUSTOMER
                    + ");";
            stmt.execute(createUsersTable);

            // 2. Buat Tabel Menu Items
            String createMenuItemsTable = "CREATE TABLE IF NOT EXISTS menu_items ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT NOT NULL,"
                    + "price REAL NOT NULL,"
                    + "category TEXT NOT NULL," // FOOD or BEVERAGE
                    + "detail TEXT," // spiciness (food) or size/ice (beverage)
                    + "is_deleted INTEGER DEFAULT 0"
                    + ");";
            stmt.execute(createMenuItemsTable);

            // Jalankan migrasi kolom is_deleted jika database lama sudah terbentuk
            try {
                stmt.execute("ALTER TABLE menu_items ADD COLUMN is_deleted INTEGER DEFAULT 0;");
            } catch (SQLException e) {
                // Kolom mungkin sudah ada, abaikan error
            }

            // 3. Buat Tabel Orders
            String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "customer_name TEXT NOT NULL,"
                    + "order_date DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + "completed_date DATETIME,"
                    + "total_price REAL NOT NULL,"
                    + "payment_method TEXT NOT NULL," // CASH or E-WALLET
                    + "status TEXT NOT NULL DEFAULT 'PENDING'" // PENDING or COMPLETED
                    + ");";
            stmt.execute(createOrdersTable);

            // Migrasi kolom completed_date jika database lama sudah terbentuk
            try {
                stmt.execute("ALTER TABLE orders ADD COLUMN completed_date DATETIME;");
            } catch (SQLException e) {
                // Kolom mungkin sudah ada, abaikan error
            }

            // 4. Buat Tabel Order Items
            String createOrderItemsTable = "CREATE TABLE IF NOT EXISTS order_items ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "order_id INTEGER NOT NULL,"
                    + "menu_item_id INTEGER NOT NULL,"
                    + "quantity INTEGER NOT NULL,"
                    + "subtotal REAL NOT NULL,"
                    + "FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,"
                    + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)"
                    + ");";
            stmt.execute(createOrderItemsTable);

            // Seed data default jika tabel kosong
            seedDefaultData(stmt);

            System.out.println("Database berhasil diinisialisasi.");
        } catch (SQLException e) {
            System.err.println("Gagal menginisialisasi database: " + e.getMessage());
        }
    }

    private static void seedDefaultData(Statement stmt) throws SQLException {
        // Cek apakah user admin sudah ada
        java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users;");
        if (rs.next() && rs.getInt(1) == 0) {
            // Seed Admin & Customer default
            stmt.execute("INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN');");
            stmt.execute("INSERT INTO users (username, password, role) VALUES ('budi', 'budi123', 'CUSTOMER');");
            System.out.println("Default users seeded: admin/admin123 (ADMIN), budi/budi123 (CUSTOMER).");
        }
        rs.close();

        // Cek apakah menu sudah ada
        rs = stmt.executeQuery("SELECT COUNT(*) FROM menu_items;");
        if (rs.next() && rs.getInt(1) == 0) {
            // Seed Menu Makanan & Minuman
            stmt.execute("INSERT INTO menu_items (name, price, category, detail) VALUES ('Nasi Goreng Spesial', 25000, 'FOOD', 'Pedas Sedang');");
            stmt.execute("INSERT INTO menu_items (name, price, category, detail) VALUES ('Ayam Bakar Taliwang', 35000, 'FOOD', 'Sangat Pedas');");
            stmt.execute("INSERT INTO menu_items (name, price, category, detail) VALUES ('Sate Ayam Madura', 28000, 'FOOD', 'Tidak Pedas');");
            stmt.execute("INSERT INTO menu_items (name, price, category, detail) VALUES ('Es Teh Manis', 6000, 'BEVERAGE', 'Medium / Es');");
            stmt.execute("INSERT INTO menu_items (name, price, category, detail) VALUES ('Es Jeruk Peras', 8000, 'BEVERAGE', 'Large / Es');");
            stmt.execute("INSERT INTO menu_items (name, price, category, detail) VALUES ('Kopi Susu Gula Aren', 15000, 'BEVERAGE', 'Medium / Hangat');");
            System.out.println("Default menu items seeded.");
        }
        rs.close();
    }
}
