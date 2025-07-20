package com.linearpast.minecraftmanager.entity;

import com.linearpast.minecraftmanager.entity.embeddable.PlayerAnswersId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.patterns.TypePatternQuestions;

@Entity
@Table(name = "player_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerAnswers {
	@EmbeddedId
	private PlayerAnswersId id;

	@ManyToOne
	@MapsId("playerId")
	@JoinColumn(name = "player_id")
	private Players players;

	@ManyToOne
	@MapsId("questionId")
	@JoinColumn(name = "question_id")
	private Questions questions;

	@Lob
	private String answer;
	private Integer score;
}
