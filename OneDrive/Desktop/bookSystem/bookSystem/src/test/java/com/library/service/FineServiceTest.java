package com.library.service;

import com.library.domain.BorrowRecord;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class FineServiceTest {

    private FineService fs;

    @BeforeEach
    void setup() {
        fs = new FineService();
    }

    @Test
    void testBookFine() {

        LocalDate today = LocalDate.now();

        BorrowRecord r = new BorrowRecord(
                "Java", "11", "Book",
                today.minusDays(10),
                today.minusDays(5)
        );

        // 5 days late → 5 * 10 = 50
        assertEquals(50, fs.calculateItemFine(r, today));
    }

    @Test
    void testCDFine() {

        LocalDate today = LocalDate.now();

        BorrowRecord r = new BorrowRecord(
                "Music", "22", "CD",
                today.minusDays(15),
                today.minusDays(3)
        );

        // 3 days late → 3 * 20 = 60
        assertEquals(60, fs.calculateItemFine(r, today));
    }

    @Test
    void testNoFine() {

        LocalDate today = LocalDate.now();

        BorrowRecord r = new BorrowRecord(
                "Clean Code", "33", "Book",
                today,
                today.plusDays(8)
        );

        // Not late
        assertEquals(0, fs.calculateItemFine(r, today));
    }
}
