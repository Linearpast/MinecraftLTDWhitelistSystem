package com.linearpast.minecraftmanager.repository;

import com.linearpast.minecraftmanager.entity.Operators;
import com.linearpast.minecraftmanager.entity.Players;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayersRepository extends JpaRepository<Players, Integer>, JpaSpecificationExecutor<Players> {
	Players findByPlayerName(String playerName);

	@Transactional
	@Modifying
	@Query("UPDATE Players p SET p.status = :status WHERE p.id = :id")
	int updateStatusById(@Param("id") Integer id, @Param("status") Byte status);

	@Transactional
	@Modifying
	@Query("UPDATE Players p SET p.operators = :operators WHERE p.id = :id")
	void updateOperatorsById(@Param("id") Integer id, @Param("operators") Operators operators);

	@Modifying
	@Transactional
	@Query("UPDATE Players p SET p.status = :status, " +
			"p.operators = :operators " +
			"WHERE p.id IN :ids")
	int bulkUpdateStatus(
			@Param("ids") List<Integer> ids,
			@Param("status") Byte status,
			@Param("operators") Operators operators
	);

	Players findPlayersByPlayerNameAndQq(String playerName, String qq);
	Optional<Players> findPlayersByConfirmationEmail_Token(String token);
	int countByStatus(Byte status);
}
