package com.library.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String username;
    private String email;
    private String password;
    private String role; // "user" or "admin"
    private int fine;    // total fine balance

    // Borrowed Records
    private List<BorrowRecord> borrowedBooks = new ArrayList<>();

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.fine = 0;
    }

    // ----------- Getters & Setters ------------

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public int getFine() {
        return fine;
    }

    public void setFine(int fine) {
        this.fine = fine;
    }

    public List<BorrowRecord> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(List<BorrowRecord> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

	public void setPassword(String string) {
		// TODO Auto-generated method stub
		
	}
}
