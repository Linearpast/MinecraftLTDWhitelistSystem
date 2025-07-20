package com.linearpast.minecraftmanager.service.impl;

import com.linearpast.minecraftmanager.entity.Operators;
import com.linearpast.minecraftmanager.entity.Players;
import com.linearpast.minecraftmanager.repository.PlayersRepository;
import com.linearpast.minecraftmanager.service.PlayersService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayersServiceImpl implements PlayersService {
	@Autowired
	private PlayersRepository playersRepository;

	@Override
	public Page<Players> getPlayers(
			String playerName,
			String qq,
			String uuid,
			Byte status,
			Integer minScore,
			Integer maxScore,
			Pageable pageable
	) {
		Specification<Players> spec = PlayersRepository.PlayerSpecifications
				.withDynamicQuery(playerName, qq, uuid, status, minScore, maxScore);
		return playersRepository.findAll(spec, pageable);
	}

	@Override
	public void deletePlayer(Integer id) {
		playersRepository.deleteById(id);
	}

	@Override
	@Transactional
	public int updatePlayerStatus(Integer id, Byte status, Operators operators) {
		playersRepository.updateOperatorsById(id, operators);
		return playersRepository.updateScoreById(id, status);
	}

	@Override
	public Players getPlayer(String playerName) {
		return playersRepository.findByPlayerName(playerName);
	}

	@Override
	public Players getPlayerById(Integer id) {
		return playersRepository.findById(id).orElse(null);
	}

	@Override
	public Players savePlayer(Players player) {
		return playersRepository.save(player);
	}

	@Override
	public Players getPlayerIsApply(String playerName, String qq) {
		return playersRepository.findPlayersByPlayerNameAndQq(playerName, qq);
	}

	@Override
	public void deletePlayers(List<Integer> ids) {
		playersRepository.deleteAllById(ids);
	}

	@Override
	public Optional<Players> getPlayerByToken(String token) {
		return playersRepository.findPlayersByConfirmationEmail_Token(token);
	}

	@Override
	public int updatePlayersStatus(List<Integer> ids, Byte status, Operators operators) {
		return playersRepository.bulkUpdateStatus(ids, status, operators);
	}

	@Override
	public Integer getPlayersCountByStatus(Byte status) {
		return playersRepository.countByStatus(status);
	}
}
