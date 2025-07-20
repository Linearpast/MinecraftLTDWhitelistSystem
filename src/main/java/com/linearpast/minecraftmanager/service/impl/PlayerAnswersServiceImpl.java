package com.linearpast.minecraftmanager.service.impl;

import com.linearpast.minecraftmanager.entity.PlayerAnswers;
import com.linearpast.minecraftmanager.entity.Players;
import com.linearpast.minecraftmanager.repository.PlayerAnswersRepository;
import com.linearpast.minecraftmanager.service.PlayerAnswersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerAnswersServiceImpl implements PlayerAnswersService {
	@Autowired
	private PlayerAnswersRepository playerAnswersRepository;

	@Override
	public List<PlayerAnswers> saveAllPlayerAnswers(List<PlayerAnswers> playerAnswers) {
		return playerAnswersRepository.saveAll(playerAnswers);
	}

	@Override
	public void deleteAllPlayerAnswers(Players players) {
		List<PlayerAnswers> allByPlayersId = playerAnswersRepository.findAllByPlayers_Id(players.getId());
		playerAnswersRepository.deleteAll(allByPlayersId);
	}

	@Override
	public List<PlayerAnswers> getPlayerAnswers(Players players) {
		return playerAnswersRepository.findAllByPlayers_Id(players.getId());
	}

	@Override
	public List<PlayerAnswers> getAllPlayerAnswers(List<Integer> ids) {
		return playerAnswersRepository.findByPlayerIds(ids);
	}
}
