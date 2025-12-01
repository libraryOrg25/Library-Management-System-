package com.library.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BorrowRecordTest {

    @Test
    void testBorrowRecordFields() {
        LocalDate d1 = LocalDate.of(2024,1,1);
        LocalDate d2 = LocalDate.of(2024,1,10);

        BorrowRecord r = new BorrowRecord("Java", "1111", "BOOK", d1, d2);

        assertEquals("Java", r.getBookTitle());
        assertEquals("1111", r.getIsbn());
        assertEquals("BOOK", r.getType());
        assertEquals(d1, r.getBorrowDate());
        assertEquals(d2, r.getDeadline());
    }
}
