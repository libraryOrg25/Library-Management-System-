package com.library.service;

public interface IEmailService {
	void send(String recipientEmail, String subject, String messageText);
}
