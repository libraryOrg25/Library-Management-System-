package com.library.persistence;

import com.library.domain.User;
import com.library.persistence.FileStorage;
import com.library.domain.Book;
import com.library.domain.BorrowRecord;
import org.junit.jupiter.api.*;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FileStorageTest {

    private Path tempUsers;
    private Path tempBooks;

    @BeforeEach
    void setup() throws Exception {
        tempUsers = Files.createTempFile("users_test", ".txt");
        tempBooks = Files.createTempFile("books_test", ".txt");

        FileStorage.setUserFile(tempUsers.toString());
        FileStorage.setBooksFile(tempBooks.toString());
    }

    @Test
    void testSaveLoadUsers() {
        User u = new User("f", "f@gmail.com", "123", "user");
        BorrowRecord r = new BorrowRecord(
                "Java","111","Book",
                LocalDate.of(2024,1,1),
                LocalDate.of(2024,2,1)
        );
        u.setBorrowedBooks(List.of(r));

        FileStorage.saveUsers(List.of(u));

        List<User> loaded = FileStorage.loadUsers();

        assertEquals(1, loaded.size());
        assertEquals("f", loaded.get(0).getUsername());
    }

    @Test
    void testBooksSaveLoad() {

        Book b = new Book("Java","Auth","111","Book",5);

        FileStorage.updateBooksFile(List.of(b));

        List<Book> loaded = FileStorage.loadBooks();

        assertEquals(1, loaded.size());
        assertEquals("Java", loaded.get(0).getTitle());
    }
}
