package com.it3030.smartcampus.member4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsoleMailService implements MailService {

	private static final Logger log = LoggerFactory.getLogger(ConsoleMailService.class);

	@Override
	public void sendOtp(String email, String otp) {
		log.info("Sending OTP {} to {}", otp, email);
	}
}