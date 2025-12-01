package com.library.service;

import com.library.domain.BorrowRecord;
import com.library.domain.User;
import com.library.persistence.FileStorage;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;

public class ReminderServiceTest {

    private ReminderService reminder;
    private IEmailService emailMock;

    @BeforeEach
    void setup() {
        emailMock = mock(IEmailService.class);
        reminder = new ReminderService(emailMock);
    }

    @Test
    void testSendReminderToLateUsers() {

        BorrowRecord lateRec = new BorrowRecord(
                "Java Book", "1111", "Book",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(3)
        );

        User lateUser = new User("fatima", "fatima@gmail.com", "1234", "user");
        lateUser.setBorrowedBooks(List.of(lateRec));


        User onTimeUser = new User("mona", "mona@gmail.com", "000", "user");
        onTimeUser.setBorrowedBooks(List.of(
                new BorrowRecord("Python", "2222", "Book",
                        LocalDate.now(),
                        LocalDate.now().plusDays(5))
        ));

        List<User> users = List.of(lateUser, onTimeUser);

        try (MockedStatic<FileStorage> mock = Mockito.mockStatic(FileStorage.class)) {
            mock.when(FileStorage::loadUsers).thenReturn(users);

            reminder.sendLateReturnReminders();

            verify(emailMock, times(1))
                    .send(eq("fatima@gmail.com"), eq("Late Book Reminder"), anyString());

            verify(emailMock, never())
                    .send(eq("mona@gmail.com"), anyString(), anyString());
        }
    }
}
