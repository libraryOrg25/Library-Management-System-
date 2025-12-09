package com.library.service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    void testSendEmailSuccess() throws Exception {

        EmailService service = new EmailService("pass123", "sender@test.com");

        // Mock Transport.send()
        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {

            transportMock.when(() -> Transport.send(any(Message.class)))
                         .thenAnswer(inv -> null);

            // Fake Session
            Session session = Session.getInstance(new Properties());
            MimeMessage msg = spy(new MimeMessage(session));

            // Mock Session.getInstance(...)
            try (MockedStatic<Session> sessionMock = mockStatic(Session.class)) {
                sessionMock.when(() -> Session.getInstance(any(Properties.class), any()))
                           .thenReturn(session);

                // === FIX: حل مشكلة ambiguity ===
                doNothing().when(msg).setFrom(any(InternetAddress.class));

                doNothing().when(msg)
                           .setRecipients(
                                   any(Message.RecipientType.class),
                                   any(InternetAddress[].class)
                           );

                doNothing().when(msg).setSubject(anyString());
                doNothing().when(msg).setText(anyString());

                // Execute
                service.send("receiver@test.com", "Hello", "Test message");

                transportMock.verify(() -> Transport.send(any(Message.class)), times(1));
            }
        }
    }

    @Test
    void testSendEmailFailureThrows() throws Exception {

        EmailService service = new EmailService("pass123", "sender@test.com");

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {

            // Force error
            transportMock.when(() -> Transport.send(any(Message.class)))
                         .thenThrow(new MessagingException("SMTP ERROR"));

            try {
                service.send("receiver@test.com", "ErrorMail", "Test");
            } catch (RuntimeException ex) {
                assert ex.getMessage().contains("Failed to send email");
            }

            transportMock.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }
}
