package ui;

import model.Admin;
import model.Beverage;
import model.Food;
import model.MenuItem;
import model.Order;
import model.User;
import repository.MenuRepository;
import repository.OrderRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AdminFrame extends JFrame {
    private User admin;
    private MenuRepository menuRepository;
    private OrderRepository orderRepository;

    private JTabbedPane tabbedPane;
    
    // Components for Menu Panel
    private JTable tblMenu;
    private DefaultTableModel menuModel;
    private JTextField txtMenuId, txtMenuName, txtMenuPrice, txtMenuDetail;
    private JComboBox<String> cbCategory;
    private JButton btnAddMenu, btnEditMenu, btnDeleteMenu, btnClearMenu;

    // Components for Order Panel
    private JTable tblOrders;
    private DefaultTableModel orderModel;
    private JButton btnCompleteOrder, btnRefreshOrders;

    public AdminFrame(User admin) {
        this.admin = admin;
        this.menuRepository = new MenuRepository();
        this.orderRepository = new OrderRepository();
        initUI();
    }

    private void initUI() {
        setTitle("Admin Dashboard - Food Ordering System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 246, 248));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 58, 64)); // Dark slate gray
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("ADMINISTRATOR CONTROL PANEL");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Login sebagai: " + admin.getUsername());
        lblWelcome.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblWelcome.setForeground(new Color(206, 212, 218));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle);
        titlePanel.add(lblWelcome);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 53, 69)); // Bootstrap Danger Red
        btnLogout.setForeground(Color.BLACK);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        createMenuTab();
        createOrdersTab();

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);

        // Load Initial Data
        loadMenuData();
        loadOrdersData();
    }

    private void createMenuTab() {
        JPanel menuPanel = new JPanel(new BorderLayout(15, 0));
        menuPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        menuPanel.setBackground(Color.WHITE);

        // LEFT: Table
        String[] menuColumns = {"ID", "Nama Menu", "Harga", "Kategori", "Detail (Pedas/Suhu/Size)"};
        menuModel = new DefaultTableModel(menuColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblMenu = new JTable(menuModel);
        tblMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblMenu.setRowHeight(22);
        JScrollPane scrollPane = new JScrollPane(tblMenu);
        menuPanel.add(scrollPane, BorderLayout.CENTER);

        // RIGHT: Input Form & CRUD Operations
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(248, 249, 250));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        formPanel.setPreferredSize(new Dimension(320, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        // Title
        JLabel lblFormTitle = new JLabel("Form Kelola Menu");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(33, 37, 41));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblFormTitle, gbc);
        gbc.gridwidth = 1;

        // ID (Read-only)
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("ID Menu:"), gbc);
        txtMenuId = new JTextField();
        txtMenuId.setEditable(false);
        txtMenuId.setBackground(new Color(233, 236, 239));
        gbc.gridx = 1;
        formPanel.add(txtMenuId, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Nama Menu:"), gbc);
        txtMenuName = new JTextField();
        gbc.gridx = 1;
        formPanel.add(txtMenuName, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Harga:"), gbc);
        txtMenuPrice = new JTextField();
        gbc.gridx = 1;
        formPanel.add(txtMenuPrice, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Kategori:"), gbc);
        cbCategory = new JComboBox<>(new String[]{"FOOD", "BEVERAGE"});
        gbc.gridx = 1;
        formPanel.add(cbCategory, gbc);

        // Detail
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Detail Spec:"), gbc);
        txtMenuDetail = new JTextField();
        txtMenuDetail.setToolTipText("Contoh: Pedas Sedang (Food) atau Large / Ice (Beverage)");
        gbc.gridx = 1;
        formPanel.add(txtMenuDetail, gbc);

        // Buttons Panel inside Form
        JPanel btnFormPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnFormPanel.setOpaque(false);

        btnAddMenu = new JButton("Tambah");
        btnAddMenu.setBackground(new Color(40, 167, 69));
        btnAddMenu.setForeground(Color.BLACK);

        btnEditMenu = new JButton("Simpan Edit");
        btnEditMenu.setBackground(new Color(255, 193, 7));
        btnEditMenu.setForeground(Color.BLACK);

        btnDeleteMenu = new JButton("Hapus");
        btnDeleteMenu.setBackground(new Color(220, 53, 69));
        btnDeleteMenu.setForeground(Color.BLACK);

        btnClearMenu = new JButton("Clear");
        btnClearMenu.setBackground(new Color(108, 117, 125));
        btnClearMenu.setForeground(Color.BLACK);

        btnFormPanel.add(btnAddMenu);
        btnFormPanel.add(btnEditMenu);
        btnFormPanel.add(btnDeleteMenu);
        btnFormPanel.add(btnClearMenu);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        formPanel.add(btnFormPanel, gbc);

        // Dummy filler for spacing
        gbc.gridy = 7; gbc.weighty = 1.0;
        formPanel.add(new JLabel(""), gbc);

        menuPanel.add(formPanel, BorderLayout.EAST);
        tabbedPane.addTab("Kelola Menu Makanan & Minuman", menuPanel);

        // Events for Menu
        tblMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblMenu.getSelectedRow();
                if (row != -1) {
                    txtMenuId.setText(menuModel.getValueAt(row, 0).toString());
                    txtMenuName.setText(menuModel.getValueAt(row, 1).toString());
                    txtMenuPrice.setText(menuModel.getValueAt(row, 2).toString());
                    cbCategory.setSelectedItem(menuModel.getValueAt(row, 3).toString());
                    txtMenuDetail.setText(menuModel.getValueAt(row, 4).toString());
                }
            }
        });

        btnAddMenu.addActionListener(e -> handleAddMenu());
        btnEditMenu.addActionListener(e -> handleEditMenu());
        btnDeleteMenu.addActionListener(e -> handleDeleteMenu());
        btnClearMenu.addActionListener(e -> clearMenuForm());
    }

    private void createOrdersTab() {
        JPanel orderPanel = new JPanel(new BorderLayout(15, 15));
        orderPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        orderPanel.setBackground(Color.WHITE);

        // Table
        String[] orderColumns = {"ID Pesanan", "Pelanggan", "Tanggal Pesanan", "Total Bayar", "Metode Bayar", "Status"};
        orderModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblOrders = new JTable(orderModel);
        tblOrders.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblOrders.setRowHeight(22);
        JScrollPane scrollPane = new JScrollPane(tblOrders);
        orderPanel.add(scrollPane, BorderLayout.CENTER);

        // Control Panel
        JPanel ctrlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        ctrlPanel.setOpaque(false);

        btnCompleteOrder = new JButton("Selesaikan Pesanan (COMPLETED)");
        btnCompleteOrder.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCompleteOrder.setBackground(new Color(40, 167, 69));
        btnCompleteOrder.setForeground(Color.BLACK);

        btnRefreshOrders = new JButton("Refresh Daftar");
        btnRefreshOrders.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnRefreshOrders.setBackground(new Color(0, 123, 255));
        btnRefreshOrders.setForeground(Color.BLACK);

        ctrlPanel.add(btnCompleteOrder);
        ctrlPanel.add(btnRefreshOrders);
        orderPanel.add(ctrlPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Daftar Pesanan Masuk", orderPanel);

        // Events
        btnCompleteOrder.addActionListener(e -> handleCompleteOrder());
        btnRefreshOrders.addActionListener(e -> loadOrdersData());
    }

    // --- Action Handlers ---

    private void loadMenuData() {
        menuModel.setRowCount(0);
        List<MenuItem> list = menuRepository.getAllMenuItems();
        for (MenuItem item : list) {
            menuModel.addRow(new Object[]{
                    item.getId(),
                    item.getName(),
                    item.getPrice(),
                    item.getCategory(),
                    item.getDetailInfo().replace("Tingkat Kepedasan: ", "").replace("Suhu & Ukuran: ", "")
            });
        }
    }

    private void handleAddMenu() {
        String name = txtMenuName.getText().trim();
        String priceStr = txtMenuPrice.getText().trim();
        String category = cbCategory.getSelectedItem().toString();
        String detail = txtMenuDetail.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || detail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua form harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            MenuItem item;
            if ("FOOD".equals(category)) {
                item = new Food(0, name, price, detail);
            } else {
                item = new Beverage(0, name, price, detail);
            }

            if (menuRepository.addMenuItem(item)) {
                JOptionPane.showMessageDialog(this, "Menu berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadMenuData();
                clearMenuForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan menu ke database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka numerik!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEditMenu() {
        String idStr = txtMenuId.getText();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih menu yang akan diedit dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtMenuName.getText().trim();
        String priceStr = txtMenuPrice.getText().trim();
        String category = cbCategory.getSelectedItem().toString();
        String detail = txtMenuDetail.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || detail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua form harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            double price = Double.parseDouble(priceStr);
            MenuItem item;
            if ("FOOD".equals(category)) {
                item = new Food(id, name, price, detail);
            } else {
                item = new Beverage(id, name, price, detail);
            }

            if (menuRepository.updateMenuItem(item)) {
                JOptionPane.showMessageDialog(this, "Menu berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadMenuData();
                clearMenuForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate menu.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka numerik!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteMenu() {
        String idStr = txtMenuId.getText();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih menu yang akan dihapus dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus menu ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(idStr);
            if (menuRepository.deleteMenuItem(id)) {
                JOptionPane.showMessageDialog(this, "Menu berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadMenuData();
                clearMenuForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus menu.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearMenuForm() {
        txtMenuId.setText("");
        txtMenuName.setText("");
        txtMenuPrice.setText("");
        txtMenuDetail.setText("");
        cbCategory.setSelectedIndex(0);
        tblMenu.clearSelection();
    }

    private void loadOrdersData() {
        orderModel.setRowCount(0);
        List<Order> list = orderRepository.getAllOrders();
        for (Order order : list) {
            orderModel.addRow(new Object[]{
                    order.getId(),
                    order.getCustomerName(),
                    order.getOrderDate(),
                    "Rp" + String.format("%,.0f", order.getTotalPrice()),
                    order.getPaymentMethod(),
                    order.getStatus()
            });
        }
    }

    private void handleCompleteOrder() {
        int row = tblOrders.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) orderModel.getValueAt(row, 0);
        String currentStatus = orderModel.getValueAt(row, 5).toString();

        if ("COMPLETED".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Pesanan ini sudah selesai!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (orderRepository.updateOrderStatus(orderId, "COMPLETED")) {
            JOptionPane.showMessageDialog(this, "Pesanan berhasil diselesaikan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadOrdersData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengubah status pesanan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
