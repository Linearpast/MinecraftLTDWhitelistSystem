package com.linearpast.minecraftmanager.repository;

import com.linearpast.minecraftmanager.entity.Operators;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperatorsRepository extends JpaRepository<Operators, Integer> {
	Operators findByUsernameAndPassword(String username, String password);
}
