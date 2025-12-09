package com.library.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BorrowRecordTest {

    @Test
    void testConstructorAndGetters() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        LocalDate deadline = LocalDate.of(2025, 1, 10);

        BorrowRecord record = new BorrowRecord(
                "Java Programming",
                "ISBN123",
                "BOOK",
                borrowDate,
                deadline
        );

        assertEquals("Java Programming", record.getBookTitle());
        assertEquals("ISBN123", record.getIsbn());
        assertEquals("BOOK", record.getType());
        assertEquals(borrowDate, record.getBorrowDate());
        assertEquals(deadline, record.getDeadline());
    }
}
