package com.library.domain;

import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testUserFields() {
        User u = new User("fatima", "fatima@gmail.com", "1234", "user");

        assertEquals("fatima", u.getUsername());
        assertEquals("fatima@gmail.com", u.getEmail());
        assertEquals("1234", u.getPassword());
        assertEquals("user", u.getRole());
        assertEquals(0, u.getFine());
    }

    @Test
    void testSetFine() {
        User u = new User("a", "b", "c", "user");
        u.setFine(20);
        assertEquals(20, u.getFine());
    }

    @Test
    void testBorrowedBooksList() {
        User u = new User("x", "x@gmail.com", "1", "user");

        BorrowRecord r = new BorrowRecord("Java", "111", "BOOK",
                java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(1));

        u.setBorrowedBooks(Arrays.asList(r));

        assertEquals(1, u.getBorrowedBooks().size());
        assertEquals("Java", u.getBorrowedBooks().get(0).getBookTitle());
    }
}
