package com.linearpast.minecraftmanager.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchAnswerDTO {
	private String playerName;
	private String qq;
	private Byte status;
	private Integer minValue;
	private Integer maxValue;
	private Integer page = 1;
	private Integer size = 10;
}
