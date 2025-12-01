package com.library.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    void testBookFields() {
        Book b = new Book("Java", "Author", "1111", "BOOK", 5);

        assertEquals("Java", b.getTitle());
        assertEquals("Author", b.getAuthor());
        assertEquals("1111", b.getIsbn());
        assertEquals("BOOK", b.getType());
        assertEquals(5, b.getCopies());
    }

    @Test
    void testSetCopies() {
        Book b = new Book("Test", "A", "X", "BOOK", 5);
        b.setCopies(10);
        assertEquals(10, b.getCopies());
    }
}
