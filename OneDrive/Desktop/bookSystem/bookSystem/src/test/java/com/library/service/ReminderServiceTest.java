package com.library.service;

import com.library.domain.BorrowRecord;
import com.library.domain.User;
import com.library.persistence.FileStorage;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

class ReminderServiceTest {

    private IEmailService emailService;
    private ReminderService reminderService;
    private MockedStatic<FileStorage> fileStorageMock;

    @BeforeEach
    void setup() {
        emailService = mock(IEmailService.class);
        reminderService = new ReminderService(emailService);

        fileStorageMock = mockStatic(FileStorage.class);
    }

    @AfterEach
    void tearDown() {
        fileStorageMock.close();
    }

    @Test
    void testSendLateReturnReminders_sendsEmailToLateUsers() {

        // --- بيانات مستخدم واحد عنده كتاب متأخر ---
        BorrowRecord lateRecord = new BorrowRecord(
                "Java Book",
                "12345",
                "Book",
                LocalDate.now().minusDays(10),   // borrow date
                LocalDate.now().minusDays(5)     // deadline = 5 days late
        );

        User u = new User("fatima", "test@example.com", "123", "user");
        u.getBorrowedBooks().add(lateRecord);

        fileStorageMock.when(FileStorage::loadUsers)
                .thenReturn(List.of(u));

        // --- نفذ الفانكشن ---
        reminderService.sendLateReturnReminders();

        // --- تحقق أن الإيميل انبعت مرة واحدة ---
        verify(emailService, times(1))
                .send(eq("test@example.com"),
                      eq("Late Book Reminder"),
                      contains("5 days late returning"));
    }

    @Test
    void testSendLateReturnReminders_noEmailWhenNotLate() {

        BorrowRecord onTime = new BorrowRecord(
                "Algorithms",
                "999",
                "Book",
                LocalDate.now().minusDays(3),
                LocalDate.now().plusDays(2)   // deadline NOT passed
        );

        User u = new User("ahmad", "a@a.com", "123", "user");
        u.getBorrowedBooks().add(onTime);

        fileStorageMock.when(FileStorage::loadUsers)
                .thenReturn(List.of(u));

        reminderService.sendLateReturnReminders();

        // لا يجب إرسال أي إيميل
        verify(emailService, never()).send(any(), any(), any());
    }

    @Test
    void testSendLateReturnReminders_handlesMultipleUsers() {

        BorrowRecord r1 = new BorrowRecord(
                "DB Systems",
                "111",
                "Book",
                LocalDate.now().minusDays(8),
                LocalDate.now().minusDays(3)
        );

        BorrowRecord r2 = new BorrowRecord(
                "Networks CD",
                "222",
                "CD",
                LocalDate.now().minusDays(20),
                LocalDate.now().minusDays(10)
        );

        User u1 = new User("lina", "lina@example.com", "123", "user");
        u1.getBorrowedBooks().add(r1);

        User u2 = new User("sara", "sara@example.com", "123", "user");
        u2.getBorrowedBooks().add(r2);

        fileStorageMock.when(FileStorage::loadUsers)
                .thenReturn(List.of(u1, u2));

        reminderService.sendLateReturnReminders();

        verify(emailService, times(2)).send(any(), any(), any());
    }
}
