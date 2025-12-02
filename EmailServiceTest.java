package com.library.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceTest {

    @Test
    void testSendRealEmail() {
        EmailService emailService = new EmailService();

        String recipient = "anoud.salah2003@gmail.com"; // sending to yourself
        String subject = "Library System Test Email";
        String message = "This is a real test email sent from JUnit in your library project.";

        try {
            emailService.sendEmail(recipient, subject, message);
            assertTrue(true);  // If no exception, test passes
        } catch (Exception e) {
            fail("Email sending FAILED: " + e.getMessage());
        }
    }
}
