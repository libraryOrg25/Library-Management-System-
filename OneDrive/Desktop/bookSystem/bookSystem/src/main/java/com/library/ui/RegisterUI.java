package com.library.ui;

import javax.swing.*;
import java.awt.*;
import com.library.service.AuthService;

public class RegisterUI extends JFrame {

    // ====== ⬇️ صاروا protected عشان الاختبار يقدر يوصلهم
    protected JTextField usernameField, emailField;
    protected JPasswordField passwordField;
    protected JComboBox<String> roleBox;
    protected JButton registerBtn;
    protected AuthService authService;

    // ============= Inject-able AuthService for testing =================
    protected AuthService createAuthService() {
        return new AuthService();
    }

    public RegisterUI() {

        // نستخدم الـ factory method بدل new AuthService()
        authService = createAuthService();

        setTitle("Create an Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 450);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel title = new JLabel("CREATE AN ACCOUNT", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBounds(50, 30, 300, 40);
        add(title);

        JLabel userLabel = new JLabel("User name");
        userLabel.setBounds(50, 90, 100, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(50, 115, 300, 35);
        usernameField.setBackground(Color.BLACK);
        usernameField.setForeground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(usernameField);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setBounds(50, 160, 100, 25);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(50, 185, 300, 35);
        emailField.setBackground(Color.BLACK);
        emailField.setForeground(Color.WHITE);
        emailField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(emailField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setBounds(50, 230, 100, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 255, 300, 35);
        passwordField.setBackground(Color.BLACK);
        passwordField.setForeground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(passwordField);

        JLabel roleLabel = new JLabel("Role");
        roleLabel.setBounds(50, 300, 100, 25);
        add(roleLabel);

        roleBox = new JComboBox<>(new String[]{"admin", "user"});
        roleBox.setBounds(50, 325, 300, 35);
        roleBox.setBackground(Color.BLACK);
        roleBox.setForeground(Color.WHITE);
        add(roleBox);

        // ====== ⬇️ زر التسجيل صار protected وقابل للاختبار
        registerBtn = new JButton("REGISTER");
        registerBtn.setBounds(100, 375, 200, 35);
        registerBtn.setBackground(Color.WHITE);
        registerBtn.setForeground(Color.BLACK);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(registerBtn);

        JButton goToLogin = new JButton("Already have an account? Log in");
        goToLogin.setBounds(80, 415, 250, 25);
        goToLogin.setFont(new Font("SansSerif", Font.PLAIN, 12));
        goToLogin.setFocusPainted(false);
        goToLogin.setContentAreaFilled(false);
        goToLogin.setBorderPainted(false);
        goToLogin.setForeground(Color.BLUE);
        add(goToLogin);

        // ================= Register Button Event =====================
        registerBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = (String) roleBox.getSelectedItem();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = authService.register(username, email, password, role);

            if (success) {
                JOptionPane.showMessageDialog(this, "Account created successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Email already exists!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ============== Go to Login ======================
        goToLogin.addActionListener(e -> {
            dispose();
            new LoginUI().setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterUI().setVisible(true));
    }
}
