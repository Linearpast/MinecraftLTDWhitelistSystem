package com.linearpast.minecraftmanager.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerAnswersId implements Serializable {
	@Column(name = "player_id")
	private int playerId;

	@Column(name = "question_id")
	private int questionId;
}
