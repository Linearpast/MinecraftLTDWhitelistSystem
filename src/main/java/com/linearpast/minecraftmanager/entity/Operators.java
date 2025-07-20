package com.linearpast.minecraftmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "operators")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Operators {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Byte permissionLevel;
	private String roleName;
	private String username;
	private String password;
	private String nickname;
}
