package com.library.persistence;

import com.library.domain.Book;
import com.library.domain.BorrowRecord;
import com.library.domain.User;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class FileStorage {

    private static String USER_FILE = "users.txt";
    private static String BOOKS_FILE = "books.txt";

    // ----------- Setters to override paths for testing --------------
    public static void setUserFile(String path) {
        USER_FILE = path;
    }

    public static void setBooksFile(String path) {
        BOOKS_FILE = path;
    }

    // ------------------ LOAD USERS ----------------------
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File f = new File(USER_FILE);
        if (!f.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String meta;
            while ((meta = br.readLine()) != null) {

                if (meta.trim().isEmpty()) continue;

                String[] parts = meta.split(",", -1);
                if (parts.length < 4) continue;

                String username = parts[0];
                String email = parts[1];
                String password = parts[2];
                String role = parts[3];

                int fine = 0;
                if (parts.length >= 5) {
                    try { fine = Integer.parseInt(parts[4]); } catch (Exception ignored) {}
                }

                User u = new User(username, email, password, role);
                u.setFine(fine);

                String borrowedLine = br.readLine();
                List<BorrowRecord> recs = new ArrayList<>();

                if (borrowedLine != null && borrowedLine.startsWith("borrowed=")) {
                    String val = borrowedLine.substring("borrowed=".length());
                    if (!val.isEmpty()) {
                        for (String it : val.split(";")) {
                            String[] b = it.split("\\|");
                            if (b.length == 5) {
                                recs.add(new BorrowRecord(
                                        b[0], b[1], b[2],
                                        LocalDate.parse(b[3]),
                                        LocalDate.parse(b[4])
                                ));
                            }
                        }
                    }
                }

                u.setBorrowedBooks(recs);
                users.add(u);
            }
        } catch (IOException e) { e.printStackTrace(); }

        return users;
    }

    // ------------------- SAVE USERS ----------------------
    public static void saveUsers(List<User> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USER_FILE))) {
            for (User u : users) {

                pw.println(String.join(",",
                        u.getUsername(),
                        u.getEmail(),
                        u.getPassword(),
                        u.getRole(),
                        "" + u.getFine()
                ));

                if (u.getBorrowedBooks() == null || u.getBorrowedBooks().isEmpty()) {
                    pw.println("borrowed=");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (BorrowRecord r : u.getBorrowedBooks()) {
                        sb.append(r.getBookTitle()).append("|")
                                .append(r.getIsbn()).append("|")
                                .append(r.getType()).append("|")
                                .append(r.getBorrowDate()).append("|")
                                .append(r.getDeadline()).append(";");

                    }
                    pw.println("borrowed=" + sb);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // -------------------- FIND USER -----------------------
    public static User findUserByEmail(String email) {
        for (User u : loadUsers()) {
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        }
        return null;
    }

    // -------------------- UPDATE USER ---------------------
    public static void updateUser(User updated) {
        List<User> users = loadUsers();

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equalsIgnoreCase(updated.getEmail())) {
                users.set(i, updated);
                break;
            }
        }
        saveUsers(users);
    }

    // ------------------- ADD BOOK -------------------------
    public static void addBook(Book b) {
        List<Book> books = loadBooks();
        books.add(b);
        updateBooksFile(books);
    }

    // ------------------- REMOVE USER -----------------------
    public static void removeUserByEmail(String email) {
        List<User> users = loadUsers();
        users.removeIf(u -> u.getEmail().equalsIgnoreCase(email));
        saveUsers(users);
    }

    // -------------------- LOAD BOOKS -----------------------
    public static List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();

        File f = new File(BOOKS_FILE);
        if (!f.exists()) return books;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);

                if (p.length == 5) {
                    books.add(new Book(
                            p[0], p[1], p[2], p[3],
                            Integer.parseInt(p[4])
                    ));
                }
            }

        } catch (IOException e) { e.printStackTrace(); }

        return books;
    }

    // ---------------- SAVE BOOKS --------------------------
    public static void updateBooksFile(List<Book> books) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : books) {
                pw.println(String.join(",",
                        b.getTitle(),
                        b.getAuthor(),
                        b.getIsbn(),
                        b.getType(),
                        "" + b.getCopies()
                ));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ------------------ PERFORM BORROW ---------------------
    public static void performBorrow(String email, Book book) {

        LocalDate now = LocalDate.now();
        int days = book.getType().equalsIgnoreCase("CD") ? 7 : 28;

        BorrowRecord rec = new BorrowRecord(
                book.getTitle(),
                book.getIsbn(),
                book.getType(),
                now,
                now.plusDays(days)
        );

        addBorrowToUser(email, rec);

        List<Book> books = loadBooks();

        for (Book b : books) {
            if (b.getIsbn().equalsIgnoreCase(book.getIsbn())) {
                b.setCopies(b.getCopies() - 1);
                break;
            }
        }

        updateBooksFile(books);
    }

    // ------------------ PERFORM RETURN ---------------------
    public static void performReturn(String email, BorrowRecord rec) {

        removeBorrowFromUser(email, rec);

        List<Book> books = loadBooks();

        for (Book b : books) {
            if (b.getIsbn().equalsIgnoreCase(rec.getIsbn())) {
                b.setCopies(b.getCopies() + 1);
                break;
            }
        }

        updateBooksFile(books);
    }

    // ---------------- ADD BORROW ---------------------------
    private static void addBorrowToUser(String email, BorrowRecord rec) {
        List<User> users = loadUsers();

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                if (u.getBorrowedBooks() == null)
                    u.setBorrowedBooks(new ArrayList<>());

                u.getBorrowedBooks().add(rec);
                break;
            }
        }
        saveUsers(users);
    }

    // ---------------- REMOVE BORROW ------------------------
    private static void removeBorrowFromUser(String email, BorrowRecord rec) {
        List<User> users = loadUsers();

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {

                if (u.getBorrowedBooks() != null) {
                    u.getBorrowedBooks().removeIf(r ->
                            r.getIsbn().equals(rec.getIsbn()) &&
                            r.getBookTitle().equals(rec.getBookTitle())
                    );
                }
                break;
            }
        }

        saveUsers(users);
    }

	public static String getUserFile() {
		// TODO Auto-generated method stub
		return null;
	}
}
