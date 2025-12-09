package com.library.service;

import com.library.domain.User;
import com.library.persistence.FileStorage;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;
    private MockedStatic<FileStorage> mockedStatic;

    @BeforeEach
    void setUp() {
        authService = new AuthService();
        mockedStatic = Mockito.mockStatic(FileStorage.class);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void testRegisterSuccess() {
        List<User> emptyList = new ArrayList<>();

        mockedStatic.when(FileStorage::loadUsers).thenReturn(emptyList);

        boolean result = authService.register("Fatima", "fatima@test.com", "12345", "user");

        assertTrue(result);

        mockedStatic.verify(() -> FileStorage.saveUsers(anyList()));
    }

    @Test
    void testRegisterFailsWhenEmailExists() {
        List<User> users = new ArrayList<>();
        users.add(new User("Ali", "ali@test.com", "abc", "user"));

        mockedStatic.when(FileStorage::loadUsers).thenReturn(users);

        boolean result = authService.register("NewUser", "ali@test.com", "pass", "user");

        assertFalse(result);

        mockedStatic.verify(() -> FileStorage.saveUsers(anyList()), never());
    }

    @Test
    void testLoginSuccess() {
        User u = new User("Layan", "layan@test.com", "pass123", "admin");

        mockedStatic.when(FileStorage::loadUsers).thenReturn(List.of(u));

        User logged = authService.login("layan@test.com", "pass123");

        assertNotNull(logged);
        assertEquals("Layan", logged.getUsername());
    }

    @Test
    void testLoginFailsWrongPassword() {
        User u = new User("Sara", "sara@test.com", "mypassword", "user");

        mockedStatic.when(FileStorage::loadUsers).thenReturn(List.of(u));

        User result = authService.login("sara@test.com", "wrong");

        assertNull(result);
    }

    @Test
    void testLoginFailsEmailNotFound() {
        mockedStatic.when(FileStorage::loadUsers).thenReturn(Collections.emptyList());

        User result = authService.login("unknown@test.com", "123");

        assertNull(result);
    }
}
