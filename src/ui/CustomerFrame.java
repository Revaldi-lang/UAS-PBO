package ui;

import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.User;
import payment.CashPayment;
import payment.EWalletPayment;
import payment.Payment;
import payment.QRISPayment;
import repository.MenuRepository;
import repository.OrderRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerFrame extends JFrame {
    private User customer;
    private MenuRepository menuRepository;
    private OrderRepository orderRepository;

    private List<OrderItem> cartList;
    private double totalCartPrice = 0.0;

    private JTabbedPane tabbedPane;

    private JTable tblMenu;
    private DefaultTableModel menuModel;
    private JComboBox<String> cbFilterCategory;
    private JSpinner spinQty;
    private JButton btnAddToCart;

    private JTable tblCart;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;
    private JButton btnRemoveItem;
    private JComboBox<String> cbPaymentMethod;

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JTextField txtCashAmount;
    private JComboBox<String> cbWalletProvider;
    private JTextField txtPhoneNumber;
    private JButton btnCheckout;

    private JTable tblHistory;
    private DefaultTableModel historyModel;
    private JButton btnRefreshHistory;

    private JLabel lblDiscountVal;
    private JLabel lblFinalTotal;
    private JTextField txtPromoCode;
    private JButton btnApplyPromo;
    private JLabel lblPromoStatus;
    private repository.PromoRepository promoRepository;
    private model.Promo appliedPromo = null;
    private double discountAmount = 0.0;
    private double finalTotalPrice = 0.0;

    // Colors
    private static final Color ACCENT      = new Color(79, 70, 229);
    private static final Color ACCENT_HOVER = new Color(67, 56, 202);
    private static final Color BG          = new Color(249, 250, 251);
    private static final Color TEXT_DARK   = new Color(17, 24, 39);
    private static final Color TEXT_MUTED  = new Color(107, 114, 128);
    private static final Color BORDER      = new Color(229, 231, 235);
    private static final Color SUCCESS     = new Color(5, 150, 105);
    private static final Color SUCCESS_H   = new Color(4, 120, 87);
    private static final Color DANGER      = new Color(220, 38, 38);
    private static final Color DANGER_H    = new Color(185, 28, 28);
    private static final Color NEUTRAL     = new Color(107, 114, 128);
    private static final Color NEUTRAL_H   = new Color(75, 85, 99);
    private static final Color WARNING     = new Color(217, 119, 6);

    public CustomerFrame(User customer) {
        this.customer = customer;
        this.menuRepository = new MenuRepository();
        this.orderRepository = new OrderRepository();
        this.promoRepository = new repository.PromoRepository();
        this.cartList = new ArrayList<>();
        initUI();
    }

    private void initUI() {
        setTitle("Warung Om Budi");
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
        JLabel lblTitle = new JLabel("Warung Om Budi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);
        JLabel lblUser = new JLabel(customer.getWelcomeMessage());
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
        createBrowseTab();
        createCartTab();
        createHistoryTab();
        main.add(tabbedPane, BorderLayout.CENTER);

        add(main);
        loadMenuData("Semua");
        loadHistoryData();
    }

    private void createBrowseTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setBackground(BG);

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterBar.setOpaque(false);
        filterBar.add(fieldLabel("Kategori:"));
        cbFilterCategory = new JComboBox<>(new String[]{"Semua", "FOOD", "BEVERAGE"});
        cbFilterCategory.setPreferredSize(new Dimension(130, 32));
        filterBar.add(cbFilterCategory);
        panel.add(filterBar, BorderLayout.NORTH);

        // Table card
        JPanel tableCard = wrapCard(new BorderLayout());
        String[] cols = {"ID", "Nama Menu", "Harga", "Kategori", "Detail"};
        menuModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMenu = new JTable(menuModel);
        styleTable(tblMenu);
        tblMenu.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblMenu.getColumnModel().getColumn(0).setMaxWidth(60);
        tableCard.add(new JScrollPane(tblMenu), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        // Action bar
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionBar.setOpaque(false);
        actionBar.add(fieldLabel("Jumlah:"));
        spinQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spinQty.setPreferredSize(new Dimension(65, 32));
        actionBar.add(spinQty);
        btnAddToCart = flatButton("Tambah ke Keranjang", SUCCESS, SUCCESS_H);
        btnAddToCart.setPreferredSize(new Dimension(190, 34));
        actionBar.add(btnAddToCart);
        panel.add(actionBar, BorderLayout.SOUTH);

        tabbedPane.addTab("Menu", panel);

        cbFilterCategory.addActionListener(e -> loadMenuData(cbFilterCategory.getSelectedItem().toString()));
        btnAddToCart.addActionListener(e -> handleAddToCart());
    }

    private void createCartTab() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setBackground(BG);

        // Left: cart table
        JPanel tableCard = wrapCard(new BorderLayout(0, 10));
        String[] cols = {"Nama Menu", "Harga", "Qty", "Subtotal"};
        cartModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCart = new JTable(cartModel);
        styleTable(tblCart);
        DefaultTableCellRenderer centerR = new DefaultTableCellRenderer();
        centerR.setHorizontalAlignment(JLabel.CENTER);
        tblCart.getColumnModel().getColumn(2).setCellRenderer(centerR);
        tableCard.add(new JScrollPane(tblCart), BorderLayout.CENTER);

        btnRemoveItem = flatButton("Hapus Item", DANGER, DANGER_H);
        btnRemoveItem.setPreferredSize(new Dimension(130, 34));
        JPanel removeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        removeWrap.setOpaque(false);
        removeWrap.add(btnRemoveItem);
        tableCard.add(removeWrap, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);

        // Right: checkout summary
        JPanel summaryCard = wrapCard(new GridBagLayout());
        summaryCard.setPreferredSize(new Dimension(300, 0));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0; g.gridx = 0;

        g.gridy = 0; g.insets = new Insets(0, 0, 16, 0);
        JLabel sumTitle = new JLabel("Ringkasan Pembayaran");
        sumTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sumTitle.setForeground(TEXT_DARK);
        summaryCard.add(sumTitle, g);

        // Subtotal (Original Total)
        g.gridy = 1; g.insets = new Insets(0, 0, 4, 0);
        summaryCard.add(fieldLabel("Subtotal Menu"), g);
        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(TEXT_DARK);
        g.gridy = 2; g.insets = new Insets(0, 0, 8, 0);
        summaryCard.add(lblTotal, g);

        // Diskon
        g.gridy = 3; g.insets = new Insets(0, 0, 4, 0);
        summaryCard.add(fieldLabel("Diskon Kupon"), g);
        lblDiscountVal = new JLabel("Rp 0");
        lblDiscountVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDiscountVal.setForeground(DANGER);
        g.gridy = 4; g.insets = new Insets(0, 0, 8, 0);
        summaryCard.add(lblDiscountVal, g);

        // Total Bayar Akhir
        g.gridy = 5; g.insets = new Insets(0, 0, 4, 0);
        summaryCard.add(fieldLabel("Total Bayar Akhir"), g);
        lblFinalTotal = new JLabel("Rp 0");
        lblFinalTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblFinalTotal.setForeground(ACCENT);
        g.gridy = 6; g.insets = new Insets(0, 0, 14, 0);
        summaryCard.add(lblFinalTotal, g);

        // Coupon input
        g.gridy = 7; g.insets = new Insets(0, 0, 4, 0);
        summaryCard.add(fieldLabel("Kode Promo (Kupon)"), g);
        JPanel pnlCoupon = new JPanel(new BorderLayout(8, 0));
        pnlCoupon.setOpaque(false);
        txtPromoCode = new JTextField();
        txtPromoCode.setPreferredSize(new Dimension(120, 32));
        txtPromoCode.putClientProperty("JTextField.placeholderText", "Masukkan kode");
        btnApplyPromo = flatButton("Pakai", ACCENT, ACCENT_HOVER);
        btnApplyPromo.setPreferredSize(new Dimension(75, 32));
        pnlCoupon.add(txtPromoCode, BorderLayout.CENTER);
        pnlCoupon.add(btnApplyPromo, BorderLayout.EAST);
        g.gridy = 8; g.insets = new Insets(0, 0, 4, 0);
        summaryCard.add(pnlCoupon, g);

        lblPromoStatus = new JLabel("");
        lblPromoStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPromoStatus.setForeground(SUCCESS);
        g.gridy = 9; g.insets = new Insets(0, 0, 12, 0);
        summaryCard.add(lblPromoStatus, g);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        g.gridy = 10; g.insets = new Insets(0, 0, 16, 0);
        summaryCard.add(sep, g);

        // Payment method
        g.gridy = 11; g.insets = new Insets(0, 0, 4, 0);
        summaryCard.add(fieldLabel("Metode Bayar"), g);
        cbPaymentMethod = new JComboBox<>(new String[]{"CASH", "E-WALLET", "QRIS"});
        cbPaymentMethod.setPreferredSize(new Dimension(0, 34));
        g.gridy = 12; g.insets = new Insets(0, 0, 14, 0);
        summaryCard.add(cbPaymentMethod, g);

        // Dynamic payment input
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        JPanel pnlCash = new JPanel(new GridBagLayout());
        pnlCash.setOpaque(false);
        GridBagConstraints cg = new GridBagConstraints();
        cg.fill = GridBagConstraints.HORIZONTAL; cg.weightx = 1.0; cg.gridx = 0;
        cg.gridy = 0; cg.insets = new Insets(0, 0, 4, 0);
        pnlCash.add(fieldLabel("Uang Tunai (Rp)"), cg);
        txtCashAmount = new JTextField();
        txtCashAmount.setPreferredSize(new Dimension(0, 34));
        txtCashAmount.putClientProperty("JTextField.placeholderText", "Masukkan nominal");
        cg.gridy = 1; cg.insets = new Insets(0, 0, 0, 0);
        pnlCash.add(txtCashAmount, cg);

        JPanel pnlEWallet = new JPanel(new GridBagLayout());
        pnlEWallet.setOpaque(false);
        GridBagConstraints eg = new GridBagConstraints();
        eg.fill = GridBagConstraints.HORIZONTAL; eg.weightx = 1.0; eg.gridx = 0;
        eg.gridy = 0; eg.insets = new Insets(0, 0, 4, 0);
        pnlEWallet.add(fieldLabel("Provider"), eg);
        cbWalletProvider = new JComboBox<>(new String[]{"GoPay", "OVO", "Dana", "LinkAja"});
        cbWalletProvider.setPreferredSize(new Dimension(0, 34));
        eg.gridy = 1; eg.insets = new Insets(0, 0, 12, 0);
        pnlEWallet.add(cbWalletProvider, eg);
        eg.gridy = 2; eg.insets = new Insets(0, 0, 4, 0);
        pnlEWallet.add(fieldLabel("No. Handphone"), eg);
        txtPhoneNumber = new JTextField();
        txtPhoneNumber.setPreferredSize(new Dimension(0, 34));
        txtPhoneNumber.putClientProperty("JTextField.placeholderText", "08123456789");
        eg.gridy = 3; eg.insets = new Insets(0, 0, 0, 0);
        pnlEWallet.add(txtPhoneNumber, eg);

        JPanel pnlQRIS = new JPanel(new GridBagLayout());
        pnlQRIS.setOpaque(false);
        GridBagConstraints qg = new GridBagConstraints();
        qg.fill = GridBagConstraints.HORIZONTAL; qg.weightx = 1.0; qg.gridx = 0;
        qg.gridy = 0; qg.insets = new Insets(0, 0, 4, 0);
        JLabel lblScan = new JLabel("Scan Kode QRIS di bawah ini:");
        lblScan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblScan.setForeground(TEXT_DARK);
        pnlQRIS.add(lblScan, qg);

        JLabel lblBarcode = new JLabel();
        lblBarcode.setHorizontalAlignment(JLabel.CENTER);
        java.io.File qrFile = new java.io.File("src/resources/qris_code.jpg");
        if (qrFile.exists()) {
            ImageIcon qrIcon = new ImageIcon(qrFile.getAbsolutePath());
            Image img = qrIcon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            lblBarcode.setIcon(new ImageIcon(img));
        } else {
            lblBarcode.setText("[Gambar QRIS tidak ditemukan]");
            lblBarcode.setForeground(DANGER);
        }
        qg.gridy = 1; qg.insets = new Insets(4, 0, 0, 0);
        pnlQRIS.add(lblBarcode, qg);

        cardPanel.add(pnlCash, "CASH");
        cardPanel.add(pnlEWallet, "E-WALLET");
        cardPanel.add(pnlQRIS, "QRIS");

        g.gridy = 13; g.insets = new Insets(0, 0, 20, 0);
        summaryCard.add(cardPanel, g);

        // Checkout button
        btnCheckout = flatButton("Bayar Sekarang", ACCENT, ACCENT_HOVER);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCheckout.setPreferredSize(new Dimension(0, 42));
        g.gridy = 14; g.insets = new Insets(0, 0, 0, 0);
        summaryCard.add(btnCheckout, g);

        g.gridy = 15; g.weighty = 1.0;
        summaryCard.add(Box.createGlue(), g);

        panel.add(summaryCard, BorderLayout.EAST);
        tabbedPane.addTab("Keranjang", panel);

        cbPaymentMethod.addActionListener(e -> cardLayout.show(cardPanel, cbPaymentMethod.getSelectedItem().toString()));
        btnRemoveItem.addActionListener(e -> handleRemoveCartItem());
        btnCheckout.addActionListener(e -> handleCheckout());
        btnApplyPromo.addActionListener(e -> handleApplyPromo());
    }

    private void createHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setBackground(BG);

        JPanel tableCard = wrapCard(new BorderLayout());
        String[] cols = {"ID", "Tanggal", "Total Harga", "Metode Bayar", "Status"};
        historyModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHistory = new JTable(historyModel);
        styleTable(tblHistory);

        // Status renderer
        tblHistory.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                String s = v == null ? "" : v.toString();
                if ("COMPLETED".equals(s) && !sel) lbl.setForeground(SUCCESS);
                else if ("PENDING".equals(s) && !sel) lbl.setForeground(WARNING);
                return lbl;
            }
        });

        tableCard.add(new JScrollPane(tblHistory), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setOpaque(false);
        btnRefreshHistory = flatButton("Refresh", NEUTRAL, NEUTRAL_H);
        btnRefreshHistory.setPreferredSize(new Dimension(100, 34));
        bottom.add(btnRefreshHistory);
        panel.add(bottom, BorderLayout.SOUTH);

        tabbedPane.addTab("Riwayat", panel);
        btnRefreshHistory.addActionListener(e -> loadHistoryData());
    }

    // --- Helpers ---

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

    private void loadMenuData(String filter) {
        menuModel.setRowCount(0);
        List<MenuItem> list = menuRepository.getAllMenuItems();
        for (MenuItem item : list) {
            if ("Semua".equals(filter) || item.getCategory().equalsIgnoreCase(filter)) {
                menuModel.addRow(new Object[]{
                        item.getId(), item.getName(), item.getPrice(), item.getCategory(),
                        item.getDetailInfo().replace("Tingkat Kepedasan: ", "").replace("Suhu & Ukuran: ", "")
                });
            }
        }
    }

    private void handleAddToCart() {
        int row = tblMenu.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih menu dari tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int itemId = (int) menuModel.getValueAt(row, 0);
        int qty = (int) spinQty.getValue();

        List<MenuItem> allItems = menuRepository.getAllMenuItems();
        MenuItem selectedItem = null;
        for (MenuItem item : allItems) {
            if (item.getId() == itemId) { selectedItem = item; break; }
        }
        if (selectedItem == null) return;

        boolean exists = false;
        for (OrderItem oi : cartList) {
            if (oi.getMenuItem().getId() == itemId) {
                oi.setQuantity(oi.getQuantity() + qty);
                exists = true; break;
            }
        }
        if (!exists) cartList.add(new OrderItem(selectedItem, qty));

        JOptionPane.showMessageDialog(this,
                qty + " x " + selectedItem.getName() + " ditambahkan ke keranjang.",
                "Berhasil", JOptionPane.INFORMATION_MESSAGE);
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
                    item.getMenuItem().getName(), item.getMenuItem().getPrice(),
                    item.getQuantity(), sub
            });
        }
        
        // Dynamic promo calculation
        if (appliedPromo != null) {
            if (totalCartPrice >= appliedPromo.getMinPurchase()) {
                discountAmount = totalCartPrice * (appliedPromo.getDiscountPercent() / 100.0);
                if (discountAmount > appliedPromo.getMaxDiscount()) {
                    discountAmount = appliedPromo.getMaxDiscount();
                }
                lblPromoStatus.setText("Promo diterapkan: " + appliedPromo.getCode());
                lblPromoStatus.setForeground(SUCCESS);
            } else {
                appliedPromo = null;
                discountAmount = 0.0;
                lblPromoStatus.setText("Kupon dilepas (total belanja kurang)");
                lblPromoStatus.setForeground(DANGER);
            }
        } else {
            discountAmount = 0.0;
        }

        finalTotalPrice = totalCartPrice - discountAmount;
        if (finalTotalPrice < 0) finalTotalPrice = 0.0;

        lblTotal.setText("Rp " + String.format("%,.0f", totalCartPrice));
        lblDiscountVal.setText("Rp " + String.format("%,.0f", discountAmount));
        lblFinalTotal.setText("Rp " + String.format("%,.0f", finalTotalPrice));
    }

    private void handleRemoveCartItem() {
        int row = tblCart.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cartList.remove(row);
        updateCartView();
    }

    private void handleCheckout() {
        if (cartList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String paymentMethod = cbPaymentMethod.getSelectedItem().toString();
        Payment paymentStrategy = null;

        if ("CASH".equals(paymentMethod)) {
            String cashStr = txtCashAmount.getText().trim();
            if (cashStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan jumlah uang!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                paymentStrategy = new CashPayment(Double.parseDouble(cashStr));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if ("E-WALLET".equals(paymentMethod)) {
            String provider = cbWalletProvider.getSelectedItem().toString();
            String phone = txtPhoneNumber.getText().trim();
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan nomor handphone!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            paymentStrategy = new EWalletPayment(provider, phone);
        } else if ("QRIS".equals(paymentMethod)) {
            paymentStrategy = new QRISPayment();
        }

        String receipt = paymentStrategy.processPayment(finalTotalPrice);
        if (receipt.startsWith("Pembayaran Gagal")) {
            JOptionPane.showMessageDialog(this, receipt, "Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Order order = new Order(0, customer.getUsername(), "", finalTotalPrice, paymentMethod, "PENDING", discountAmount, appliedPromo != null ? appliedPromo.getCode() : "");
        for (OrderItem item : cartList) order.addOrderItem(item);

        if (orderRepository.saveOrder(order)) {
            JOptionPane.showMessageDialog(this,
                    receipt + "\n\nPesanan direkam dengan status PENDING.\nTunggu konfirmasi admin.",
                    "Transaksi Berhasil", JOptionPane.INFORMATION_MESSAGE);
            cartList.clear();
            appliedPromo = null;
            discountAmount = 0.0;
            txtPromoCode.setText("");
            lblPromoStatus.setText("");
            updateCartView();
            txtCashAmount.setText("");
            txtPhoneNumber.setText("");
            tabbedPane.setSelectedIndex(2);
            loadHistoryData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memproses pesanan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleApplyPromo() {
        String code = txtPromoCode.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan kode promo!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (totalCartPrice <= 0) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        model.Promo promo = promoRepository.getPromoByCode(code);
        if (promo == null) {
            lblPromoStatus.setText("Kode promo tidak valid!");
            lblPromoStatus.setForeground(DANGER);
            appliedPromo = null;
            discountAmount = 0.0;
            updateCartView();
            return;
        }
        if (totalCartPrice < promo.getMinPurchase()) {
            lblPromoStatus.setText("Min. belanja Rp" + String.format("%,.0f", promo.getMinPurchase()));
            lblPromoStatus.setForeground(DANGER);
            appliedPromo = null;
            discountAmount = 0.0;
            updateCartView();
            return;
        }
        
        appliedPromo = promo;
        lblPromoStatus.setText("Promo diterapkan: " + promo.getCode());
        lblPromoStatus.setForeground(SUCCESS);
        updateCartView();
    }

    private void loadHistoryData() {
        historyModel.setRowCount(0);
        List<Order> list = orderRepository.getOrdersByCustomer(customer.getUsername());
        for (Order order : list) {
            historyModel.addRow(new Object[]{
                    order.getId(), order.getOrderDate(),
                    "Rp " + String.format("%,.0f", order.getTotalPrice()),
                    order.getPaymentMethod(), order.getStatus()
            });
        }
    }
}
