package com.linearpast.minecraftmanager.repository.view;

import com.linearpast.minecraftmanager.entity.view.PlayerInfoView;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public interface PlayerInfoViewRepository extends JpaRepository<PlayerInfoView, Integer> , JpaSpecificationExecutor<PlayerInfoView> {

	class PlayerInfoViewSpecifications {
		public static Specification<PlayerInfoView> withDynamicQuery(
				String playerName, String qq, String uuid, Byte status,  Integer minValue, Integer maxValue) {

			return (Root<PlayerInfoView> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
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
