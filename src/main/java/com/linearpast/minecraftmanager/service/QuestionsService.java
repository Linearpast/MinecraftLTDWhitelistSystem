package com.linearpast.minecraftmanager.service;

import com.linearpast.minecraftmanager.entity.Questions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionsService {
	Questions saveQuestions(Questions questions);
	Page<Questions> getQuestions(Pageable pageable);
	void deleteQuestionsById(Integer id);
	void deleteQuestions(List<Integer> ids);
	List<Questions> getAllQuestions();
}
