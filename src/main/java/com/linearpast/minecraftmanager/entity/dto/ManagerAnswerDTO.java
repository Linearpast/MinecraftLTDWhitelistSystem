package com.linearpast.minecraftmanager.entity.dto;

import com.linearpast.minecraftmanager.entity.PlayerAnswers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerAnswerDTO {
	private Integer id;
	private String playerName;
	private String qq;
	private Byte status;
	private Boolean readStatus;
	private Integer score;
	private Integer fullScore;
	private List<PlayerAnswers> optionAnswers;
	private List<PlayerAnswers> blankAnswers;
	private List<PlayerAnswers> textAnswers;
	private Boolean active;
}
