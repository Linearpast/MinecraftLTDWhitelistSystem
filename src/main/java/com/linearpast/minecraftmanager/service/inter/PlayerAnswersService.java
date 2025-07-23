package com.linearpast.minecraftmanager.service.inter;

import com.linearpast.minecraftmanager.entity.PlayerAnswers;
import com.linearpast.minecraftmanager.entity.Players;

import java.util.List;

public interface PlayerAnswersService {
	List<PlayerAnswers> saveAllPlayerAnswers(List<PlayerAnswers> playerAnswers);
	void deleteAllPlayerAnswers(Players players);
	List<PlayerAnswers> getPlayerAnswers(Players players);
	List<PlayerAnswers> getAllPlayerAnswers(List<Integer> ids);
}
