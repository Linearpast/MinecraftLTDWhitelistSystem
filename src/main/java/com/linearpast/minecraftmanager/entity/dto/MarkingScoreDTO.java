package com.linearpast.minecraftmanager.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MarkingScoreDTO {
	private Integer playerId;
	private List<AnswerScore> answerScore;

	@Data
	@NoArgsConstructor
	public static class AnswerScore{
		private Integer questionId;
		private Integer score;
	}
}