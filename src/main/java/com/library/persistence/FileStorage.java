package com.library.persistence;

import com.library.domain.Book;
import com.library.domain.BorrowRecord;
import com.library.domain.User;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class FileStorage {

    private static String getResourcePath(String filename) {
        try {
            URL resource = FileStorage.class.getClassLoader().getResource(filename);
            if (resource == null) {
                throw new RuntimeException("Resource not found: " + filename);
            }
            return new File(resource.toURI()).getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("Cannot load resource: " + filename + " -> " + e.getMessage());
        }
    }

    private static String USER_FILE = getResourcePath("users.txt");
    private static String BOOKS_FILE = getResourcePath("books.txt");
    private static final String BORROWED_PREFIX = "borrowed=";
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(FileStorage.class.getName());

    public static void setUserFile(String path) { USER_FILE = path; }
    public static void setBooksFile(String path) { BOOKS_FILE = path; }

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

                // load borrowed record line
                String borrowedLine = br.readLine();
                List<BorrowRecord> recs = new ArrayList<>();

                if (borrowedLine != null && borrowedLine.startsWith(BORROWED_PREFIX)) {
                    String val = borrowedLine.substring(BORROWED_PREFIX.length());
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
        } catch (IOException e) {
        	LOGGER.severe("Failed to load users: " + e.getMessage());
        }

        return users;
    }
 
    
    
    
    
    
       
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
                    pw.println(BORROWED_PREFIX);
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (BorrowRecord r : u.getBorrowedBooks()) {
                        sb.append(r.getBookTitle()).append("|")
                                .append(r.getIsbn()).append("|")
                                .append(r.getType()).append("|")
                                .append(r.getBorrowDate()).append("|")
                                .append(r.getDeadline()).append(";");
                    }
                    pw.println(BORROWED_PREFIX + sb);
                }
            }
        } catch (IOException e) {LOGGER.severe("Failed to update books file: " + e.getMessage()); }
    }

    // ===========================
    // FIND USER
    // ===========================
    public static User findUserByEmail(String email) {
        for (User u : loadUsers()) {
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        }
        return null;
    }

    // ===========================
    // UPDATE USER
    // ===========================
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

    // ===========================
    // LOAD BOOKS
    // ===========================
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

        } 
        catch (IOException e) {
            LOGGER.severe("Failed to load books: " + e.getMessage());
        }
        return books;
    }

    // ===========================
    // SAVE BOOKS
    // ===========================
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
        } 
        catch (IOException e) {
            LOGGER.severe("Failed to update books file: " + e.getMessage());
        }
        
        
          
    }

    // ===========================
    //   BORROW / RETURN
    // ===========================
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
	public static Object addBook(Book any) {
		// TODO Auto-generated method stub
		return null;
	}
}
