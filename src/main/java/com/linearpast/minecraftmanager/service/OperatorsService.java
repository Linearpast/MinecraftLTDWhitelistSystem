package com.linearpast.minecraftmanager.service;

import com.linearpast.minecraftmanager.entity.Operators;

public interface OperatorsService {

	Operators login(String username, String password);
	Operators save(Operators operators);
}
