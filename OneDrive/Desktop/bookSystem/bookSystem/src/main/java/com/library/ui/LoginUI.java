package com.library.ui;

import javax.swing.*;
import java.awt.*;
import com.library.service.AuthService;
import com.library.domain.User;
import com.library.persistence.FileStorage;

public class LoginUI extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private AuthService authService;

    public LoginUI() {
        authService = new AuthService();
        setTitle("Log In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel title = new JLabel("LOG IN", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBounds(50, 30, 300, 40);
        add(title);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setBounds(50, 90, 100, 25);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(50, 115, 300, 35);
        emailField.setBackground(Color.BLACK);
        emailField.setForeground(Color.WHITE);
        emailField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(emailField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setBounds(50, 170, 100, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 195, 300, 35);
        passwordField.setBackground(Color.BLACK);
        passwordField.setForeground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(passwordField);

        JButton loginBtn = new JButton("LOG IN");
        loginBtn.setBounds(100, 250, 200, 35);
        loginBtn.setBackground(Color.WHITE);
        loginBtn.setForeground(Color.BLACK);
        loginBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(loginBtn);

        JButton goToRegister = new JButton("Create a new account");
        goToRegister.setBounds(110, 290, 180, 25);
        goToRegister.setFocusPainted(false);
        goToRegister.setContentAreaFilled(false);
        goToRegister.setBorderPainted(false);
        goToRegister.setForeground(Color.BLUE);
        add(goToRegister);

        // LOGIN EVENT
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            User user = authService.login(email, password);

            if (user == null) {
                JOptionPane.showMessageDialog(this, "Wrong email or password!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, "Login Successful!");

            // save current user
            

            dispose();

            if (user.getRole().equalsIgnoreCase("admin")) {
                new AdminDashboard().setVisible(true);
            } else {
                
                new UserDashboard(user.getEmail()).setVisible(true);
            }
        });
    }
    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginUI login = new LoginUI();
                login.setVisible(true);
            }
        });
    }


    
    
    
    
}
