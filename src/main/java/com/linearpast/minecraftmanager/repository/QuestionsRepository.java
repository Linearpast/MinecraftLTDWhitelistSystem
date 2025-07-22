package com.linearpast.minecraftmanager.repository;

import com.linearpast.minecraftmanager.entity.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionsRepository extends JpaRepository<Questions, Integer> {
}
