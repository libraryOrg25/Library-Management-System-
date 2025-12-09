package com.library.persistence;

import com.library.domain.Book;
import com.library.domain.BorrowRecord;
import com.library.domain.User;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileStorageTest {

    private static Path tempUsers;
    private static Path tempBooks;

    @BeforeAll
    static void setup() throws Exception {
        tempUsers = Files.createTempFile("users_test", ".txt");
        tempBooks = Files.createTempFile("books_test", ".txt");

        FileStorage.setUserFile(tempUsers.toString());
        FileStorage.setBooksFile(tempBooks.toString());
    }

    @BeforeEach
    void cleanFiles() throws Exception {
        Files.write(tempUsers, "".getBytes());
        Files.write(tempBooks, "".getBytes());
    }

    // ---------------------------------------------------------
    // 1) saveUsers + loadUsers
    // ---------------------------------------------------------
    @Test
    @Order(1)
    void testSaveAndLoadUsers() {
        User u = new User("Fatima", "fatima@mail.com", "123", "user");
        u.setFine(15);
        BorrowRecord r = new BorrowRecord("Book A", "001", "BOOK",
                LocalDate.now(), LocalDate.now().plusDays(10));
        u.getBorrowedBooks().add(r);

        FileStorage.saveUsers(List.of(u));

        List<User> loaded = FileStorage.loadUsers();
        assertEquals(1, loaded.size());
        assertEquals("Fatima", loaded.get(0).getUsername());
        assertEquals("BOOK", loaded.get(0).getBorrowedBooks().get(0).getType());
    }

    // ---------------------------------------------------------
    // 2) findUserByEmail
    // ---------------------------------------------------------
    @Test
    @Order(2)
    void testFindUserByEmail() {
        User u1 = new User("A", "a@mail.com", "1", "user");
        User u2 = new User("B", "b@mail.com", "2", "user");

        FileStorage.saveUsers(List.of(u1, u2));

        User found = FileStorage.findUserByEmail("b@mail.com");
        assertNotNull(found);
        assertEquals("B", found.getUsername());
    }

    // ---------------------------------------------------------
    // 3) updateUser
    // ---------------------------------------------------------
    @Test
    @Order(3)
    void testUpdateUser() {
        User u = new User("Ali", "ali@mail.com", "pass", "user");
        FileStorage.saveUsers(List.of(u));

        u.setFine(50);
        FileStorage.updateUser(u);

        User loaded = FileStorage.findUserByEmail("ali@mail.com");
        assertEquals(50, loaded.getFine());
    }

    // ---------------------------------------------------------
    // 4) loadBooks + updateBooksFile
    // ---------------------------------------------------------
    @Test
    @Order(4)
    void testSaveAndLoadBooks() {
        Book b = new Book("Math", "John", "111", "BOOK", 3);

        FileStorage.updateBooksFile(List.of(b));
        List<Book> loaded = FileStorage.loadBooks();

        assertEquals(1, loaded.size());
        assertEquals("Math", loaded.get(0).getTitle());
        assertEquals(3, loaded.get(0).getCopies());
    }

    // ---------------------------------------------------------
    // 5) performBorrow
    // ---------------------------------------------------------
    @Test
    @Order(5)
    void testPerformBorrow() {

        User u = new User("Sam", "sam@mail.com", "xx", "user");
        FileStorage.saveUsers(List.of(u));

        Book b = new Book("Physics", "Albert", "222", "BOOK", 5);
        FileStorage.updateBooksFile(List.of(b));

        FileStorage.performBorrow("sam@mail.com", b);

        User updated = FileStorage.findUserByEmail("sam@mail.com");
        assertEquals(1, updated.getBorrowedBooks().size());

        List<Book> books = FileStorage.loadBooks();
        assertEquals(4, books.get(0).getCopies()); // decreased by 1
    }

    // ---------------------------------------------------------
    // 6) performReturn
    // ---------------------------------------------------------
    @Test
    @Order(6)
    void testPerformReturn() {

        User u = new User("Dina", "dina@mail.com", "xxx", "user");

        BorrowRecord r = new BorrowRecord("AI Book", "333", "BOOK",
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(10));

        u.getBorrowedBooks().add(r);

        FileStorage.saveUsers(List.of(u));

        Book b = new Book("AI Book", "Someone", "333", "BOOK", 1);
        FileStorage.updateBooksFile(List.of(b));

        FileStorage.performReturn("dina@mail.com", r);

        User loaded = FileStorage.findUserByEmail("dina@mail.com");
        assertEquals(0, loaded.getBorrowedBooks().size());

        List<Book> books = FileStorage.loadBooks();
        assertEquals(2, books.get(0).getCopies()); // incremented by 1
    }
}
