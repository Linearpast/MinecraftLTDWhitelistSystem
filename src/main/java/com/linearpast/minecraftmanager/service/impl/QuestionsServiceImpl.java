package com.linearpast.minecraftmanager.service.impl;

import com.linearpast.minecraftmanager.entity.Questions;
import com.linearpast.minecraftmanager.repository.QuestionsRepository;
import com.linearpast.minecraftmanager.service.inter.QuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionsServiceImpl implements QuestionsService {
	@Autowired
	private QuestionsRepository questionsRepository;

	@Override
	public Questions saveQuestions(Questions questions) {
		return questionsRepository.save(questions);
	}

	@Override
	public Page<Questions> getQuestions(Pageable pageable) {
		return questionsRepository.findAll(pageable);
	}

	@Override
	public void deleteQuestionsById(Integer id) {
		questionsRepository.deleteById(id);
	}

	@Override
	public void deleteQuestions(List<Integer> ids) {
		questionsRepository.deleteAllById(ids);
	}

	@Override
	public List<Questions> getAllQuestions() {
		return questionsRepository.findAll();
	}
}
