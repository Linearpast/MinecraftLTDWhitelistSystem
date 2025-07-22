package com.linearpast.minecraftmanager.service;

import com.linearpast.minecraftmanager.entity.Operators;
import com.linearpast.minecraftmanager.entity.Players;
import com.linearpast.minecraftmanager.entity.view.PlayerInfoView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PlayersService {
	Page<PlayerInfoView> getPlayers(
			String playerName,
			String qq,
			String uuid,
			Byte status,
			Integer minScore,
			Integer maxScore,
			Pageable pageable
	);

	boolean deletePlayer(Integer id);
	int updatePlayerStatus(Integer id, Byte status, Operators operators);
	Players getPlayer(String playerName);
	Players getPlayerById(Integer id);
	Players savePlayer(Players player);
	Players getPlayerIsApply(String playerName, String qq);
	int deletePlayers(List<Integer> ids) ;
	Optional<Players> getPlayerByToken(String token);
	int updatePlayersStatus(List<Integer> ids, Byte status, Operators operators);
	Integer getPlayersCountByStatus(Byte status);
}
