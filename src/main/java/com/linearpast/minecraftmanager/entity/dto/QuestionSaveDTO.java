package com.linearpast.minecraftmanager.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionSaveDTO {
	private Byte type;
	private Integer score;
	private String title;
	private String optionsData;
	private String blankContent;
	private Integer id;
}
