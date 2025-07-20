package com.linearpast.minecraftmanager.repository;

import com.linearpast.minecraftmanager.entity.Players;
import com.linearpast.minecraftmanager.entity.Region;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.StringUtils;

public interface RegionRepository extends JpaRepository<Region, Long>, JpaSpecificationExecutor<Region> {

	class RegionSpecifications {
		public static Specification<Region> withDynamicQuery(
				Long code, Long parentCode, Byte level) {

			return (Root<Region> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
				Predicate predicate = cb.conjunction();
				if(code != null) predicate = cb.and(predicate, cb.equal(root.get("code"), code));
				if(parentCode != null) predicate = cb.and(predicate, cb.equal(root.get("parentCode"), parentCode));
				if(level != null) predicate = cb.and(predicate, cb.equal(root.get("level"), level));
				return predicate;
			};
		}
	}
}
