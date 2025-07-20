package com.linearpast.minecraftmanager.repository;

import com.linearpast.minecraftmanager.entity.PlayerAnswers;
import com.linearpast.minecraftmanager.entity.embeddable.PlayerAnswersId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerAnswersRepository extends JpaRepository<PlayerAnswers, PlayerAnswersId> {
	List<PlayerAnswers> findAllByPlayers_Id(Integer playerId);

	@Query("SELECT pa FROM PlayerAnswers pa WHERE pa.players.id IN :playerIds")
	List<PlayerAnswers> findByPlayerIds(@Param("playerIds") List<Integer> ids);
}
