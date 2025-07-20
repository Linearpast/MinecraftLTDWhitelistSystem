package com.linearpast.minecraftmanager.controller;

import com.linearpast.minecraftmanager.entity.ConfirmationEmail;
import com.linearpast.minecraftmanager.entity.PlayerAnswers;
import com.linearpast.minecraftmanager.entity.Players;
import com.linearpast.minecraftmanager.entity.dto.ManagerAnswerDTO;
import com.linearpast.minecraftmanager.entity.dto.MarkingScoreDTO;
import com.linearpast.minecraftmanager.entity.dto.SearchAnswerDTO;
import com.linearpast.minecraftmanager.service.PlayerAnswersService;
import com.linearpast.minecraftmanager.service.PlayersService;
import com.linearpast.minecraftmanager.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/playerAnswer")
public class PlayerAnswerController {
	@Autowired
	private PlayersService playersService;
	@Autowired
	private PlayerAnswersService playerAnswersService;

	@PostMapping("/markingAnswer")
	public Result<?> marking(@RequestBody MarkingScoreDTO markingScoreDTO) {
		Integer playerId = markingScoreDTO.getPlayerId();
		if(playerId == null) return Result.error("请重新获取数据");
		List<PlayerAnswers> playerAnswers = playerAnswersService.getPlayerAnswers(new Players() {{
			setId(playerId);
		}});
		Map<Integer, Integer> scoreMap = new HashMap<>();
		for (MarkingScoreDTO.AnswerScore answerScore : markingScoreDTO.getAnswerScore()) {
			scoreMap.put(answerScore.getQuestionId(), answerScore.getScore());
		}
		for (PlayerAnswers playerAnswer : playerAnswers) {
			Integer score = scoreMap.get(playerAnswer.getId().getQuestionId());
			if(score == null) {
				playerAnswer.setScore(null);
				continue;
			}
			playerAnswer.setScore(Math.min(playerAnswer.getQuestions().getScore(), score));
		}
		List<PlayerAnswers> answers = playerAnswersService.saveAllPlayerAnswers(playerAnswers);
		return answers == null ? Result.error("服务器错误") : Result.success();
	}

	@PostMapping("/getAll")
	public Result<?> getAll(@RequestBody SearchAnswerDTO searchAnswerDTO) {
		Page<Players> players = playersService.getPlayers(
				searchAnswerDTO.getPlayerName(),
				searchAnswerDTO.getQq(), null,
				searchAnswerDTO.getStatus(),
				searchAnswerDTO.getMinValue(),
				searchAnswerDTO.getMaxValue(),
				PageRequest.of(searchAnswerDTO.getPage() - 1, searchAnswerDTO.getSize())
		);
		List<ManagerAnswerDTO> result = new ArrayList<>();
		List<Integer> playerIds = players.getContent().stream().map(Players::getId).toList();
		Map<Integer, List<PlayerAnswers>> answersMap = playerAnswersService.getAllPlayerAnswers(playerIds)
				.stream().collect(Collectors.groupingBy(playerAnswers -> playerAnswers.getPlayers().getId()));

		for (Players player : players.getContent()) {
			List<PlayerAnswers> playerAnswers = answersMap.getOrDefault(player.getId(), List.of());
			ManagerAnswerDTO answerDTO = getManagerAnswerDTO(player, playerAnswers);
			result.add(answerDTO);
		}
		return Result.successPage(result, players.getTotalElements());
	}

	private ManagerAnswerDTO getManagerAnswerDTO(Players player, List<PlayerAnswers> playerAnswers) {
		boolean readStatus = true;
		Map<Integer, List<PlayerAnswers>> answersMap = new HashMap<>();
		int fullScore = 0;
		for (PlayerAnswers playerAnswer : playerAnswers) {
			int type = playerAnswer.getQuestions().getType();
			answersMap.computeIfAbsent(type, k -> new ArrayList<>()).add(playerAnswer);
			if(playerAnswer.getScore() == null && type == 3){
				readStatus = false;
			}
			fullScore += playerAnswer.getQuestions().getScore();
		}

		boolean flag = player.getConfirmationEmail() != null && player.getConfirmationEmail().getActive();
		return new ManagerAnswerDTO(
				player.getId(),
				player.getPlayerName(),
				player.getQq(),
				player.getStatus(),
				readStatus,
				player.getTotalScore(),
				fullScore,
				answersMap.getOrDefault(1, null),
				answersMap.getOrDefault(2, null),
				answersMap.getOrDefault(3, null),
				flag
		);
	}
}
