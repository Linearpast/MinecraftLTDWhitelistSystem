package com.linearpast.minecraftmanager.controller;

import com.linearpast.minecraftmanager.entity.Operators;
import com.linearpast.minecraftmanager.service.inter.OperatorsService;
import com.linearpast.minecraftmanager.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operators")
public class OperatorController {
	@Autowired
	private OperatorsService operatorsService;

	@PostMapping("/save")
	public Result<?> save(@RequestBody Operators operators) {
		if(!StringUtils.hasText(operators.getNickname()) ||
				!StringUtils.hasText(operators.getPassword()) ||
				!StringUtils.hasText(operators.getUsername()) ||
				!StringUtils.hasText(operators.getRoleName())) {
			return Result.error("错误");
		}
		if(operators.getUsername().length() <= 3) return Result.error("用户名应该大于3字符");
		if(operators.getPassword().length() < 8 || operators.getPassword().matches("([0-9]{8,})|([a-zA-Z]){8,}"))
			return Result.error("密码应同时包含数字和字母且至少8个字符");
		Operators save = operatorsService.save(operators);
		return save == null ? Result.error("服务器错误") : Result.success();
	}
}
