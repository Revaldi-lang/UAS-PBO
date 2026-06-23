package ui;

import database.DatabaseConfig;
import model.User;
import repository.UserRepository;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private UserRepository userRepository;

    // Clean color palette
    private static final Color ACCENT      = new Color(79, 70, 229);   // Indigo-600
    private static final Color ACCENT_HOVER = new Color(67, 56, 202);  // Indigo-700
    private static final Color BG          = new Color(249, 250, 251); // Gray-50
    private static final Color CARD_BG     = Color.WHITE;
    private static final Color TEXT_DARK   = new Color(17, 24, 39);    // Gray-900
    private static final Color TEXT_MUTED  = new Color(107, 114, 128); // Gray-500
    private static final Color BORDER      = new Color(229, 231, 235); // Gray-200
    private static final Color INPUT_BG    = new Color(249, 250, 251); // Gray-50
    private static final Color INPUT_FOCUS = new Color(79, 70, 229);

    public LoginFrame() {
        userRepository = new UserRepository();
        initUI();
    }

    private void initUI() {
        setTitle("Warung Om Budi");
        setSize(400, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Background panel
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setBackground(BG);

        // Card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(36, 32, 36, 32)
        ));

        // Title
        JLabel lblTitle = new JLabel("Warung Om Budi");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(TEXT_DARK);

        JLabel lblSub = new JLabel("Masuk ke akun Anda untuk melanjutkan");
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setBorder(new EmptyBorder(6, 0, 28, 0));

        card.add(lblTitle);
        card.add(lblSub);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 6, 0);
        form.add(fieldLabel("Username"), gbc);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setPreferredSize(new Dimension(0, 40));
        txtUsername.putClientProperty("JTextField.placeholderText", "Masukkan username");
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 18, 0);
        form.add(txtUsername, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 6, 0);
        form.add(fieldLabel("Password"), gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(0, 40));
        txtPassword.putClientProperty("JTextField.placeholderText", "Masukkan password");
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 28, 0);
        form.add(txtPassword, gbc);

        card.add(form);

        // Login button
        btnLogin = new JButton("Masuk");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(ACCENT);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.addMouseListener(hoverEffect(btnLogin, ACCENT, ACCENT_HOVER));
        card.add(btnLogin);

        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // Register button
        btnRegister = new JButton("Buat Akun Baru");
        btnRegister.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnRegister.setBackground(CARD_BG);
        btnRegister.setForeground(ACCENT);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnRegister.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        btnRegister.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        btnRegister.addMouseListener(hoverEffect(btnRegister, CARD_BG, new Color(243, 244, 246)));
        card.add(btnRegister);

        bg.add(card);
        add(bg);

        // Listeners
        btnLogin.addActionListener(e -> handleLogin());
        btnRegister.addActionListener(e -> handleRegister());

        KeyAdapter enterKey = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_DARK);
        return lbl;
    }

    private MouseAdapter hoverEffect(JButton btn, Color normal, Color hover) {
        return new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(normal); }
        };
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
            this.dispose();
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
            JOptionPane.showMessageDialog(this, "Registrasi berhasil! Silakan login.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            txtPassword.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Registrasi gagal! Username mungkin sudah digunakan.", "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            UIManager.put("ScrollBar.width", 10);

            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("TabbedPane.tabHeight", 38);
            UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
            UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("TabbedPane.underlineColor", new Color(79, 70, 229));
            UIManager.put("TabbedPane.selectedForeground", new Color(79, 70, 229));

            UIManager.put("Table.rowHeight", 36);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
            UIManager.put("Table.selectionBackground", new Color(238, 242, 255));
            UIManager.put("Table.selectionForeground", new Color(55, 48, 163));

            UIManager.put("TableHeader.background", new Color(249, 250, 251));
            UIManager.put("TableHeader.foreground", new Color(107, 114, 128));
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 11));
            UIManager.put("TableHeader.separatorColor", new Color(229, 231, 235));
            UIManager.put("TableHeader.bottomSeparatorColor", new Color(229, 231, 235));

        } catch (Exception e) {
            e.printStackTrace();
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) { ex.printStackTrace(); }
        }

        DatabaseConfig.initializeDatabase();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
