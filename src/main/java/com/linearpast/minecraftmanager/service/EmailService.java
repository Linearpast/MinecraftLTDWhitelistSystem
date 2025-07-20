package com.linearpast.minecraftmanager.service;

import com.linearpast.minecraftmanager.entity.ConfirmationEmail;

import java.util.Optional;

public interface EmailService {
	ConfirmationEmail saveConfirmationEmail(ConfirmationEmail confirmationEmail);
	Optional<ConfirmationEmail> findConfirmationEmailByToken(String token);
	void deleteConfirmationEmail(Integer id);
}
