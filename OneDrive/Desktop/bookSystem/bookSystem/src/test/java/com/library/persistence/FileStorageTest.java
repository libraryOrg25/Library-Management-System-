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

    // 🆕 new — branch coverage
    @Test
    @Order(3)
    void testFindUserByEmail_notFound() {
        FileStorage.saveUsers(List.of());
        User found = FileStorage.findUserByEmail("notfound@mail.com");
        assertNull(found);
    }

    // ---------------------------------------------------------
    // 3) updateUser
    // ---------------------------------------------------------
    @Test
    @Order(4)
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
    @Order(5)
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
    @Order(6)
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

    // 🆕 branch: CD borrow creates 7 days deadline
    @Test
    @Order(7)
    void testPerformBorrow_CD() {
        User u = new User("Sara", "sara@mail.com", "p", "user");
        FileStorage.saveUsers(List.of(u));

        Book cd = new Book("Music CD", "Artist", "555", "CD", 2);
        FileStorage.updateBooksFile(List.of(cd));

        FileStorage.performBorrow("sara@mail.com", cd);

        User updated = FileStorage.findUserByEmail("sara@mail.com");
        assertEquals(1, updated.getBorrowedBooks().size());

        BorrowRecord r = updated.getBorrowedBooks().get(0);
        assertEquals("CD", r.getType());
    }

    // ---------------------------------------------------------
    // 6) performReturn
    // ---------------------------------------------------------
    @Test
    @Order(8)
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

    // 🆕 branch: return record not found
    @Test
    @Order(9)
    void testPerformReturn_recordNotFound() {

        User u = new User("Mira", "mira@mail.com", "y", "user");
        FileStorage.saveUsers(List.of(u));

        // No records in user → branch
        BorrowRecord fake = new BorrowRecord("Unknown", "999", "BOOK",
                LocalDate.now(), LocalDate.now());

        FileStorage.performReturn("mira@mail.com", fake);

        User loaded = FileStorage.findUserByEmail("mira@mail.com");
        assertEquals(0, loaded.getBorrowedBooks().size()); // unchanged
    }

    // ---------------------------------------------------------
    // 🆕 loadUsers() branches
    // ---------------------------------------------------------
    @Test
    @Order(10)
    void testLoadUsers_emptyFile() {
        List<User> users = FileStorage.loadUsers();
        assertEquals(0, users.size());
    }

    @Test
    @Order(11)
    void testLoadUsers_emptyBorrowedLine() throws Exception {
        String content = "A,a@mail.com,1,user,0\nborrowed=\n";
        Files.write(tempUsers, content.getBytes());

        List<User> users = FileStorage.loadUsers();
        assertEquals(1, users.size());
        assertEquals(0, users.get(0).getBorrowedBooks().size());
    }

    @Test
    @Order(12)
    void testLoadUsers_invalidBorrowRecord() throws Exception {
        String content = "A,a@mail.com,1,user,0\nborrowed=wrong|data|missing\n";
        Files.write(tempUsers, content.getBytes());

        List<User> users = FileStorage.loadUsers();
        assertEquals(1, users.size());
        assertEquals(0, users.get(0).getBorrowedBooks().size()); // ignored invalid
    }
}
