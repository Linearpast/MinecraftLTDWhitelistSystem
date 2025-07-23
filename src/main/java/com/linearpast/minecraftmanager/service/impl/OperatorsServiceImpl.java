package com.linearpast.minecraftmanager.service.impl;

import com.linearpast.minecraftmanager.entity.Operators;
import com.linearpast.minecraftmanager.repository.OperatorsRepository;
import com.linearpast.minecraftmanager.service.inter.OperatorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperatorsServiceImpl implements OperatorsService {

	@Autowired
	private OperatorsRepository operatorsRepository;

	@Override
	public Operators login(String username, String password) {
		return operatorsRepository.findByUsernameAndPassword(username, password);
	}

	@Override
	public Operators save(Operators operators) {
		return operatorsRepository.save(operators);
	}
}
