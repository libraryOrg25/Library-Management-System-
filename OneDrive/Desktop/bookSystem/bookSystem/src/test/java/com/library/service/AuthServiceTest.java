package com.library.service;

import com.library.persistence.FileStorage;
import com.library.domain.User;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private Path tempUsers;

    @BeforeEach
    void setup() throws Exception {
        tempUsers = Files.createTempFile("users_test", ".txt");
        FileStorage.setUserFile(tempUsers.toString());

        Files.write(tempUsers, Arrays.asList(
                "fatima,f@gmail.com,1234,user,0",
                "borrowed="
        ));
    }

    @Test
    void testRegisterEmailExists() {
        AuthService auth = new AuthService();
        boolean result = auth.register("new", "f@gmail.com", "9999", "user");
        assertFalse(result);
    }

    @Test
    void testRegisterSuccess() {
        AuthService auth = new AuthService();
        boolean result = auth.register("lana", "lana@mail.com", "1111", "user");
        assertTrue(result);

        User u = FileStorage.findUserByEmail("lana@mail.com");
        assertNotNull(u);
    }

    @Test
    void testLoginSuccess() {
        AuthService auth = new AuthService();
        User u = auth.login("f@gmail.com", "1234");

        assertNotNull(u);
        assertEquals("fatima", u.getUsername());
    }

    @Test
    void testLoginFail() {
        AuthService auth = new AuthService();
        User u = auth.login("x@mail.com", "wrong");
        assertNull(u);
    }
}
