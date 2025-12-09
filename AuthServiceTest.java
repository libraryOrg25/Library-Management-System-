package com.library.service;

import com.library.domain.User;
import com.library.persistence.FileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private AuthService service;

    @BeforeEach
    void setup() {
        // Reset users list before each test
        List<User> empty = new ArrayList<>();
        FileStorage.saveUsers(empty);

        // Re-load AuthService with empty users
        service = new AuthService();
    }

    @Test
    void testRegisterSuccess() {
        boolean result = service.register("fatima", "fatima@mail.com", "1234", "user");
        assertTrue(result);

        User u = service.login("fatima@mail.com", "1234");
        assertNotNull(u);
        assertEquals("fatima", u.getUsername());
    }

    @Test
    void testRegisterEmailExists() {
        service.register("fatima", "fatima@mail.com", "1234", "user");
        boolean result = service.register("other", "fatima@mail.com", "pass", "user");

        assertFalse(result); // duplicate email
    }

    @Test
    void testLoginSuccess() {
        service.register("fatima", "fatima@mail.com", "1234", "user");

        User u = service.login("fatima@mail.com", "1234");

        assertNotNull(u);
        assertEquals("fatima", u.getUsername());
    }

    @Test
    void testLoginFail() {
        User u = service.login("wrong@mail.com", "nope");
        assertNull(u);
    }
}
