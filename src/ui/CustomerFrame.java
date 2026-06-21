package ui;

import model.Customer;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.User;
import payment.CashPayment;
import payment.EWalletPayment;
import payment.Payment;
import repository.MenuRepository;
import repository.OrderRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CustomerFrame extends JFrame {
    private User customer;
    private MenuRepository menuRepository;
    private OrderRepository orderRepository;

    // Cart list
    private List<OrderItem> cartList;
    private double totalCartPrice = 0.0;

    private JTabbedPane tabbedPane;

    // Browse Menu Components
    private JTable tblMenu;
    private DefaultTableModel menuModel;
    private JComboBox<String> cbFilterCategory;
    private JSpinner spinQty;
    private JButton btnAddToCart;

    // Cart Components
    private JTable tblCart;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;
    private JButton btnRemoveItem;
    private JComboBox<String> cbPaymentMethod;
    
    // Payment detail panels (to be swapped)
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel pnlCash, pnlEWallet;
    
    // Cash inputs
    private JTextField txtCashAmount;
    
    // E-Wallet inputs
    private JComboBox<String> cbWalletProvider;
    private JTextField txtPhoneNumber;

    private JButton btnCheckout;

    // History Components
    private JTable tblHistory;
    private DefaultTableModel historyModel;
    private JButton btnRefreshHistory;

    public CustomerFrame(User customer) {
        this.customer = customer;
        this.menuRepository = new MenuRepository();
        this.orderRepository = new OrderRepository();
        this.cartList = new ArrayList<>();
        initUI();
    }

    private void initUI() {
        setTitle("Customer Portal - Food Ordering System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 246, 248));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 123, 255)); // Customer Blue Accent
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("FOOD ORDER SYSTEM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblWelcome = new JLabel(customer.getWelcomeMessage());
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblWelcome.setForeground(new Color(240, 244, 255));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle);
        titlePanel.add(lblWelcome);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 53, 69));
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

        createBrowseTab();
        createCartTab();
        createHistoryTab();

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);

        // Load data
        loadMenuData("Semua");
        loadHistoryData();
    }

    private void createBrowseTab() {
        JPanel browsePanel = new JPanel(new BorderLayout(15, 15));
        browsePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        browsePanel.setBackground(Color.WHITE);

        // Top Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterBar.setOpaque(false);
        filterBar.add(new JLabel("Kategori:"));
        cbFilterCategory = new JComboBox<>(new String[]{"Semua", "FOOD", "BEVERAGE"});
        filterBar.add(cbFilterCategory);

        browsePanel.add(filterBar, BorderLayout.NORTH);

        // Menu Table
        String[] columns = {"ID", "Nama Menu", "Harga", "Kategori", "Detail"};
        menuModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblMenu = new JTable(menuModel);
        tblMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblMenu.setRowHeight(22);
        JScrollPane scrollPane = new JScrollPane(tblMenu);
        browsePanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Order Action bar
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(new JLabel("Jumlah Porsi/Gelas:"));
        spinQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        actionPanel.add(spinQty);

        btnAddToCart = new JButton("Masukkan ke Keranjang");
        btnAddToCart.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAddToCart.setBackground(new Color(40, 167, 69));
        btnAddToCart.setForeground(Color.BLACK);
        actionPanel.add(btnAddToCart);

        browsePanel.add(actionPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Cari & Pesan Menu", browsePanel);

        // Events
        cbFilterCategory.addActionListener(e -> loadMenuData(cbFilterCategory.getSelectedItem().toString()));
        btnAddToCart.addActionListener(e -> handleAddToCart());
    }

    private void createCartTab() {
        JPanel cartPanel = new JPanel(new BorderLayout(15, 15));
        cartPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        cartPanel.setBackground(Color.WHITE);

        // Left side: Table representing current cart
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setOpaque(false);

        String[] columns = {"Nama Menu", "Harga", "Kuantitas", "Subtotal"};
        cartModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCart = new JTable(cartModel);
        tblCart.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblCart.setRowHeight(22);
        JScrollPane scrollCart = new JScrollPane(tblCart);
        tableContainer.add(scrollCart, BorderLayout.CENTER);

        // Remove item button
        btnRemoveItem = new JButton("Hapus Item Terpilih");
        btnRemoveItem.setBackground(new Color(220, 53, 69));
        btnRemoveItem.setForeground(Color.BLACK);
        tableContainer.add(btnRemoveItem, BorderLayout.SOUTH);

        cartPanel.add(tableContainer, BorderLayout.CENTER);

        // Right side: Checkout Summary & Payment options
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBackground(new Color(248, 249, 250));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        summaryPanel.setPreferredSize(new Dimension(350, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        // Total Label
        JLabel lblSummaryTitle = new JLabel("Ringkasan Belanja");
        lblSummaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        summaryPanel.add(lblSummaryTitle, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        summaryPanel.add(new JLabel("Total Harga:"), gbc);
        lblTotal = new JLabel("Rp0", JLabel.RIGHT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(new Color(0, 123, 255));
        gbc.gridx = 1;
        summaryPanel.add(lblTotal, gbc);

        // Payment Method Choice
        gbc.gridx = 0; gbc.gridy = 2;
        summaryPanel.add(new JLabel("Metode Bayar:"), gbc);
        cbPaymentMethod = new JComboBox<>(new String[]{"CASH", "E-WALLET"});
        gbc.gridx = 1;
        summaryPanel.add(cbPaymentMethod, gbc);

        // Card Panel for dynamic payment input
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        // Cash payment detail panel
        pnlCash = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlCash.setOpaque(false);
        pnlCash.add(new JLabel("Uang Tunai Diterima (Rp):"));
        txtCashAmount = new JTextField();
        pnlCash.add(txtCashAmount);

        // E-Wallet payment detail panel
        pnlEWallet = new JPanel(new GridLayout(4, 1, 0, 5));
        pnlEWallet.setOpaque(false);
        pnlEWallet.add(new JLabel("Pilih Provider E-Wallet:"));
        cbWalletProvider = new JComboBox<>(new String[]{"GoPay", "OVO", "Dana", "LinkAja"});
        pnlEWallet.add(cbWalletProvider);
        pnlEWallet.add(new JLabel("No. Handphone:"));
        txtPhoneNumber = new JTextField();
        pnlEWallet.add(txtPhoneNumber);

        cardPanel.add(pnlCash, "CASH");
        cardPanel.add(pnlEWallet, "E-WALLET");

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.weighty = 0.5;
        summaryPanel.add(cardPanel, gbc);
        gbc.weighty = 0.0;

        // Place order button
        btnCheckout = new JButton("Bayar & Proses Pesanan");
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckout.setBackground(new Color(40, 167, 69));
        btnCheckout.setForeground(Color.BLACK);
        btnCheckout.setPreferredSize(new Dimension(0, 40));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        summaryPanel.add(btnCheckout, gbc);

        cartPanel.add(summaryPanel, BorderLayout.EAST);
        tabbedPane.addTab("Keranjang Belanja", cartPanel);

        // Events
        cbPaymentMethod.addActionListener(e -> {
            String selected = cbPaymentMethod.getSelectedItem().toString();
            cardLayout.show(cardPanel, selected);
        });

        btnRemoveItem.addActionListener(e -> handleRemoveCartItem());
        btnCheckout.addActionListener(e -> handleCheckout());
    }

    private void createHistoryTab() {
        JPanel historyPanel = new JPanel(new BorderLayout(15, 15));
        historyPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        historyPanel.setBackground(Color.WHITE);

        String[] columns = {"ID Pesanan", "Tanggal", "Total Harga", "Metode Bayar", "Status Pesanan"};
        historyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblHistory = new JTable(historyModel);
        tblHistory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblHistory.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(tblHistory);
        historyPanel.add(scroll, BorderLayout.CENTER);

        btnRefreshHistory = new JButton("Refresh Riwayat Pesanan");
        btnRefreshHistory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnRefreshHistory.setBackground(new Color(0, 123, 255));
        btnRefreshHistory.setForeground(Color.BLACK);
        
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setOpaque(false);
        bottom.add(btnRefreshHistory);
        historyPanel.add(bottom, BorderLayout.SOUTH);

        tabbedPane.addTab("Riwayat Pesanan Anda", historyPanel);

        btnRefreshHistory.addActionListener(e -> loadHistoryData());
    }

    // --- Action Handlers ---

    private void loadMenuData(String categoryFilter) {
        menuModel.setRowCount(0);
        List<MenuItem> list = menuRepository.getAllMenuItems();
        for (MenuItem item : list) {
            if ("Semua".equals(categoryFilter) || item.getCategory().equalsIgnoreCase(categoryFilter)) {
                menuModel.addRow(new Object[]{
                        item.getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getCategory(),
                        item.getDetailInfo().replace("Tingkat Kepedasan: ", "").replace("Suhu & Ukuran: ", "")
                });
            }
        }
    }

    private void handleAddToCart() {
        int row = tblMenu.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih menu dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int itemId = (int) menuModel.getValueAt(row, 0);
        int qty = (int) spinQty.getValue();

        List<MenuItem> allItems = menuRepository.getAllMenuItems();
        MenuItem selectedItem = null;
        for (MenuItem item : allItems) {
            if (item.getId() == itemId) {
                selectedItem = item;
                break;
            }
        }

        if (selectedItem == null) return;

        // Check if item already exists in cart, then just update qty
        boolean exists = false;
        for (OrderItem orderItem : cartList) {
            if (orderItem.getMenuItem().getId() == itemId) {
                orderItem.setQuantity(orderItem.getQuantity() + qty);
                exists = true;
                break;
            }
        }

        if (!exists) {
            cartList.add(new OrderItem(selectedItem, qty));
        }

        JOptionPane.showMessageDialog(this, qty + " porsi " + selectedItem.getName() + " ditambahkan ke keranjang.", "Keranjang Terisi", JOptionPane.INFORMATION_MESSAGE);
        
        // Reset spinner
        spinQty.setValue(1);
        
        updateCartView();
    }

    private void updateCartView() {
        cartModel.setRowCount(0);
        totalCartPrice = 0.0;
        for (OrderItem item : cartList) {
            double sub = item.calculateSubtotal();
            totalCartPrice += sub;
            cartModel.addRow(new Object[]{
                    item.getMenuItem().getName(),
                    item.getMenuItem().getPrice(),
                    item.getQuantity(),
                    sub
            });
        }
        lblTotal.setText("Rp" + String.format("%,.0f", totalCartPrice));
    }

    private void handleRemoveCartItem() {
        int row = tblCart.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item keranjang yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cartList.remove(row);
        updateCartView();
    }

    private void handleCheckout() {
        if (cartList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja Anda kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String paymentMethod = cbPaymentMethod.getSelectedItem().toString();
        Payment paymentStrategy = null;

        if ("CASH".equals(paymentMethod)) {
            String cashStr = txtCashAmount.getText().trim();
            if (cashStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan jumlah uang pembayaran tunai!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double cash = Double.parseDouble(cashStr);
                paymentStrategy = new CashPayment(cash);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah uang harus berupa angka!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            String provider = cbWalletProvider.getSelectedItem().toString();
            String phone = txtPhoneNumber.getText().trim();
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan nomor handphone akun E-Wallet Anda!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            paymentStrategy = new EWalletPayment(provider, phone);
        }

        // Process payment (Polymorphism)
        String receipt = paymentStrategy.processPayment(totalCartPrice);
        if (receipt.startsWith("Pembayaran Gagal")) {
            JOptionPane.showMessageDialog(this, receipt, "Pembayaran Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Simpan Order ke Database
        Order order = new Order(0, customer.getUsername(), "", totalCartPrice, paymentMethod, "PENDING");
        for (OrderItem item : cartList) {
            order.addOrderItem(item);
        }

        if (orderRepository.saveOrder(order)) {
            // Tampilkan struk/receipt sukses
            JOptionPane.showMessageDialog(this, receipt + "\n\nPesanan Anda berhasil direkam dengan status: PENDING.\nSilakan tunggu konfirmasi Admin.", "Transaksi Sukses", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear cart
            cartList.clear();
            updateCartView();
            txtCashAmount.setText("");
            txtPhoneNumber.setText("");
            
            // Pindah ke tab riwayat pesanan
            tabbedPane.setSelectedIndex(2);
            loadHistoryData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memproses pesanan ke database.", "Transaksi Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadHistoryData() {
        historyModel.setRowCount(0);
        List<Order> list = orderRepository.getOrdersByCustomer(customer.getUsername());
        for (Order order : list) {
            historyModel.addRow(new Object[]{
                    order.getId(),
                    order.getOrderDate(),
                    "Rp" + String.format("%,.0f", order.getTotalPrice()),
                    order.getPaymentMethod(),
                    order.getStatus()
            });
        }
    }
}
