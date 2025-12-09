package com.library.service;

import com.library.domain.BorrowRecord;
import com.library.domain.User;
import com.library.persistence.FileStorage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReminderService {

    private final IEmailService emailService;

    public ReminderService(IEmailService emailService) {
        this.emailService = emailService;
    }

    public void sendLateReturnReminders() {
        List<User> users = FileStorage.loadUsers();

        for (User u : users) {

            if (u.getBorrowedBooks() == null) continue;

            for (BorrowRecord r : u.getBorrowedBooks()) {

                if (LocalDate.now().isAfter(r.getDeadline())) {
                    long lateDays = ChronoUnit.DAYS.between(r.getDeadline(), LocalDate.now());

                    String msg = "You are " + lateDays +
                            " days late returning: " + r.getBookTitle();

                    emailService.send(u.getEmail(), "Late Book Reminder", msg);
                }
            }
        }
    }
}
