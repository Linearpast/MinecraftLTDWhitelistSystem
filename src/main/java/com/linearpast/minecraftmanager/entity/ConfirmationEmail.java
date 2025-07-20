package com.linearpast.minecraftmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "confirmation_email")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationEmail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String token;
	private Boolean used;
	@Column(name = "expired_time")
	private LocalDateTime expiredTime;
	private Boolean active;
}
