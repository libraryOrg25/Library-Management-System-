package com.library.service;

import com.library.domain.BorrowRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FineServiceTest {

    private final FineService fineService = new FineService();

    @Test
    void testNoLateFine() {
        BorrowRecord r = new BorrowRecord(
                "Book A", "111", "book",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 10)
        );

        int fine = fineService.calculateItemFine(r, LocalDate.of(2025, 1, 9));
        assertEquals(0, fine);
    }

    @Test
    void testBookLateFine() {
        BorrowRecord r = new BorrowRecord(
                "Book B", "222", "book",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 10)
        );

        int fine = fineService.calculateItemFine(r, LocalDate.of(2025, 1, 12));
        assertEquals(20, fine); // 2 days * 10
    }

    @Test
    void testCDLateFine() {
        BorrowRecord r = new BorrowRecord(
                "Music CD", "333", "CD",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 10)
        );

        int fine = fineService.calculateItemFine(r, LocalDate.of(2025, 1, 15));
        assertEquals(100, fine); // 5 days * 20
    }

    @Test
    void testTodayBeforeDeadline() {
        BorrowRecord r = new BorrowRecord(
                "Book C", "444", "BOOK",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 2, 1)
        );

        int fine = fineService.calculateItemFine(r, LocalDate.of(2025, 1, 20));
        assertEquals(0, fine);
    }

    @Test
    void testCaseInsensitiveType() {
        BorrowRecord r = new BorrowRecord(
                "CD test", "555", "cD",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 10)
        );

        int fine = fineService.calculateItemFine(r, LocalDate.of(2025, 1, 12));
        assertEquals(40, fine);
    }
}
