package com.linearpast.minecraftmanager.repository;

import com.linearpast.minecraftmanager.entity.ConfirmationEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationEmailRepository extends JpaRepository<ConfirmationEmail, Integer> {
	Optional<ConfirmationEmail> findByToken(String token);
}
