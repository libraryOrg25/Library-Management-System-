package com.library.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String password;
    private String role; // "admin" or "user"
    private List<BorrowRecord> borrowedBooks;

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.borrowedBooks = new ArrayList<>();
    }

    // Getters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public List<BorrowRecord> getBorrowedBooks() { return borrowedBooks; }

    // Add a borrow record
    public void borrowBook(BorrowRecord record) {
        borrowedBooks.add(record);
    }

    // Return a book (by title)
    public void returnBook(String title) {
        borrowedBooks.removeIf(r -> r.getBookTitle().equalsIgnoreCase(title));
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", borrowedBooks=" + borrowedBooks.size() +
                '}';
    }
}
