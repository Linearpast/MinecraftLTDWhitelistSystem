package com.linearpast.minecraftmanager.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerSaveDTO {
	private Integer id;
	private String playerName;
	private String qq;
	private String description;
	private Byte status;
	private Integer totalScore;
	private String createTime;
	private Long code;
	private Integer pid;
}
