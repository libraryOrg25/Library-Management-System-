package com.library.domain;

import java.io.Serializable;

public class Book implements Serializable {

    private String title;
    private String author;
    private String isbn;
    private String type;   // BOOK or CD
    private int copies;

    public Book(String title, String author, String isbn, String type, int copies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.type = type;
        this.copies = copies;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getType() {
        return type;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }
}
