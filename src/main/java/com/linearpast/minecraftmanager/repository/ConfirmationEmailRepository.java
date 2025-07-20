package com.linearpast.minecraftmanager.repository;

import com.linearpast.minecraftmanager.entity.ConfirmationEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationEmailRepository extends JpaRepository<ConfirmationEmail, Integer> {
	Optional<ConfirmationEmail> findByToken(String token);
}
