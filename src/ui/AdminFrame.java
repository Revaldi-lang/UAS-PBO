package ui;

import model.Beverage;
import model.Food;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.User;
import repository.MenuRepository;
import repository.OrderRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AdminFrame extends JFrame {
    private User admin;
    private MenuRepository menuRepository;
    private OrderRepository orderRepository;

    private JTabbedPane tabbedPane;

    private JTable tblMenu;
    private DefaultTableModel menuModel;
    private JTextField txtMenuId, txtMenuName, txtMenuPrice, txtMenuDetail;
    private JComboBox<String> cbCategory;
    private JButton btnAddMenu, btnEditMenu, btnDeleteMenu, btnClearMenu;

    private JTable tblOrders;
    private DefaultTableModel orderModel;
    private JButton btnCompleteOrder, btnRefreshOrders, btnDetailOrder;

    // Colors
    private static final Color ACCENT      = new Color(79, 70, 229);
    private static final Color ACCENT_HOVER = new Color(67, 56, 202);
    private static final Color BG          = new Color(249, 250, 251);
    private static final Color TEXT_DARK   = new Color(17, 24, 39);
    private static final Color TEXT_MUTED  = new Color(107, 114, 128);
    private static final Color BORDER      = new Color(229, 231, 235);
    private static final Color SUCCESS     = new Color(5, 150, 105);
    private static final Color SUCCESS_H   = new Color(4, 120, 87);
    private static final Color WARNING     = new Color(217, 119, 6);
    private static final Color WARNING_H   = new Color(180, 83, 9);
    private static final Color DANGER      = new Color(220, 38, 38);
    private static final Color DANGER_H    = new Color(185, 28, 28);
    private static final Color NEUTRAL     = new Color(107, 114, 128);
    private static final Color NEUTRAL_H   = new Color(75, 85, 99);

    public AdminFrame(User admin) {
        this.admin = admin;
        this.menuRepository = new MenuRepository();
        this.orderRepository = new OrderRepository();
        initUI();
    }

    private void initUI() {
        setTitle("Admin Dashboard");
        setSize(1000, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(14, 24, 14, 24)
        ));

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("Admin Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);
        JLabel lblUser = new JLabel("Login sebagai " + admin.getUsername());
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUser.setForeground(TEXT_MUTED);
        titleGroup.add(lblTitle);
        titleGroup.add(lblUser);
        header.add(titleGroup, BorderLayout.WEST);

        JButton btnLogout = flatButton("Logout", DANGER, DANGER_H);
        btnLogout.setPreferredSize(new Dimension(85, 34));
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        JPanel rp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rp.setOpaque(false);
        rp.add(btnLogout);
        header.add(rp, BorderLayout.EAST);

        main.add(header, BorderLayout.NORTH);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        createMenuTab();
        createOrdersTab();
        main.add(tabbedPane, BorderLayout.CENTER);

        add(main);
        loadMenuData();
        loadOrdersData();
    }

    private void createMenuTab() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setBackground(BG);

        // Table card
        JPanel tableCard = wrapCard(new BorderLayout());
        String[] cols = {"ID", "Nama", "Harga", "Kategori", "Detail"};
        menuModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMenu = new JTable(menuModel);
        styleTable(tblMenu);
        tblMenu.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblMenu.getColumnModel().getColumn(0).setMaxWidth(60);
        tableCard.add(new JScrollPane(tblMenu), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        // Form card
        JPanel formCard = wrapCard(new GridBagLayout());
        formCard.setPreferredSize(new Dimension(280, 0));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx = 0;

        g.gridy = 0; g.insets = new Insets(0, 0, 16, 0);
        JLabel formTitle = new JLabel("Kelola Menu");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(TEXT_DARK);
        formCard.add(formTitle, g);

        g.gridy = 1; g.insets = new Insets(0, 0, 4, 0);
        formCard.add(fieldLabel("ID Menu"), g);
        txtMenuId = new JTextField();
        txtMenuId.setEditable(false);
        txtMenuId.setBackground(new Color(243, 244, 246));
        txtMenuId.setPreferredSize(new Dimension(0, 34));
        g.gridy = 2; g.insets = new Insets(0, 0, 12, 0);
        formCard.add(txtMenuId, g);

        g.gridy = 3; g.insets = new Insets(0, 0, 4, 0);
        formCard.add(fieldLabel("Nama Menu"), g);
        txtMenuName = new JTextField();
        txtMenuName.setPreferredSize(new Dimension(0, 34));
        txtMenuName.putClientProperty("JTextField.placeholderText", "Nama hidangan");
        g.gridy = 4; g.insets = new Insets(0, 0, 12, 0);
        formCard.add(txtMenuName, g);

        g.gridy = 5; g.insets = new Insets(0, 0, 4, 0);
        formCard.add(fieldLabel("Harga"), g);
        txtMenuPrice = new JTextField();
        txtMenuPrice.setPreferredSize(new Dimension(0, 34));
        txtMenuPrice.putClientProperty("JTextField.placeholderText", "Contoh: 25000");
        g.gridy = 6; g.insets = new Insets(0, 0, 12, 0);
        formCard.add(txtMenuPrice, g);

        g.gridy = 7; g.insets = new Insets(0, 0, 4, 0);
        formCard.add(fieldLabel("Kategori"), g);
        cbCategory = new JComboBox<>(new String[]{"FOOD", "BEVERAGE"});
        cbCategory.setPreferredSize(new Dimension(0, 34));
        g.gridy = 8; g.insets = new Insets(0, 0, 12, 0);
        formCard.add(cbCategory, g);

        g.gridy = 9; g.insets = new Insets(0, 0, 4, 0);
        formCard.add(fieldLabel("Detail Spesifikasi"), g);
        txtMenuDetail = new JTextField();
        txtMenuDetail.setPreferredSize(new Dimension(0, 34));
        txtMenuDetail.putClientProperty("JTextField.placeholderText", "Cth: Pedas Sedang");
        g.gridy = 10; g.insets = new Insets(0, 0, 20, 0);
        formCard.add(txtMenuDetail, g);

        // Buttons 2x2
        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        btnGrid.setOpaque(false);
        btnAddMenu = flatButton("Tambah", SUCCESS, SUCCESS_H);
        btnEditMenu = flatButton("Simpan Edit", WARNING, WARNING_H);
        btnDeleteMenu = flatButton("Hapus", DANGER, DANGER_H);
        btnClearMenu = flatButton("Clear", NEUTRAL, NEUTRAL_H);
        btnGrid.add(btnAddMenu);
        btnGrid.add(btnEditMenu);
        btnGrid.add(btnDeleteMenu);
        btnGrid.add(btnClearMenu);
        g.gridy = 11; g.insets = new Insets(0, 0, 0, 0);
        formCard.add(btnGrid, g);

        g.gridy = 12; g.weighty = 1.0;
        formCard.add(Box.createGlue(), g);

        panel.add(formCard, BorderLayout.EAST);
        tabbedPane.addTab("Kelola Menu", panel);

        // Events
        tblMenu.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = tblMenu.getSelectedRow();
                if (r != -1) {
                    txtMenuId.setText(menuModel.getValueAt(r, 0).toString());
                    txtMenuName.setText(menuModel.getValueAt(r, 1).toString());
                    txtMenuPrice.setText(menuModel.getValueAt(r, 2).toString());
                    cbCategory.setSelectedItem(menuModel.getValueAt(r, 3).toString());
                    txtMenuDetail.setText(menuModel.getValueAt(r, 4).toString());
                }
            }
        });
        btnAddMenu.addActionListener(e -> handleAddMenu());
        btnEditMenu.addActionListener(e -> handleEditMenu());
        btnDeleteMenu.addActionListener(e -> handleDeleteMenu());
        btnClearMenu.addActionListener(e -> clearMenuForm());
    }

    private void createOrdersTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setBackground(BG);

        JPanel tableCard = wrapCard(new BorderLayout());
        String[] cols = {"ID", "Pelanggan", "Tanggal", "Total Bayar", "Metode Bayar", "Status"};
        orderModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblOrders = new JTable(orderModel);
        styleTable(tblOrders);

        // Status badge renderer
        tblOrders.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                String s = v == null ? "" : v.toString();
                if ("COMPLETED".equals(s)) {
                    if (!sel) lbl.setForeground(SUCCESS);
                } else if ("PENDING".equals(s)) {
                    if (!sel) lbl.setForeground(WARNING);
                }
                return lbl;
            }
        });

        tableCard.add(new JScrollPane(tblOrders), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        ctrl.setOpaque(false);
        btnDetailOrder = flatButton("Lihat Detail", ACCENT, ACCENT_HOVER);
        btnDetailOrder.setPreferredSize(new Dimension(130, 36));
        btnCompleteOrder = flatButton("Selesaikan Pesanan", SUCCESS, SUCCESS_H);
        btnCompleteOrder.setPreferredSize(new Dimension(180, 36));
        btnRefreshOrders = flatButton("Refresh", NEUTRAL, NEUTRAL_H);
        btnRefreshOrders.setPreferredSize(new Dimension(100, 36));
        ctrl.add(btnDetailOrder);
        ctrl.add(btnCompleteOrder);
        ctrl.add(btnRefreshOrders);
        panel.add(ctrl, BorderLayout.SOUTH);

        tabbedPane.addTab("Pesanan Masuk", panel);

        btnDetailOrder.addActionListener(e -> showOrderDetail());
        btnCompleteOrder.addActionListener(e -> handleCompleteOrder());
        btnRefreshOrders.addActionListener(e -> loadOrdersData());
    }

    // --- Shared helpers ---

    private JPanel wrapCard(LayoutManager layout) {
        JPanel card = new JPanel(layout);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return card;
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(243, 244, 246));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(249, 250, 251));
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
    }

    private JButton flatButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 34));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    // --- Action Handlers ---

    private void loadMenuData() {
        menuModel.setRowCount(0);
        List<MenuItem> list = menuRepository.getAllMenuItems();
        for (MenuItem item : list) {
            menuModel.addRow(new Object[]{
                    item.getId(), item.getName(), item.getPrice(), item.getCategory(),
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
            MenuItem item = "FOOD".equals(category) ? new Food(0, name, price, detail) : new Beverage(0, name, price, detail);
            if (menuRepository.addMenuItem(item)) {
                JOptionPane.showMessageDialog(this, "Menu berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadMenuData(); clearMenuForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan menu.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEditMenu() {
        String idStr = txtMenuId.getText();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih menu dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
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
            MenuItem item = "FOOD".equals(category) ? new Food(id, name, price, detail) : new Beverage(id, name, price, detail);
            if (menuRepository.updateMenuItem(item)) {
                JOptionPane.showMessageDialog(this, "Menu berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadMenuData(); clearMenuForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate menu.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteMenu() {
        String idStr = txtMenuId.getText();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih menu dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus menu ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(idStr);
            if (menuRepository.deleteMenuItem(id)) {
                JOptionPane.showMessageDialog(this, "Menu berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadMenuData(); clearMenuForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus menu.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearMenuForm() {
        txtMenuId.setText(""); txtMenuName.setText("");
        txtMenuPrice.setText(""); txtMenuDetail.setText("");
        cbCategory.setSelectedIndex(0); tblMenu.clearSelection();
    }

    private void loadOrdersData() {
        orderModel.setRowCount(0);
        List<Order> list = orderRepository.getAllOrders();
        for (Order order : list) {
            orderModel.addRow(new Object[]{
                    order.getId(), order.getCustomerName(), order.getOrderDate(),
                    "Rp" + String.format("%,.0f", order.getTotalPrice()),
                    order.getPaymentMethod(), order.getStatus()
            });
        }
    }

    private void handleCompleteOrder() {
        int row = tblOrders.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan dari tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int orderId = (int) orderModel.getValueAt(row, 0);
        String status = orderModel.getValueAt(row, 5).toString();
        if ("COMPLETED".equals(status)) {
            JOptionPane.showMessageDialog(this, "Pesanan ini sudah selesai!", "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (orderRepository.updateOrderStatus(orderId, "COMPLETED")) {
            JOptionPane.showMessageDialog(this, "Pesanan diselesaikan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadOrdersData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengubah status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showOrderDetail() {
        int row = tblOrders.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan dari tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) orderModel.getValueAt(row, 0);

        // Find the order from repository (it already loads items)
        List<Order> allOrders = orderRepository.getAllOrders();
        Order selectedOrder = null;
        for (Order o : allOrders) {
            if (o.getId() == orderId) { selectedOrder = o; break; }
        }
        if (selectedOrder == null) return;

        // Build the detail dialog
        JDialog dialog = new JDialog(this, "Detail Pesanan #" + orderId, true);
        dialog.setSize(520, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        // ---- Top: Order info ----
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(3, 0, 3, 12);
        g.anchor = GridBagConstraints.WEST;

        JLabel dlgTitle = new JLabel("Detail Pesanan #" + orderId);
        dlgTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dlgTitle.setForeground(TEXT_DARK);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2; g.insets = new Insets(0, 0, 12, 0);
        infoPanel.add(dlgTitle, g);
        g.gridwidth = 1;

        // Row: Pelanggan
        g.gridx = 0; g.gridy = 1; g.insets = new Insets(3, 0, 3, 12);
        infoPanel.add(infoLabel("Pelanggan"), g);
        g.gridx = 1;
        infoPanel.add(infoValue(selectedOrder.getCustomerName()), g);

        // Row: Status
        g.gridx = 0; g.gridy = 2;
        infoPanel.add(infoLabel("Status"), g);
        JLabel lblStatus = infoValue(selectedOrder.getStatus());
        lblStatus.setForeground("COMPLETED".equals(selectedOrder.getStatus()) ? SUCCESS : WARNING);
        g.gridx = 1;
        infoPanel.add(lblStatus, g);

        // Row: Metode Bayar
        g.gridx = 0; g.gridy = 3;
        infoPanel.add(infoLabel("Metode Bayar"), g);
        g.gridx = 1;
        infoPanel.add(infoValue(selectedOrder.getPaymentMethod()), g);

        // Row: Total
        g.gridx = 0; g.gridy = 4;
        infoPanel.add(infoLabel("Total Bayar"), g);
        JLabel lblTotalVal = infoValue("Rp " + String.format("%,.0f", selectedOrder.getTotalPrice()));
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g.gridx = 1;
        infoPanel.add(lblTotalVal, g);

        // Row: Waktu Order
        g.gridx = 0; g.gridy = 5;
        infoPanel.add(infoLabel("Waktu Order"), g);
        g.gridx = 1;
        String orderDate = selectedOrder.getOrderDate();
        infoPanel.add(infoValue(orderDate != null ? orderDate : "-"), g);

        // Row: Waktu Selesai
        g.gridx = 0; g.gridy = 6;
        infoPanel.add(infoLabel("Waktu Selesai"), g);
        g.gridx = 1;
        String completedDate = selectedOrder.getCompletedDate();
        infoPanel.add(infoValue(completedDate != null ? completedDate : "Belum selesai"), g);

        content.add(infoPanel, BorderLayout.NORTH);

        // ---- Center: Items table ----
        String[] itemCols = {"Nama Menu", "Harga", "Qty", "Subtotal"};
        DefaultTableModel itemModel = new DefaultTableModel(itemCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (OrderItem oi : selectedOrder.getOrderItems()) {
            itemModel.addRow(new Object[]{
                    oi.getMenuItem().getName(),
                    "Rp " + String.format("%,.0f", oi.getMenuItem().getPrice()),
                    oi.getQuantity(),
                    "Rp " + String.format("%,.0f", oi.calculateSubtotal())
            });
        }
        JTable tblItems = new JTable(itemModel);
        styleTable(tblItems);
        tblItems.setRowHeight(30);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        tblItems.getColumnModel().getColumn(2).setCellRenderer(center);

        JScrollPane scrollItems = new JScrollPane(tblItems);
        scrollItems.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        content.add(scrollItems, BorderLayout.CENTER);

        // ---- Bottom: Close button ----
        JButton btnClose = flatButton("Tutup", NEUTRAL, NEUTRAL_H);
        btnClose.setPreferredSize(new Dimension(100, 36));
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnClose);
        content.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private JLabel infoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    private JLabel infoValue(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_DARK);
        return lbl;
    }
}
