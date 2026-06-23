package ui;

import database.DatabaseConfig;
import model.User;
import repository.UserRepository;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private UserRepository userRepository;

    public LoginFrame() {
        userRepository = new UserRepository();
        initUI();
    }

    private void initUI() {
        setTitle("Food Ordering System - Login");
        setSize(420, 365);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel (Background)
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(242, 245, 249)); // Very soft blue-grey

        // Card Panel (Container for login elements)
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 224, 233), 1, true),
                new EmptyBorder(25, 30, 25, 30)
        ));

        // Header Label
        JLabel lblHeader = new JLabel("FOOD ORDER SYSTEM");
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(new Color(230, 74, 25)); // Modern Warm Coral/Orange

        JLabel lblSubHeader = new JLabel("Silakan masuk ke akun Anda");
        lblSubHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubHeader.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubHeader.setForeground(new Color(120, 130, 140));
        lblSubHeader.setBorder(new EmptyBorder(5, 0, 20, 0));

        cardPanel.add(lblHeader);
        cardPanel.add(lblSubHeader);

        // Input Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.weightx = 1.0;

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsername.setForeground(new Color(80, 90, 100));
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.putClientProperty("JTextField.placeholderText", "Masukkan username");
        txtUsername.setPreferredSize(new Dimension(0, 32));

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(new Color(80, 90, 100));
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.putClientProperty("JTextField.placeholderText", "Masukkan password");
        txtPassword.setPreferredSize(new Dimension(0, 32));

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lblUsername, gbc);
        gbc.gridy = 1;
        formPanel.add(txtUsername, gbc);
        gbc.gridy = 2;
        formPanel.add(lblPassword, gbc);
        gbc.gridy = 3;
        formPanel.add(txtPassword, gbc);

        cardPanel.add(formPanel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(new Color(230, 74, 25)); // Orange Red Accent
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 36));

        btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setBackground(new Color(108, 117, 125)); // Secondary Gray
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setPreferredSize(new Dimension(0, 36));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);

        cardPanel.add(buttonPanel);

        // Add card to main panel
        mainPanel.add(cardPanel);
        add(mainPanel);

        // Listeners
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userRepository.login(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, user.getWelcomeMessage(), "Login Berhasil", JOptionPane.INFORMATION_MESSAGE);
            this.dispose(); // Tutup window login

            // Buka Dashboard yang sesuai berdasarkan role (Polymorphism)
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                new AdminFrame(user).setVisible(true);
            } else {
                new CustomerFrame(user).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Username atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = userRepository.register(username, password);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registrasi berhasil! Silakan login menggunakan akun Anda.", "Registrasi Sukses", JOptionPane.INFORMATION_MESSAGE);
            txtPassword.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Registrasi gagal! Username mungkin sudah digunakan.", "Registrasi Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Set Look and Feel to FlatLaf
        try {
            FlatLightLaf.setup();
            
            // Set global components rounded corners and formatting
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            
            // Styles for specific components
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("TabbedPane.tabHeight", 36);
            UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
            UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.BOLD, 13));
            
            UIManager.put("Table.rowHeight", 28);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
            UIManager.put("Table.selectionBackground", new Color(254, 240, 235)); // Soft warm/orange select bg
            UIManager.put("Table.selectionForeground", new Color(230, 74, 25)); // Orange accent select text
            
            UIManager.put("TableHeader.background", new Color(248, 249, 250));
            UIManager.put("TableHeader.foreground", new Color(74, 85, 104));
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Jalankan inisialisasi database
        DatabaseConfig.initializeDatabase();

        // Tampilkan Login Screen
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
