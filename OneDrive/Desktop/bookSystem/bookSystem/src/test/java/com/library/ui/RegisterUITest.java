package com.library.ui;

import com.library.service.AuthService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterUITest {

    private RegisterUI ui;
    private AuthService mockAuth;

    @BeforeEach
    void setup() {

        // Mock AuthService
        mockAuth = Mockito.mock(AuthService.class);

        // إنشاء UI لكن بدون إظهارها
        ui = new RegisterUI() {
            @Override
            protected AuthService createAuthService() {
                return mockAuth;
            }
        };
    }

    @Test
    void testEmptyFieldsShowError() {

        ui.usernameField.setText("");
        ui.emailField.setText("");
        ui.passwordField.setText("");

        // نستدعي نفس الكود اللي بيصير عند الضغط على REGISTER
        ui.registerBtn.doClick();

        // Mockito لا يهمه JOptionPane، لكنه يهمه أنه لم يتم استدعاء register
        verify(mockAuth, never()).register(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testRegisterSuccess() {

        when(mockAuth.register("fatima", "fatima@gmail.com", "123", "user"))
                .thenReturn(true);

        ui.usernameField.setText("fatima");
        ui.emailField.setText("fatima@gmail.com");
        ui.passwordField.setText("123");
        ui.roleBox.setSelectedItem("user");

        ui.registerBtn.doClick();

        verify(mockAuth, times(1))
                .register("fatima", "fatima@gmail.com", "123", "user");
    }

    @Test
    void testRegisterEmailExists() {

        when(mockAuth.register("fatima", "fatima@gmail.com", "123", "user"))
                .thenReturn(false);

        ui.usernameField.setText("fatima");
        ui.emailField.setText("fatima@gmail.com");
        ui.passwordField.setText("123");
        ui.roleBox.setSelectedItem("user");

        ui.registerBtn.doClick();

        verify(mockAuth, times(1))
                .register("fatima", "fatima@gmail.com", "123", "user");
    }
}
