package com.linearpast.minecraftmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "region")
@AllArgsConstructor
@NoArgsConstructor
public class Region {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long code;

	private String name;
	private String fullName;

	@Column(name = "parent_code")
	private Long parentCode;

	private Byte level;
}
