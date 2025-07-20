package com.linearpast.minecraftmanager.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.linearpast.minecraftmanager.entity.*;
import com.linearpast.minecraftmanager.entity.dto.*;
import com.linearpast.minecraftmanager.entity.embeddable.PlayerAnswersId;
import com.linearpast.minecraftmanager.service.PlayerAnswersService;
import com.linearpast.minecraftmanager.service.PlayersService;
import com.linearpast.minecraftmanager.service.QuestionsService;
import com.linearpast.minecraftmanager.service.RegionService;
import com.linearpast.minecraftmanager.service.impl.EmailServiceImpl;
import com.linearpast.minecraftmanager.utils.HttpApiUtils;
import com.linearpast.minecraftmanager.utils.Result;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

	@Autowired
	private PlayersService playersService;
	@Autowired
	private RegionService regionService;
	@Autowired
	private EmailServiceImpl emailServiceImpl;


	@PostMapping("/getApply")
	public Result<?> getApplyPage(
			@RequestParam(required = false) String playerName,
			@RequestParam(required = false) String qq,
			@RequestParam(required = false) String uuid,
			@RequestParam(required = false) Byte status,
			@RequestParam(required = false) Integer minValue,
			@RequestParam(required = false) Integer maxValue,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer size
	){
		Page<Players> playerScores = playersService.getPlayers(
				playerName, qq, uuid, status, minValue, maxValue, PageRequest.of(page - 1, size)
		);
		return Result.successPage(playerScores.getContent(), playerScores.getTotalElements());
	}

	@DeleteMapping("/delete/{id}")
	public Result<?> delete(@PathVariable Integer id){
		playersService.deletePlayer(id);
		return Result.success();
	}

	@PostMapping("/changeStatus")
	public Result<?> updateStatus(@RequestBody PlayerStatusDTO playerStatusDTO, HttpSession session){
		Operators operators = (Operators) session.getAttribute("adminAccount");
		int code = playersService.updatePlayerStatus(playerStatusDTO.getId(), playerStatusDTO.getStatus(), operators);
		return code < 1 ? Result.error("操作失败") : Result.success();
	}

	@DeleteMapping("/batchDelete")
	public Result<?> batchDelete(@RequestBody List<Integer> ids){
		playersService.deletePlayers(ids);
		return Result.success();
	}

	@PostMapping("/batchChangeStatus")
	public Result<?> batchPass(@RequestBody BatchStatusUpdateDTO dto, HttpSession session){
		Operators operators = (Operators) session.getAttribute("adminAccount");
		int code = playersService.updatePlayersStatus(dto.getIds(), dto.getStatus(), operators);
		return code < 1 ? Result.error("操作失败") : Result.success();
	}

	@PostMapping("/save")
	public Result<?> save(@RequestBody PlayerSaveDTO players, HttpSession session){
		try {
			Operators account = (Operators) session.getAttribute("adminAccount");
			if(account == null) return Result.error("无权限");
			players.setPid(account.getId());
			if(players.getId() == null){
				Players player = playersService.getPlayer(players.getPlayerName());
				if(player != null) return Result.error("玩家"+ player.getPlayerName() +"已存在");
				String uuid = HttpApiUtils.minecraftAccountQuery(players.getPlayerName());
				if(uuid == null){
					return Result.error("非正版账号");
				}
				Region region = regionService.findRegion(players.getCode(), null, null).get(0);
				if(players.getCode() != null){
					if(region.getCode() == null) return Result.error("邮政编码错误，不存在该地址");
				}
				ConfirmationEmail email = new ConfirmationEmail();
				email.setActive(true);
				ConfirmationEmail confirmationEmail = emailServiceImpl.saveConfirmationEmail(email);
				Players newPlayer = new Players();
				newPlayer.setPlayerName(players.getPlayerName());
				newPlayer.setQq(players.getQq());
				newPlayer.setUuid(uuid);
				newPlayer.setStatus(players.getStatus());
				newPlayer.setDescription(players.getDescription());
				newPlayer.setCreateTime(LocalDateTime.parse(players.getCreateTime(),
						DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
				);
				newPlayer.setTotalScore(players.getTotalScore());
				newPlayer.setRegion(region);
				newPlayer.setOperators(account);
				newPlayer.setConfirmationEmail(confirmationEmail);
				Players result = playersService.savePlayer(newPlayer);
				if(result == null) return Result.error("服务器错误");
			}else {
				Players player = playersService.getPlayerById(players.getId());
				if(player == null) return Result.error("玩家不存在");
				Players playerByName = playersService.getPlayer(players.getPlayerName());
				if(playerByName != null && !Objects.equals(playerByName.getId(), player.getId()))
					return Result.error("玩家" + playerByName.getPlayerName() + "已存在");
				if(!Objects.equals(player.getRegion().getCode(), players.getCode())){
					Region newRegion = regionService.findRegion(players.getCode(), null, null).get(0);
					if(players.getCode() != null){
						if(newRegion.getCode() == null) return Result.error("邮政编码错误，不存在该地址");
					}
					player.setRegion(newRegion);
				}
				String uuid = HttpApiUtils.minecraftAccountQuery(players.getPlayerName());
				if(uuid == null){
					return Result.error("非正版账号");
				}
				player.setPlayerName(players.getPlayerName());
				player.setQq(players.getQq());
				player.setUuid(uuid);
				player.setStatus(players.getStatus());
				player.setCreateTime(LocalDateTime.parse(players.getCreateTime(),
						DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
				);
				player.setDescription(players.getDescription());
				player.setTotalScore(players.getTotalScore());
				player.setOperators(account);
				Players result = playersService.savePlayer(player);
				if(result == null) return Result.error("服务器错误");
			}
			return Result.success();
		}catch (Exception ignored){}
		return Result.error("操作失败");
	}

	@RestController
	@RequestMapping("/api/answer")
	public static class PlayerAnswerController{
		@Autowired
		private QuestionsService questionsService;
		@Autowired
		private PlayersService playersService;
		@Autowired
		private PlayerAnswersService playerAnswersService;
		@Autowired
		private EmailServiceImpl emailServiceImpl;

		@PostMapping("/getQuestion")
		public Result<?> getQuestion(@RequestBody SimpleQuestionDTO questionDTO, HttpSession session){
			if(questionDTO.getCode() == null) return Result.error("请选择地址");
			if(!StringUtils.hasText(questionDTO.getDescription())) return Result.error("请输入你的来由");

			//更新session
			session.setAttribute("code", questionDTO.getCode());
			session.setAttribute("description", questionDTO.getDescription());

			//判断白名单是否已存在该玩家
			String playerName = (String)session.getAttribute("mcName");
			Players existPlayer = playersService.getPlayer(playerName);
			if(existPlayer != null) return Result.error("玩家" + playerName + "已使用qq" + existPlayer.getQq() + "申请过白名单");

			//获取所有题目，并将数据进行转变以方便前端渲染
			List<Questions> allQuestions = questionsService.getAllQuestions();
			Map<Integer, List<Questions>> typeGroups = new HashMap<>();
			if(allQuestions != null){
				//一次遍历将所有题目按类型分到Map
				for (Questions question : allQuestions) {
					int type = question.getType();
					if(type == 1){
						String options = question.getOptions();
						JsonArray asJsonArray = JsonParser.parseString(options).getAsJsonArray();
						JsonArray result = asJsonArray.get(0).getAsJsonArray();
						question.setOptions(result.toString());
					} else if(type == 2){
						String options = question.getOptions();
						String processed = options.replaceAll("\\$\\{.*?}", "\\${}");
						String[] parts = processed.split("(?<=\\$\\{})|(?=\\$\\{})");
						JsonArray result = new Gson().toJsonTree(Arrays.asList(parts)).getAsJsonArray();
						question.setOptions(result.toString());
					}
					typeGroups.computeIfAbsent(type, k -> new ArrayList<>()).add(question);
				}
			}

			//数据存储发送到前端
			QuestionListDTO questionListDTO = new QuestionListDTO();
			questionListDTO.setOptionQuestions(typeGroups.getOrDefault(1, null));
			questionListDTO.setBlankQuestions(typeGroups.getOrDefault(2, null));
			questionListDTO.setTextQuestions(typeGroups.getOrDefault(3, null));
			return Result.success(questionListDTO);
		}

		@PostMapping("/putQuestion")
		public Result<?> putQuestion(@RequestBody List<PlayerAnswerDTO> answerDTOS, HttpSession session, HttpServletRequest request){
			if(answerDTOS != null){
				//获取session来添加一个players到数据库
				String playerName = (String) session.getAttribute("mcName");
				String qq = (String) session.getAttribute("qq");
				Long code = (Long) session.getAttribute("code");
				String uuid = (String) session.getAttribute("uuid");
				String description = (String) session.getAttribute("description");
				if(code == null) return Result.error("请重新登陆");
				Players player = new Players();
				player.setPlayerName(playerName);
				player.setQq(qq);
				player.setUuid(uuid);
				player.setDescription(description);
				player.setRegion(new Region(){{setCode(code);}});
				player.setCreateTime(LocalDateTime.now());
				player.setStatus((byte)2);
				Players playerIsApply = playersService.getPlayerIsApply(playerName, qq);
				Players players;
				if(playerIsApply == null) players = playersService.savePlayer(player);
				else players = null;
				if(players == null) return Result.error("你已经申请过白名单");


				try {
					//获取所有问题，比对答案，计算分数，保存答案
					List<Questions> allQuestions = questionsService.getAllQuestions();
					List<PlayerAnswers> answers = new ArrayList<>();
					for (PlayerAnswerDTO answerDTO : answerDTOS) {
						Questions questions = allQuestions.stream().filter(q -> q.getId() == answerDTO.getId()).findFirst().orElse(null);
						if(questions != null){
							PlayerAnswers playerAnswers = new PlayerAnswers();
							playerAnswers.setPlayers(players);
							playerAnswers.setQuestions(questions);
							playerAnswers.setAnswer(answerDTO.getAnswer());
							PlayerAnswersId playerAnswersId = new PlayerAnswersId();
							playerAnswersId.setPlayerId(players.getId());
							playerAnswersId.setQuestionId(questions.getId());
							playerAnswers.setId(playerAnswersId);

							String answer = answerDTO.getAnswer();
							if(answerDTO.getType() == 1){
								JsonArray playerAnswer = JsonParser.parseString(answer).getAsJsonArray();
								JsonArray options = JsonParser.parseString(questions.getOptions()).getAsJsonArray();
								JsonArray correctAnswer = options.get(1).getAsJsonArray();
								if(correctAnswer.size() != playerAnswer.size()){
									playerAnswers.setScore(0);
								}else {
									playerAnswers.setScore(questions.getScore());
									for (int i = 0; i < correctAnswer.size(); i++) {
										if(correctAnswer.get(i).getAsBoolean() != playerAnswer.get(i).getAsBoolean()){
											playerAnswers.setScore(0);
											break;
										}
									}
								}
							}else if (answerDTO.getType() == 2){
								List<String> resultList = new ArrayList<>();
								JsonArray asJsonArray = JsonParser.parseString(answer).getAsJsonArray();
								for (JsonElement element : asJsonArray) {
									resultList.add(element.getAsString());
								}
								String regex = "\\$\\{(.+?)}";
								Pattern pattern = Pattern.compile(regex);
								Matcher matcher = pattern.matcher(questions.getOptions());
								List<String> result = new ArrayList<>();
								while (matcher.find()) {
									result.add(matcher.group(1));
								}
								if(resultList.equals(result)){
									playerAnswers.setScore(questions.getScore());
								}else {
									playerAnswers.setScore(0);
								}
							}
							answers.add(playerAnswers);
						}
					}
					List<PlayerAnswers> result = playerAnswersService.saveAllPlayerAnswers(answers);

					String token = emailServiceImpl.generateToken(players);
					String serverName = request.getServerName();
					if(serverName.equals("localhost")) serverName = "127.0.0.1";
					String host = String.format("%s://%s:%d", request.getScheme(), serverName, request.getServerPort());
					String email = players.getQq() + "@qq.com";
					String mcName = players.getPlayerName();
					//邮件发送
					CompletableFuture.runAsync(() -> {
						try {
							emailServiceImpl.sendConfirmationEmail(mcName, email, token, host);
						} catch (Exception e) {
							playerAnswersService.deleteAllPlayerAnswers(players);
							playersService.deletePlayer(players.getId());
						}
					});

					//更新session
					session.setAttribute("apply", players.getStatus());
					if(result != null) return Result.success().msg("请在30分钟内检查邮箱并确认\n未确认将清除申请\n验证前请勿重登本网站\n若未发送邮件请联系管理");
				}catch (Exception ignored){
					playerAnswersService.deleteAllPlayerAnswers(players);
					playersService.deletePlayer(players.getId());
				}
			}
			return Result.error("数据错误");
		}
	}
}
