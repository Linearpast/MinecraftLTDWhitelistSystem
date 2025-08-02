package com.linearpast.minecraftmanager.service.impl;

import com.linearpast.minecraftmanager.entity.Operators;
import com.linearpast.minecraftmanager.entity.Players;
import com.linearpast.minecraftmanager.entity.view.PlayerInfoView;
import com.linearpast.minecraftmanager.repository.PlayersRepository;
import com.linearpast.minecraftmanager.repository.view.PlayerInfoViewRepository;
import com.linearpast.minecraftmanager.service.inter.PlayersService;
import com.linearpast.minecraftmanager.utils.WhitelistTarget;
import com.linearpast.minecraftmanager.utils.config.ConfigLoader;
import com.linearpast.minecraftmanager.utils.config.SelfConfig;
import com.linearpast.minecraftmanager.utils.rcon.LoginWhitelistCommand;
import com.linearpast.minecraftmanager.utils.rcon.MinecraftRconUtils;
import com.linearpast.minecraftmanager.utils.rcon.SelfWhiteListCommand;
import io.graversen.minecraft.rcon.MinecraftRcon;
import io.graversen.minecraft.rcon.RconResponse;
import io.graversen.minecraft.rcon.commands.WhiteListCommand;
import io.graversen.minecraft.rcon.service.ConnectOptions;
import io.graversen.minecraft.rcon.service.RconDetails;
import io.graversen.minecraft.rcon.util.Target;
import io.graversen.minecraft.rcon.util.WhiteListModes;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class PlayersServiceImpl implements PlayersService {
	@Autowired
	private PlayersRepository playersRepository;
	@Autowired
	private PlayerInfoViewRepository playerInfoViewRepository;
	@Autowired
	private MinecraftRconUtils rconService;
	@Autowired
	private JavaMailSenderImpl mailSender;
	@Autowired
	private SelfConfig selfConfig;

	@Override
	public Page<PlayerInfoView> getPlayers(
			String playerName,
			String qq,
			String uuid,
			Byte status,
			Integer minScore,
			Integer maxScore,
			Pageable pageable
	) {
		Specification<PlayerInfoView> spec = PlayerInfoViewRepository.PlayerInfoViewSpecifications
				.withDynamicQuery(playerName, qq, uuid, status, minScore, maxScore);
		return playerInfoViewRepository.findAll(spec, pageable);
	}

	@Override
	public boolean deletePlayer(Integer id) {
		Players byId = playersRepository.findById(id).orElse(null);
		if(byId == null) return false;
		rconService.connect();
		MinecraftRcon minecraftRcon = rconService.minecraftRcon().orElse(null);
		if(minecraftRcon == null) return false;
		RconResponse response = minecraftRcon.sendSync(new SelfWhiteListCommand(Target.player(byId.getPlayerName()), WhiteListModes.REMOVE));
		if(response.getResponseId() != 0) return false;
		this.asyncSendEmail(byId, (byte)0, true);
		playersRepository.deleteById(id);
		return true;
	}

	@Override
	@Transactional
	public int updatePlayerStatus(Integer id, Byte status, Operators operators) {
		Players byId = playersRepository.findById(id).orElse(null);
		if (byId == null) return 0;
		rconService.connect();
		RconResponse response;
		MinecraftRcon minecraftRcon = rconService.minecraftRcon().orElse(null);
		if(minecraftRcon == null) return 0;
		if (status == 1) {
			response = minecraftRcon.sendSync(new LoginWhitelistCommand(WhitelistTarget.player(byId), WhiteListModes.ADD));
		} else {
			response = minecraftRcon.sendSync(new SelfWhiteListCommand(Target.player(byId.getPlayerName()), WhiteListModes.REMOVE));
		}
		if (response.getResponseId() == 0) {
			asyncSendEmail(byId, status, false);
			playersRepository.updateOperatorsById(id, operators);
			return playersRepository.updateStatusById(id, status);
		}
		return 0;
	}

	@Override
	public Players getPlayer(String playerName) {
		return playersRepository.findByPlayerName(playerName);
	}

	@Override
	public Players getPlayerById(Integer id) {
		return playersRepository.findById(id).orElse(null);
	}

	@Override
	public Players savePlayer(Players player) {
		rconService.connect();
		MinecraftRcon minecraftRcon = rconService.minecraftRcon().orElse(null);
		if(player.getId() == null){
			if(player.getStatus() == 1) {
				if(minecraftRcon == null) return null;
				RconResponse response = minecraftRcon.sendSync(new LoginWhitelistCommand(WhitelistTarget.player(player), WhiteListModes.ADD));
				if(response.getResponseId() != 0) return null;
				asyncSendEmail(player, player.getStatus(), false);
			}
		}else {
			Players byId = playersRepository.findById(player.getId()).orElse(null);
			if(byId == null) return null;
			if(!byId.getStatus().equals(player.getStatus()) && (player.getStatus() == 1 || byId.getStatus() == 1)) {
				if(minecraftRcon == null) return null;
				RconResponse response;
				if(player.getStatus() == 1) {
					response = minecraftRcon.sendSync(new LoginWhitelistCommand(WhitelistTarget.player(player), WhiteListModes.ADD));
				}else {
					response = minecraftRcon.sendSync(new SelfWhiteListCommand(Target.player(player.getPlayerName()), WhiteListModes.REMOVE));
				}
				if(response.getResponseId() != 0) return null;
				asyncSendEmail(player, player.getStatus(), false);
			}
		}
		return playersRepository.save(player);
	}

	@Override
	public Players getPlayerIsApply(String playerName, String qq) {
		return playersRepository.findPlayersByPlayerNameAndQq(playerName, qq);
	}

	@Override
	public int deletePlayers(List<Integer> ids) {
		List<Players> allById = playersRepository.findAllById(ids);
		if(allById.isEmpty()) return 0;
		rconService.connect();
		MinecraftRcon minecraftRcon = rconService.minecraftRcon().orElse(null);
		if(minecraftRcon == null) return 0;
		List<Integer> successIds = new ArrayList<>();
		allById.forEach(players -> {
			RconResponse response = minecraftRcon.sendSync(new SelfWhiteListCommand(Target.player(players.getPlayerName()), WhiteListModes.REMOVE));
			if (response.getResponseId() == 0) {
				successIds.add(players.getId());
				asyncSendEmail(players, (byte) 0, true);
			}
		});
		playersRepository.deleteAllById(successIds);
		return successIds.size();
	}

	@Override
	public Optional<Players> getPlayerByToken(String token) {
		return playersRepository.findPlayersByConfirmationEmail_Token(token);
	}

	@Override
	public int updatePlayersStatus(List<Integer> ids, Byte status, Operators operators) {
		List<Players> allById = playersRepository.findAllById(ids);
		if(allById.isEmpty()) return 0;
		rconService.connect();
		MinecraftRcon minecraftRcon = rconService.minecraftRcon().orElse(null);
		if(minecraftRcon == null) return 0;
		List<Integer> successIds = new ArrayList<>();
		if (status == 1) {
			allById.forEach(players -> {
				RconResponse response = minecraftRcon.sendSync(new LoginWhitelistCommand(WhitelistTarget.player(players), WhiteListModes.ADD));
				if (response.getResponseId() == 0) {
					successIds.add(players.getId());
					asyncSendEmail(players, status, false);
				}
			});
		} else {
			allById.forEach(players -> {
				RconResponse response = minecraftRcon.sendSync(new SelfWhiteListCommand(Target.player(players.getPlayerName()), WhiteListModes.REMOVE));
				if (response.getResponseId() == 0) {
					successIds.add(players.getId());
					asyncSendEmail(players, status, false);
				}
			});
		}
		return playersRepository.bulkUpdateStatus(successIds, status, operators);
	}

	@Override
	public Integer getPlayersCountByStatus(Byte status) {
		return playersRepository.countByStatus(status);
	}

	private void asyncSendEmail(Players player, Byte status, boolean delete) {
		CompletableFuture.runAsync(() -> {
			try {this.sendDealEmail(player.getQq(), status, player.getPlayerName(), delete);
			} catch (Exception ignored) {}
		});
	}

	private void sendDealEmail(String qq, Byte status, String playerName, boolean delete) throws MessagingException {
		if(!selfConfig.emailEnable) return;
		String statusText = status == 1 ? "通过" : "拒绝";
		if(delete) statusText = "重置";
		String mainColor = status == 1 ? "#4CAF50" : "#F44336"; // 通过为绿色，拒绝为红色
		String icon = status == 1 ? "✓" : "✗";

		// 使用 StringBuilder 高效拼接 HTML
		String html = "<!DOCTYPE html>" +
				"<html lang='zh-CN'>" +
				"<head>" +
				"  <meta charset='UTF-8'>" +
				"  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
				"  <title>白名单通知</title>" +
				"  <style>" +
				"    body { font-family: 'Microsoft YaHei', sans-serif; background: #f5f5f5; padding: 20px; }" +
				"    .card { max-width: 600px; margin: 50px auto; background: white; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); overflow: hidden; }" +
				"    .header { background: " + mainColor + "; color: white; padding: 30px; text-align: center; }" +
				"    .icon { font-size: 4rem; margin-bottom: 15px; }" +
				"    .content { padding: 30px; line-height: 1.8; font-size: 16px; }" +
				"    .notice { background: #f9f9f9; border-left: 4px solid " + mainColor + "; padding: 15px; margin: 20px 0; }" +
				"    .player-name { font-weight: bold; color: " + mainColor + "; }" +
				"  </style>" +
				"</head>" +
				"<body>" +
				"  <div class='card'>" +
				"    <div class='header'>" +
				"      <div class='icon'>" + icon + "</div>" +
				"      <h1>您的申请已" + statusText + "</h1>" +
				"    </div>" +
				"    <div class='content'>" +
				"      <p>亲爱的 <span class='player-name'>" + playerName + "</span>：</p>" +
				(!delete
						? "      <p>您提交的服务器白名单申请已审核完毕。</p>"
						: "      <p>管理员已将您的申请记录移除。</p>") +
				"      <div class='notice'>" +
				"        <p>▶ 申请状态：<strong>" + statusText + "</strong></p>" +
				"        <p>▶ 处理时间：" + new java.util.Date() + "</p>" +
				"      </div>" +
				"      <p>" +
				(status == 1
						? "您现在可以进入服务器游玩，如有问题请联系管理员。"
						: "") +
				"</p>" +
				"      <hr>" +
				"      <p><small>本通知由系统自动发送，请勿回复</small></p>" +
				"    </div>" +
				"  </div>" +
				"</body>" +
				"</html>";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		// 设置邮件基本信息
		helper.setFrom(ConfigLoader.config.get("spring.mail.username"));  // 发件人（你的QQ邮箱）
		helper.setTo(qq + "@qq.com");  // 收件人
		helper.setSubject("白名单处理通知");  // 邮件主题

		// 构建HTML邮件内容（包含确认链接）
		helper.setText(html, true);  // true表示内容是HTML

		// 发送邮件
		mailSender.send(message);
	}
}
