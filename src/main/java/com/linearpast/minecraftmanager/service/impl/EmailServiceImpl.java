package com.linearpast.minecraftmanager.service.impl;

import com.linearpast.minecraftmanager.entity.ConfirmationEmail;
import com.linearpast.minecraftmanager.entity.Players;
import com.linearpast.minecraftmanager.repository.ConfirmationEmailRepository;
import com.linearpast.minecraftmanager.service.inter.EmailService;
import com.linearpast.minecraftmanager.utils.config.ConfigLoader;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService {
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private ConfirmationEmailRepository confirmationEmailRepository;
	@Autowired
	private PlayerAnswersServiceImpl playerAnswersServiceImpl;
	@Autowired
	private PlayersServiceImpl playersServiceImpl;

	public void sendConfirmationEmail(String mcName, String toEmail, String token, String host) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		// 设置邮件基本信息
		helper.setFrom(ConfigLoader.config.get("spring.mail.username"));  // 发件人（你的QQ邮箱）
		helper.setTo(toEmail);  // 收件人
		helper.setSubject("邮箱验证");  // 邮件主题

		// 构建HTML邮件内容（包含确认链接）
		String htmlContent = buildHtmlContent(mcName, token, host);
		helper.setText(htmlContent, true);  // true表示内容是HTML

		// 发送邮件
		mailSender.send(message);
	}

	private String buildHtmlContent(String mcName, String token, String host) {
		String baseUrl = host + "/api/confirm";
		String confirmUrl = baseUrl + "?token=" + token;  // 带token的确认链接

		// 自定义HTML模板（可根据操作类型调整内容）
		return "<html>"
				+ "<head><style>.button{margin-top: 20px;margin-bottom: 20px;padding:10px 20px;background-color:#007bff;color:white;text-decoration:none;border-radius:5px;}</style></head>"
				+ "<body>"
				+ "<p>您正在进行玩家" + mcName + "的白名单申请操作，请点击按钮确定</p><br><br>"
				+ "<a href=\"" + confirmUrl + "\" class=\"button\">点击验证</a>"
				+ "<br><br><p>若您未执行此操作，请忽略此邮件。</p>"
				+ "<p>链接有效期为30分钟，过期后需重新操作。</p>"
				+ "</body>"
				+ "</html>";
	}

	public String generateToken(Players players) {
		// 生成唯一token（使用UUID）
		String token = UUID.randomUUID().toString();

		ConfirmationEmail confirmationToken = players.getConfirmationEmail();
		if(confirmationToken == null) confirmationToken = new ConfirmationEmail();
		confirmationToken.setToken(token);
		confirmationToken.setExpiredTime(LocalDateTime.now().plusMinutes(30));
		confirmationToken.setUsed(false);
		confirmationToken.setActive(false);
		ConfirmationEmail confirmationEmail = this.saveConfirmationEmail(confirmationToken);
		players.setConfirmationEmail(confirmationEmail);
		playersServiceImpl.savePlayer(players);

		return confirmationEmail.getToken();
	}

	public boolean validateToken(String token) {
		Optional<Players> optionalToken = playersServiceImpl.getPlayerByToken(token);
		if (optionalToken.isEmpty()) {
			return false;  // token不存在
		}

		Players players = optionalToken.get();
		ConfirmationEmail confirmationToken = players.getConfirmationEmail();
		if (confirmationToken.getUsed()) {
			return confirmationToken.getActive();  // token已使用
		}
		if (confirmationToken.getExpiredTime().isBefore(LocalDateTime.now())) {
			playerAnswersServiceImpl.deleteAllPlayerAnswers(players);
			playersServiceImpl.deletePlayer(players.getId());
			this.deleteConfirmationEmail(confirmationToken.getId());
			return false;  // token已过期
		}

		return true;  // token有效
	}

	public void markTokenAsUsed(String token) {
		Optional<ConfirmationEmail> optionalToken = this.findConfirmationEmailByToken(token);
		optionalToken.ifPresent(tokenEntity -> {
			if(tokenEntity.getUsed() && tokenEntity.getActive()) return;
			tokenEntity.setUsed(true);
			tokenEntity.setActive(true);
			this.saveConfirmationEmail(tokenEntity);
		});
	}

	@Override
	public ConfirmationEmail saveConfirmationEmail(ConfirmationEmail confirmationEmail) {
		return confirmationEmailRepository.save(confirmationEmail);
	}

	@Override
	public Optional<ConfirmationEmail> findConfirmationEmailByToken(String token) {
		return confirmationEmailRepository.findByToken(token);
	}

	@Override
	public void deleteConfirmationEmail(Integer id) {
		confirmationEmailRepository.deleteById(id);
	}
}
