package com.library.service;

import com.library.domain.BorrowRecord;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FineService {

    /**
     * احسب غرامة عنصر واحد بناءً على نوعه وعدد الأيام المتأخرة
     */
    public int calculateItemFine(BorrowRecord record, LocalDate today) {

        long daysLate = today.isAfter(record.getDeadline())
                ? ChronoUnit.DAYS.between(record.getDeadline(), today)
                : 0;

        if (daysLate <= 0) return 0;

        if (record.getType().equalsIgnoreCase("CD")) {
            return (int) daysLate * 20;
        } else {
            return (int) daysLate * 10;
        }
    }
}
