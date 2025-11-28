package com.library.domain;

import java.io.Serializable;
import java.time.LocalDate;

public class BorrowRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bookTitle;
    private LocalDate borrowDate;
    private LocalDate deadline;
    private boolean returned;

    public BorrowRecord(String bookTitle, LocalDate borrowDate, LocalDate deadline) {
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.deadline = deadline;
        this.returned = false;
    }

    public String getBookTitle() { return bookTitle; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDeadline() { return deadline; }
    public boolean isReturned() { return returned; }

    public void setReturned(boolean returned) { this.returned = returned; }

    @Override
    public String toString() {
        return bookTitle + " (Borrowed: " + borrowDate + ", Deadline: " + deadline + ")";
    }
}
