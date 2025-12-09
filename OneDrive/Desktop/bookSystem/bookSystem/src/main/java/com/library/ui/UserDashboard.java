package com.library.ui;

import com.library.domain.Book;
import com.library.domain.BorrowRecord;
import com.library.domain.User;
import com.library.persistence.FileStorage;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class UserDashboard extends JFrame {

    private JPanel booksPanel, searchPanel, myBooksPanel, finesPanel;
    private String currentUserEmail;

    public UserDashboard(String userEmail) {
        this.currentUserEmail = userEmail;

        setTitle("Library System - User");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // ===================== TOP MENU ======================
        JPanel topMenu = new JPanel(null);
        topMenu.setBackground(new Color(210, 235, 255));
        topMenu.setBounds(0, 0, 1100, 60);

        JButton logout = new JButton("LOG OUT");
        logout.setBounds(10, 10, 100, 40);
        logout.addActionListener(e -> {
            dispose();
            new LoginUI().setVisible(true);
        });
        topMenu.add(logout);

        JButton searchBtn = createMenuButton("SEARCH", 140);
        JButton booksBtn = createMenuButton("BOOKS & CD", 280);
        JButton myBooksBtn = createMenuButton("MY BOOKS", 450);
        JButton finesBtn = createMenuButton("FINES", 600);

        topMenu.add(searchBtn);
        topMenu.add(booksBtn);
        topMenu.add(myBooksBtn);
        topMenu.add(finesBtn);
        add(topMenu);

        // ===================== CARD LAYOUT CONTENT ======================
        JPanel content = new JPanel(new CardLayout());
        content.setBounds(0, 60, 1100, 590);
        add(content);

        booksPanel = createBooksPanel();
        searchPanel = createSearchPanel();
        myBooksPanel = createMyBooksPanel();
        finesPanel = createFinesPanel();

        content.add(booksPanel, "BOOKS");
        content.add(searchPanel, "SEARCH");
        content.add(myBooksPanel, "MYBOOKS");
        content.add(finesPanel, "FINES");

        // ===================== MENU BUTTONS LOGIC ======================
        booksBtn.addActionListener(e -> {
            int fine = getCurrentUserFine();
            if (fine > 0) {
                JOptionPane.showMessageDialog(this,
                        "You have unpaid fines (" + fine + " NIS). Please pay them first.");
                return;
            }

            booksPanel = createBooksPanel();
            content.add(booksPanel, "BOOKS");
            CardLayout cl = (CardLayout) content.getLayout();
            cl.show(content, "BOOKS");
        });

        searchBtn.addActionListener(e -> {
            CardLayout cl = (CardLayout) content.getLayout();
            cl.show(content, "SEARCH");
        });

        myBooksBtn.addActionListener(e -> {
            myBooksPanel = createMyBooksPanel();
            content.add(myBooksPanel, "MYBOOKS");
            CardLayout cl = (CardLayout) content.getLayout();
            cl.show(content, "MYBOOKS");
        });

        finesBtn.addActionListener(e -> {
            finesPanel = createFinesPanel();
            content.add(finesPanel, "FINES");
            CardLayout cl = (CardLayout) content.getLayout();
            cl.show(content, "FINES");
        });
    }

    // ============================================================
    private JButton createMenuButton(String txt, int x) {
        JButton b = new JButton(txt);
        b.setBounds(x, 10, 120, 40);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setBackground(new Color(210, 235, 255));
        return b;
    }

    private int getCurrentUserFine() {
        User u = FileStorage.findUserByEmail(currentUserEmail);
        if (u == null) return 0;
        return u.getFine();
    }

    // ============================================================
    // BOOKS PANEL
    // ============================================================
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel msg = new JLabel("AVAILABLE BOOKS & CDS", SwingConstants.CENTER);
        msg.setBounds(50, 10, 1000, 40);
        msg.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(msg);

        List<Book> books = FileStorage.loadBooks();
        if (books.isEmpty()) {
            JLabel empty = new JLabel("NO BOOKS FOUND", SwingConstants.CENTER);
            empty.setBounds(0, 200, 1100, 30);
            panel.add(empty);
            return panel;
        }

        int x = 60, y = 80, count = 0;

        for (Book b : books) {
            JPanel item = new JPanel(null);
            item.setBounds(x, y, 220, 200);
            item.setBackground(Color.WHITE);

            JLabel title = new JLabel(b.getTitle(), SwingConstants.CENTER);
            title.setBounds(0, 0, 220, 25);
            title.setFont(new Font("Arial", Font.BOLD, 15));
            item.add(title);

            JLabel author = new JLabel("Author: " + b.getAuthor(), SwingConstants.CENTER);
            author.setBounds(0, 30, 220, 20);
            item.add(author);

            JLabel isbn = new JLabel("ISBN: " + b.getIsbn(), SwingConstants.CENTER);
            isbn.setBounds(0, 55, 220, 20);
            item.add(isbn);

            JLabel type = new JLabel("Type: " + b.getType(), SwingConstants.CENTER);
            type.setBounds(0, 80, 220, 20);
            item.add(type);

            JLabel copies = new JLabel("Copies: " + b.getCopies(), SwingConstants.CENTER);
            copies.setBounds(0, 105, 220, 20);
            item.add(copies);

            JButton borrow = new JButton("BORROW");
            borrow.setBounds(60, 140, 100, 30);
            borrow.setBackground(new Color(190, 230, 255));
            item.add(borrow);

            if (b.getCopies() <= 0) borrow.setEnabled(false);

            borrow.addActionListener(e -> {
                int fine = getCurrentUserFine();
                if (fine > 0) {
                    JOptionPane.showMessageDialog(this, "You have unpaid fines (" + fine + " NIS). Pay them first.");
                    return;
                }
                if (b.getCopies() <= 0) {
                    JOptionPane.showMessageDialog(this, "No copies available.");
                    return;
                }

                FileStorage.performBorrow(currentUserEmail, b);
                copies.setText("Copies: " + (b.getCopies() - 1));
                JOptionPane.showMessageDialog(this, "Borrowed successfully.");
            });

            panel.add(item);

            x += 260;
            count++;

            if (count % 4 == 0) {
                x = 60;
                y += 240;
            }
        }

        return panel;
    }

    // ============================================================
    // SEARCH PANEL
    // ============================================================
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JTextField titleField = new JTextField("BOOK TITLE");
        titleField.setBounds(80, 80, 940, 50);
        titleField.setBackground(Color.BLACK);
        titleField.setForeground(Color.WHITE);
        panel.add(titleField);

        JTextField authorField = new JTextField("Author");
        authorField.setBounds(80, 150, 940, 50);
        authorField.setBackground(Color.BLACK);
        authorField.setForeground(Color.WHITE);
        panel.add(authorField);

        JTextField isbnField = new JTextField("ISBN");
        isbnField.setBounds(80, 220, 940, 50);
        isbnField.setBackground(Color.BLACK);
        isbnField.setForeground(Color.WHITE);
        panel.add(isbnField);

        JButton search = new JButton("SEARCH");
        search.setBounds(350, 300, 400, 50);
        panel.add(search);

        JPanel results = new JPanel(null);
        results.setBounds(0, 380, 1100, 170);
        panel.add(results);

        search.addActionListener(e -> {
            results.removeAll();

            List<Book> books = FileStorage.loadBooks();

            String t = titleField.getText().trim().toLowerCase();
            String a = authorField.getText().trim().toLowerCase();
            String i = isbnField.getText().trim().toLowerCase();

            int x = 50;

            for (Book b : books) {
                boolean match =
                        (t.isEmpty() || b.getTitle().toLowerCase().contains(t)) &&
                        (a.isEmpty() || b.getAuthor().toLowerCase().contains(a)) &&
                        (i.isEmpty() || b.getIsbn().toLowerCase().contains(i));

                if (match) {
                    JPanel item = new JPanel(null);
                    item.setBounds(x, 10, 220, 160);

                    JLabel title = new JLabel(b.getTitle(), SwingConstants.CENTER);
                    title.setBounds(0, 0, 220, 25);
                    item.add(title);

                    JLabel author = new JLabel("Author: " + b.getAuthor(), SwingConstants.CENTER);
                    author.setBounds(0, 30, 220, 20);
                    item.add(author);

                    JLabel isbn = new JLabel("ISBN: " + b.getIsbn(), SwingConstants.CENTER);
                    isbn.setBounds(0, 55, 220, 20);
                    item.add(isbn);

                    JButton borrow = new JButton("BORROW");
                    borrow.setBounds(60, 100, 100, 30);
                    item.add(borrow);

                    borrow.addActionListener(ev -> {
                        if (b.getCopies() <= 0) {
                            JOptionPane.showMessageDialog(this, "No copies.");
                            return;
                        }

                        int fine = getCurrentUserFine();
                        if (fine > 0) {
                            JOptionPane.showMessageDialog(this, "Pay fines first.");
                            return;
                        }

                        FileStorage.performBorrow(currentUserEmail, b);
                        JOptionPane.showMessageDialog(this, "Borrowed.");
                    });

                    results.add(item);
                    x += 260;
                }
            }

            results.revalidate();
            results.repaint();
        });

        return panel;
    }

    // ============================================================
    // MY BOOKS PANEL
    // ============================================================
    private JPanel createMyBooksPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel header = new JLabel("MY BORROWED BOOKS", SwingConstants.CENTER);
        header.setBounds(0, 20, 1100, 40);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(header);

        User u = FileStorage.findUserByEmail(currentUserEmail);

        if (u == null || u.getBorrowedBooks() == null || u.getBorrowedBooks().isEmpty()) {
            JLabel empty = new JLabel("YOU HAVE NO BORROWED BOOKS", SwingConstants.CENTER);
            empty.setBounds(0, 150, 1100, 30);
            panel.add(empty);
            return panel;
        }

        int y = 90;

        for (BorrowRecord r : u.getBorrowedBooks()) {

            JPanel item = new JPanel(null);
            item.setBounds(50, y, 1000, 80);
            item.setBackground(Color.WHITE);

            JLabel title = new JLabel(r.getBookTitle());
            title.setFont(new Font("Arial", Font.BOLD, 16));
            title.setBounds(20, 8, 500, 25);
            item.add(title);

            JLabel info1 = new JLabel("Borrowed on: " + r.getBorrowDate());
            info1.setBounds(20, 35, 400, 20);
            item.add(info1);

            JLabel info2 = new JLabel("Return before: " + r.getDeadline());
            info2.setBounds(300, 35, 400, 20);
            item.add(info2);

            JButton ret = new JButton("RETURN");
            ret.setBounds(760, 20, 100, 35);
            item.add(ret);

            ret.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Return \"" + r.getBookTitle() + "\"?", "Confirm",
                        JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION) return;

                FileStorage.performReturn(currentUserEmail, r);
                JOptionPane.showMessageDialog(this, "Returned. Thank you!");

                this.dispose();
                new UserDashboard(currentUserEmail).setVisible(true);
            });

            panel.add(item);
            y += 100;
        }

        return panel;
    }

    // ============================================================
    // FINES PANEL
    // ============================================================
    private JPanel createFinesPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel header = new JLabel("FINES", SwingConstants.CENTER);
        header.setBounds(0, 10, 1100, 30);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(header);

        User u = FileStorage.findUserByEmail(currentUserEmail);
        if (u == null) {
            JLabel empty = new JLabel("User not found", SwingConstants.CENTER);
            empty.setBounds(0, 150, 1100, 30);
            panel.add(empty);
            return panel;
        }

        int total = u.getFine();
        int y = 60;

        if (u.getBorrowedBooks() != null) {
            for (BorrowRecord r : u.getBorrowedBooks()) {
                LocalDate due = r.getDeadline();
                LocalDate today = LocalDate.now();

                long daysLate = today.isAfter(due) ? ChronoUnit.DAYS.between(due, today) : 0;

                int itemFine = 0;
                if (daysLate > 0) {
                    itemFine = r.getType().equalsIgnoreCase("CD") ?
                            (int) daysLate * 20 : (int) daysLate * 10;
                }

                total += itemFine;

                JLabel item = new JLabel(
                        r.getBookTitle() + " (" + r.getType() + ") - Late days: "
                                + daysLate + " - Fine: " + itemFine + " NIS"
                );
                item.setBounds(50, y, 800, 25);
                panel.add(item);

                y += 30;
            }
        }

        JLabel tot = new JLabel("TOTAL FINE BALANCE = " + total + " NIS");
        tot.setBounds(50, y + 20, 400, 30);
        tot.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(tot);

        JButton payFull = new JButton("PAY FULL");
        payFull.setBounds(500, y, 120, 35);
        payFull.addActionListener(e -> {
            u.setFine(0);
            FileStorage.updateUser(u);
            JOptionPane.showMessageDialog(this, "Paid full. Thank you!");

            this.dispose();
            new UserDashboard(currentUserEmail).setVisible(true);
        });
        panel.add(payFull);

        JButton payPartial = new JButton("PAY 5 NIS");
        payPartial.setBounds(640, y, 120, 35);
        payPartial.addActionListener(e -> {
            int f = u.getFine();
            int newf = Math.max(0, f - 5);

            u.setFine(newf);
            FileStorage.updateUser(u);

            JOptionPane.showMessageDialog(this, "5 NIS deducted. Remaining: " + newf);

            this.dispose();
            new UserDashboard(currentUserEmail).setVisible(true);
        });

        panel.add(payPartial);

        return panel;
    }
}
