package com.linearpast.minecraftmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "players")
public class Players {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "operator_id")
	private Operators operators;
	private String playerName;
	private String uuid;
	private Byte status;
	private String qq;

	@Column(name = "create_time")
	private LocalDateTime createTime;
	private String description;
	@OneToOne
	@JoinColumn(name = "email")
	private ConfirmationEmail confirmationEmail;

	@ManyToOne
	@JoinColumn(name = "region_code")
	private Region region;
}
