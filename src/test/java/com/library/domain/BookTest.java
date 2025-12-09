package com.library.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void testConstructorAndGetters() {
        Book b = new Book("Java Programming", "Fatima", "12345", "Book", 5);

        assertEquals("Java Programming", b.getTitle());
        assertEquals("Fatima", b.getAuthor());
        assertEquals("12345", b.getIsbn());
        assertEquals("Book", b.getType());
        assertEquals(5, b.getCopies());
    }

    @Test
    void testSetCopies() {
        Book b = new Book("Networks", "Ahmad", "98765", "CD", 3);

        b.setCopies(10);
        assertEquals(10, b.getCopies());

        b.setCopies(0);
        assertEquals(0, b.getCopies());
    }
}
