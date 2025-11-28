package com.library.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.library.domain.User;
import com.library.domain.BorrowRecord;
import com.library.persistence.FileStorage;
import com.library.service.EmailService;

public class AdminDashboard extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel addBookPanel, usersPanel, emailPanel;
    private JTextField titleField, authorField, isbnField, copiesField;
    private JTextField emailField, messageField;
    private static final String BOOKS_FILE = "books.txt";

    public AdminDashboard() {
        setTitle("Library Admin Dashboard");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JButton logoutBtn = new JButton("LOG OUT");
        logoutBtn.setBounds(20, 10, 100, 30);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginUI().setVisible(true);
        });
        add(logoutBtn);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(0, 50, 950, 500);
        add(tabbedPane);

        addBookPanel = createAddBookPanel();
        tabbedPane.addTab("ADD BOOKS", addBookPanel);

        usersPanel = createUsersInfoPanel();
        tabbedPane.addTab("USERS INFORMATION", usersPanel);

        emailPanel = createEmailPanel();
        tabbedPane.addTab("SEND EMAIL", emailPanel);
    }

    // ----------------------------------------------------------------------
    private JPanel createUsersInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Registered Users", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        List<User> users = FileStorage.loadUsers();

        String[] columns = {
                "USER NAME", "EMAIL", "Borrowed Books",
                "Date of Borrowing", "Deadline", "Debt/Overdue", "Unregister"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(30);

        for (User user : users) {
            if (user.getBorrowedBooks().isEmpty()) {
                model.addRow(new Object[]{
                        user.getUsername(), user.getEmail(), "-", "-", "-", "No", "Unregister"
                });
            } else {
                for (BorrowRecord record : user.getBorrowedBooks()) {
                    String overdue = "No";
                    if (record.getDeadline().isBefore(LocalDate.now())) {
                        long daysLate = ChronoUnit.DAYS.between(record.getDeadline(), LocalDate.now());
                        overdue = (daysLate > 29) ? "Debt (+" + daysLate + " days)"
                                : "Overdue (" + daysLate + " days)";
                    }
                    model.addRow(new Object[]{
                            user.getUsername(),
                            user.getEmail(),
                            record.getBookTitle(),
                            record.getBorrowDate(),
                            record.getDeadline(),
                            overdue,
                            "Unregister"
                    });
                }
            }
        }

        table.getColumn("Unregister").setCellRenderer(new ButtonRenderer());
        table.getColumn("Unregister").setCellEditor(new ButtonEditor(new JCheckBox(), users, model));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ----------------------------------------------------------------------
    private JPanel createAddBookPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("BOOK TITLE");
        titleLabel.setBounds(100, 50, 200, 25);
        panel.add(titleLabel);

        titleField = new JTextField();
        titleField.setBounds(100, 75, 400, 35);
        styleTextField(titleField);
        panel.add(titleField);

        JLabel authorLabel = new JLabel("Author");
        authorLabel.setBounds(100, 120, 200, 25);
        panel.add(authorLabel);

        authorField = new JTextField();
        authorField.setBounds(100, 145, 400, 35);
        styleTextField(authorField);
        panel.add(authorField);

        JLabel isbnLabel = new JLabel("ISBN");
        isbnLabel.setBounds(100, 190, 200, 25);
        panel.add(isbnLabel);

        isbnField = new JTextField();
        isbnField.setBounds(100, 215, 400, 35);
        styleTextField(isbnField);
        panel.add(isbnField);

        JLabel copiesLabel = new JLabel("Number of copies of this book");
        copiesLabel.setBounds(100, 260, 250, 25);
        panel.add(copiesLabel);

        copiesField = new JTextField();
        copiesField.setBounds(100, 285, 400, 35);
        styleTextField(copiesField);
        panel.add(copiesField);

        JButton addButton = new JButton("ADD");
        addButton.setBounds(200, 340, 200, 40);
        addButton.setBackground(Color.WHITE);
        addButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        addButton.addActionListener(e -> addBook());
        panel.add(addButton);

        return panel;
    }

    // ----------------------------------------------------------------------
    private JPanel createEmailPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        JLabel emailLabel = new JLabel("User Email:");
        emailLabel.setBounds(100, 80, 200, 25);
        panel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(100, 105, 400, 35);
        styleTextField(emailField);
        panel.add(emailField);

        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setBounds(100, 160, 200, 25);
        panel.add(messageLabel);

        messageField = new JTextField("You have overdue book(s)!");
        messageField.setBounds(100, 185, 400, 35);
        styleTextField(messageField);
        panel.add(messageField);

        JButton sendButton = new JButton("SEND");
        sendButton.setBounds(220, 250, 180, 40);
        sendButton.setBackground(Color.WHITE);
        sendButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        sendButton.addActionListener(e -> sendEmail());
        panel.add(sendButton);

        return panel;
    }

    // ----------------------------------------------------------------------
    private void styleTextField(JTextField field) {
        field.setBackground(Color.BLACK);
        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void addBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String isbn = isbnField.getText().trim();
        String copies = copiesField.getText().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || copies.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE, true))) {
            writer.write(title + "," + author + "," + isbn + "," + copies);
            writer.newLine();
            JOptionPane.showMessageDialog(this, "Book added successfully!");
            titleField.setText("");
            authorField.setText("");
            isbnField.setText("");
            copiesField.setText("");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving book!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendEmail() {
        String to = emailField.getText().trim();
        String msg = messageField.getText().trim();

        if (to.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter recipient email!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            new EmailService().sendEmail(to, "Library Notification", msg);
            JOptionPane.showMessageDialog(this, "Email sent successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to send email: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----------------------------------------------------------------------
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText((value == null) ? "Unregister" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private int row;
        private List<User> users;
        private DefaultTableModel model;

        public ButtonEditor(JCheckBox checkBox, List<User> usersList, DefaultTableModel tableModel) {
            super(checkBox);
            this.users = usersList;
            this.model = tableModel;
            this.button = new JButton("Unregister");
            this.button.setOpaque(true);
            this.button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                String email = (String) model.getValueAt(row, 1);
                users.removeIf(u -> u.getEmail().equalsIgnoreCase(email));
                FileStorage.saveUsers(users);
                model.removeRow(row);
                JOptionPane.showMessageDialog(null, "User unregistered successfully!");
            }
            clicked = false;
            return "Unregister";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
}
