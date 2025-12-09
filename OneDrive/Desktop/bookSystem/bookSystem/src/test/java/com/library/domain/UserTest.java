package com.library.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testConstructorAndGetters() {
        User u = new User("Fatima", "fatima@test.com", "1234", "admin");

        assertEquals("Fatima", u.getUsername());
        assertEquals("fatima@test.com", u.getEmail());
        assertEquals("1234", u.getPassword());
        assertEquals("admin", u.getRole());
        assertEquals(0, u.getFine());
        assertNotNull(u.getBorrowedBooks());
        assertTrue(u.getBorrowedBooks().isEmpty());
    }

    @Test
    void testSetFine() {
        User u = new User("F", "e", "p", "user");
        u.setFine(50);
        assertEquals(50, u.getFine());
    }

    @Test
    void testSetBorrowedBooks() {
        User u = new User("F", "e", "p", "user");

        List<BorrowRecord> list = new ArrayList<>();
        list.add(new BorrowRecord(
                "Java Book",
                "111",
                "BOOK",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 10)
        ));

        u.setBorrowedBooks(list);

        assertEquals(1, u.getBorrowedBooks().size());
        assertEquals("Java Book", u.getBorrowedBooks().get(0).getBookTitle());
    }

    @Test
    void testSetPasswordDoesNothing() {
        User u = new User("F", "e", "1234", "user");

        u.setPassword("newpass");

        // method is empty so password MUST remain unchanged
        assertEquals("1234", u.getPassword());
    }
}
