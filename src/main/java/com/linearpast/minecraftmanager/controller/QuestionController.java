package com.linearpast.minecraftmanager.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linearpast.minecraftmanager.entity.Questions;
import com.linearpast.minecraftmanager.entity.dto.QuestionSaveDTO;
import com.linearpast.minecraftmanager.service.QuestionsService;
import com.linearpast.minecraftmanager.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
	@Autowired
	private QuestionsService questionsService;

	@PostMapping("/save")
	public Result<?> saveQuestion(@RequestBody QuestionSaveDTO questionSaveDTO) {
		if (questionSaveDTO != null) {
			Questions questions = new Questions();
			if(questionSaveDTO.getType() == (byte) 1){
				JsonArray inputArray = new JsonArray();
				JsonArray correct = new JsonArray();
				JsonArray asJsonArray = JsonParser.parseString(questionSaveDTO.getOptionsData()).getAsJsonArray();
				if(asJsonArray.size() < 2) return Result.error("至少应有两个选项");
				boolean hasCorrect = false;
				for (JsonElement jsonElement : asJsonArray) {
					JsonObject asJsonObject = jsonElement.getAsJsonObject();
					boolean check = asJsonObject.get("check").getAsBoolean();
					hasCorrect = check || hasCorrect;
					correct.add(check);
					inputArray.add(asJsonObject.get("input").getAsString());
				}
				if(!hasCorrect) return Result.error("应至少有一个答案");
				JsonArray resultArray = new JsonArray();
				resultArray.add(inputArray);
				resultArray.add(correct);
				questions.setOptions(resultArray.toString());
			} else if (questionSaveDTO.getType() == (byte) 2) {
				questions.setOptions(questionSaveDTO.getBlankContent());
			}
			if(questionSaveDTO.getId() != null) questions.setId(questionSaveDTO.getId());
			questions.setTitle(questionSaveDTO.getTitle());
			questions.setScore(questionSaveDTO.getScore());
			questions.setType(questionSaveDTO.getType());
			Questions result = questionsService.saveQuestions(questions);
			return result == null ? Result.error("服务器错误") : Result.success();
		}
		return Result.error("错误请求");
	}

	@PostMapping("/getAll")
	public Result<?> getAllQuestions(
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer size
	) {
		Page<Questions> questions = questionsService.getQuestions(PageRequest.of(page - 1, size));
		return Result.successPage(questions.getContent(), questions.getTotalElements());
	}

	@DeleteMapping("/delete/{id}")
	public Result<?> delete(@PathVariable Integer id){
		questionsService.deleteQuestionsById(id);
		return Result.success();
	}

	@DeleteMapping("/batchDelete")
	public Result<?> batchDelete(@RequestBody List<Integer> ids){
		questionsService.deleteQuestions(ids);
		return Result.success();
	}
}
