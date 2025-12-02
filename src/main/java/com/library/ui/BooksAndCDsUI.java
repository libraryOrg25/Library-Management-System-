package com.library.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.library.domain.Book;
import com.library.persistence.FileStorage;

public class BooksAndCDsUI extends JFrame {

    public BooksAndCDsUI() {
        setTitle("Available Books & CDs");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ---------------- HEADER ----------------
        JPanel header = new JPanel();
        header.setBackground(new Color(200, 230, 255));
        header.setPreferredSize(new Dimension(1000, 60));
        header.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));

        JLabel title = new JLabel("AVAILABLE BOOKS & CDS");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ---------------- MAIN PANEL ----------------
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 4, 30, 40));
        mainPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // ---------------- LOAD BOOKS ----------------
        List<Book> books = FileStorage.loadBooks();
        System.out.println("Loaded books: " + books.size());

        if (books.isEmpty()) {
            JLabel noBooks = new JLabel("NO BOOKS AVAILABLE", SwingConstants.CENTER);
            noBooks.setFont(new Font("SansSerif", Font.BOLD, 20));
            add(noBooks, BorderLayout.CENTER);
            return;
        }

        // ---------------- DISPLAY EACH BOOK/CD ----------------
        for (Book b : books) {
            JPanel item = new JPanel();
            item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
            item.setBackground(Color.WHITE);

            JLabel t = new JLabel(b.getTitle(), SwingConstants.CENTER);
            t.setAlignmentX(Component.CENTER_ALIGNMENT);
            t.setFont(new Font("SansSerif", Font.BOLD, 16));

            JLabel a = new JLabel(b.getAuthor(), SwingConstants.CENTER);
            a.setAlignmentX(Component.CENTER_ALIGNMENT);
            a.setFont(new Font("SansSerif", Font.PLAIN, 14));

            JLabel isbn = new JLabel(b.getIsbn(), SwingConstants.CENTER);
            isbn.setAlignmentX(Component.CENTER_ALIGNMENT);
            isbn.setFont(new Font("SansSerif", Font.PLAIN, 14));

            JButton borrowBtn = new JButton("BORROW");
            borrowBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            borrowBtn.setBackground(new Color(180, 220, 255));

            item.add(t);
            item.add(a);
            item.add(isbn);
            item.add(Box.createVerticalStrut(10));
            item.add(borrowBtn);

            mainPanel.add(item);
        }

        setVisible(true);
    }
}
