package com.library.domain;

import java.io.Serializable;
import java.time.LocalDate;

public class BorrowRecord implements Serializable {

    private String bookTitle;
    private String isbn;
    private String type;    
    private LocalDate borrowDate;
    private LocalDate deadline;

    public BorrowRecord(String bookTitle, String isbn, String type, LocalDate borrowDate, LocalDate deadline) {
        this.bookTitle = bookTitle;
        this.isbn = isbn;
        this.type = type;
        this.borrowDate = borrowDate;
        this.deadline = deadline;
    }


    public String getBookTitle() {
        return bookTitle;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getType() {
        return type;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }
}
