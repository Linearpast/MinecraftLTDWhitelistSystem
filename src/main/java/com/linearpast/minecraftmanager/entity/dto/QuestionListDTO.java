package com.linearpast.minecraftmanager.entity.dto;

import com.linearpast.minecraftmanager.entity.Questions;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class QuestionListDTO {
	private List<Questions> optionQuestions;
	private List<Questions> blankQuestions;
	private List<Questions> textQuestions;
}
