package com.linearpast.minecraftmanager.repository;

import com.linearpast.minecraftmanager.entity.Operators;
import com.linearpast.minecraftmanager.entity.Players;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

public interface PlayersRepository extends JpaRepository<Players, Integer>, JpaSpecificationExecutor<Players> {
	Players findByPlayerName(String playerName);

	@Transactional
	@Modifying
	@Query("UPDATE Players p SET p.status = :status WHERE p.id = :id")
	int updateScoreById(@Param("id") Integer id, @Param("status") Byte status);

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

	class PlayerSpecifications {
		public static Specification<Players> withDynamicQuery(
				String playerName, String qq, String uuid, Byte status,  Integer minValue, Integer maxValue) {

			return (Root<Players> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
				Predicate predicate = cb.conjunction();
				if (StringUtils.hasText(playerName)) predicate = cb.and(predicate, cb.like(root.get("playerName"), "%" + playerName + "%"));
				if (StringUtils.hasText(qq)) predicate = cb.and(predicate, cb.like(root.get("qq"), "%" + qq + "%"));
				if (status != null) predicate = cb.and(predicate, cb.equal(root.get("status"), status));
				if (StringUtils.hasText(uuid)) predicate = cb.and(predicate, cb.like(root.get("uuid"), "%" + uuid + "%"));
				if (minValue != null && maxValue != null) {
					predicate = cb.and(predicate, cb.between(root.get("totalScore"), minValue, maxValue));
				}else if(minValue != null){
					predicate = cb.and(predicate, cb.ge(root.get("totalScore"), minValue));
				}else if(maxValue != null){
					predicate = cb.and(predicate, cb.le(root.get("totalScore"), maxValue));
				}
				return predicate;
			};
		}
	}
}
