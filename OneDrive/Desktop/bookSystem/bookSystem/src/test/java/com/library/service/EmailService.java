package com.library.service;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService implements IEmailService {

    private final String senderEmail = "anoud.salah2003@gmail.com";  
    private final String senderPassword = "ooaz iyar hxlp yyyw"; // Gmail App Password

    @Override
    public void send(String recipientEmail, String subject, String messageText) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
            );
            message.setSubject(subject);
            message.setText(messageText);

            Transport.send(message);

            System.out.println("✅ Email sent to: " + recipientEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Cannot send email: " + e.getMessage());
        }
    }
}
