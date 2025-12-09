package com.library.ui;

import com.library.domain.Book;
import com.library.domain.BorrowRecord;
import com.library.domain.User;
import com.library.persistence.FileStorage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AdminDashboard extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel addBookPanel, usersPanel, emailPanel;
    private JTextField titleField, authorField, isbnField, typeField, copiesField;

    public AdminDashboard() {
        setTitle("Library Admin Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JButton logout = new JButton("LOG OUT");
        logout.setBounds(20, 10, 100, 30);
        logout.addActionListener(e -> {
            dispose();
            new LoginUI().setVisible(true);
        });
        add(logout);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(0, 50, 980, 500);
        add(tabbedPane);

        addBookPanel = createAddBookPanel();
        usersPanel = createUsersInfoPanel();
        emailPanel = createEmailPanel();

        tabbedPane.addTab("ADD BOOKS", addBookPanel);
        tabbedPane.addTab("USERS INFORMATION", usersPanel);
        tabbedPane.addTab("SEND EMAIL", emailPanel);
    }

    public JPanel createAddBookPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel t1 = new JLabel("BOOK TITLE");
        t1.setBounds(80, 40, 200, 25);
        panel.add(t1);

        titleField = new JTextField();
        titleField.setBounds(80, 65, 400, 35);
        panel.add(titleField);

        JLabel t2 = new JLabel("AUTHOR");
        t2.setBounds(80, 110, 200, 25);
        panel.add(t2);

        authorField = new JTextField();
        authorField.setBounds(80, 135, 400, 35);
        panel.add(authorField);

        JLabel t3 = new JLabel("ISBN");
        t3.setBounds(80, 180, 200, 25);
        panel.add(t3);

        isbnField = new JTextField();
        isbnField.setBounds(80, 205, 400, 35);
        panel.add(isbnField);

        JLabel t4 = new JLabel("TYPE (Book/CD)");
        t4.setBounds(80, 250, 200, 25);
        panel.add(t4);

        typeField = new JTextField();
        typeField.setBounds(80, 275, 200, 35);
        panel.add(typeField);

        JLabel t5 = new JLabel("COPIES");
        t5.setBounds(320, 250, 100, 25);
        panel.add(t5);

        copiesField = new JTextField();
        copiesField.setBounds(320, 275, 160, 35);
        panel.add(copiesField);

        JButton addBtn = new JButton("ADD");
        addBtn.setBounds(200, 340, 160, 40);
        addBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            String type = typeField.getText().trim();
            int copies = 0;
            try { copies = Integer.parseInt(copiesField.getText().trim()); } catch (Exception ex) {}

            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || type.isEmpty() || copies <= 0) {
                JOptionPane.showMessageDialog(this, "Please fill all fields and copies must be > 0");
                return;
            }

            Book b = new Book(title, author, isbn, type, copies);
            FileStorage.addBook(b);
            JOptionPane.showMessageDialog(this, "Book added.");
            titleField.setText(""); authorField.setText(""); isbnField.setText(""); typeField.setText(""); copiesField.setText("");
        });
        panel.add(addBtn);

        return panel;
    }

    private JPanel createUsersInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Registered Users", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        // define column names exactly once
        String[] cols = {"USER NAME", "EMAIL", "BORROWED", "BORROW DATE", "DEADLINE", "STATUS", "REMOVE"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            // make cells non-editable except editor column
            @Override
            public boolean isCellEditable(int row, int column) {
                // only REMOVE column (last) should be editable so our editor can work
                return column == (getColumnCount() - 1);
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);

        // load users
        List<User> users = FileStorage.loadUsers();
        for (User u : users) {
            if (u.getBorrowedBooks() == null || u.getBorrowedBooks().isEmpty()) {
                model.addRow(new Object[]{
                        u.getUsername(),
                        u.getEmail(),
                        "-", "-", "-",
                        u.getFine() == 0 ? "No" : "Fine: " + u.getFine(),
                        "Remove"
                });
            } else {
                for (BorrowRecord r : u.getBorrowedBooks()) {
                    String status = "No";
                    if (r.getDeadline().isBefore(LocalDate.now())) {
                        long daysLate = ChronoUnit.DAYS.between(r.getDeadline(), LocalDate.now());
                        status = daysLate > 0 ? "Late (" + daysLate + "d)" : "No";
                    }
                    model.addRow(new Object[]{
                            u.getUsername(),
                            u.getEmail(),
                            r.getBookTitle(),
                            r.getBorrowDate(),
                            r.getDeadline(),
                            (u.getFine() == 0 ? status : "Fine: " + u.getFine()),
                            "Remove"
                    });
                }
            }
        }

        // safe: check column index of REMOVE, then set renderer/editor
        int removeColIndex = -1;
        for (int i = 0; i < model.getColumnCount(); i++) {
            if ("REMOVE".equalsIgnoreCase(model.getColumnName(i))) {
                removeColIndex = i;
                break;
            }
        }

        if (removeColIndex >= 0) {
            table.getColumnModel().getColumn(removeColIndex).setCellRenderer(new ButtonRenderer());
            table.getColumnModel().getColumn(removeColIndex).setCellEditor(new ButtonEditor(new JCheckBox(), model));
        } else {
            System.err.println("Warning: REMOVE column not found - cannot attach button editor.");
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEmailPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel l1 = new JLabel("User Email:");
        l1.setBounds(60, 60, 120, 25);
        panel.add(l1);

        JTextField email = new JTextField();
        email.setBounds(180, 60, 360, 30);
        panel.add(email);

        JLabel l2 = new JLabel("Message:");
        l2.setBounds(60, 110, 120, 25);
        panel.add(l2);

        JTextField msg = new JTextField("You have an important library message.");
        msg.setBounds(180, 110, 360, 30);
        panel.add(msg);

        JButton send = new JButton("SEND");
        send.setBounds(260, 160, 140, 40);
        send.addActionListener(e -> {
            String to = email.getText().trim();
            String message = msg.getText().trim();
            if (to.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter email"); return; }
            try {
                new com.library.service.EmailService(message, message).send(to, "Library Notification", message);
                JOptionPane.showMessageDialog(this, "Email sent");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Send failed: " + ex.getMessage());
            }
        });
        panel.add(send);

        return panel;
    }

    // table button renderer/editor
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            setText("Remove");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JButton btn = new JButton("Remove");
        private final DefaultTableModel model;
        private int editingRow = -1;

        public ButtonEditor(JCheckBox cb, DefaultTableModel model) {
            super(cb);
            this.model = model;
            btn.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            editingRow = row;
            return btn;
        }

        @Override
        public Object getCellEditorValue() {

            String email = (String) model.getValueAt(editingRow, 1);

            int confirm = JOptionPane.showConfirmDialog(AdminDashboard.this,
                    "Remove user " + email + " ?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
    FileStorage.findUserByEmail(email);

                // 2) احذف كل الصفوف اللي إلها نفس الايميل من الجدول
                for (int i = model.getRowCount() - 1; i >= 0; i--) {
                    String rowEmail = (String) model.getValueAt(i, 1);
                    if (rowEmail.equalsIgnoreCase(email)) {
                        model.removeRow(i);
                    }
                }

                JOptionPane.showMessageDialog(AdminDashboard.this, "User removed.");
            }

            return "Remove";
        }

    }
}
