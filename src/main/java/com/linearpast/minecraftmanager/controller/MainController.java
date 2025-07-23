package com.linearpast.minecraftmanager.controller;

import com.linearpast.minecraftmanager.entity.Operators;
import com.linearpast.minecraftmanager.entity.Players;
import com.linearpast.minecraftmanager.service.inter.OperatorsService;
import com.linearpast.minecraftmanager.service.inter.PlayerAnswersService;
import com.linearpast.minecraftmanager.service.inter.PlayersService;
import com.linearpast.minecraftmanager.service.impl.EmailServiceImpl;
import com.linearpast.minecraftmanager.utils.Result;
import com.linearpast.minecraftmanager.utils.config.ConfigLoader;
import com.linearpast.minecraftmanager.utils.http.HttpApiUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
	@GetMapping("/")
	public String index() {
		return "redirect:/player/login";
	}

	@Controller
	@RequestMapping("/player")
	public static class PlayerAccountController{
		@Autowired
		private PlayersService playersService;
		@Autowired
		private EmailServiceImpl emailServiceImpl;
		@Autowired
		private PlayerAnswersService playerAnswersService;

		@PostMapping("/login")
		public String login(
				@RequestParam(name = "mcName") String mcName,
				@RequestParam(name = "qq") String qq,
				HttpSession session,
				RedirectAttributes redirectAttributes
		) {
			String uuid = HttpApiUtils.minecraftAccountQuery(mcName);
			if(uuid == null) {
				redirectAttributes.addAttribute("error", "不存在该玩家");
				return "redirect:/player/login";
			}
			Players player = playersService.getPlayer(mcName);
			Players playerIsApply = playersService.getPlayerIsApply(mcName, qq);
			if(player != null && !qq.equals(player.getQq())) {
				redirectAttributes.addAttribute("error", "玩家" + mcName + "已经被qq用户" + player.getQq() + "申请过白名单");
				return "redirect:/player/login";
			}
			try {
				if(!playerIsApply.getConfirmationEmail().getActive()){
					playerAnswersService.deleteAllPlayerAnswers(playerIsApply);
					playersService.deletePlayer(playerIsApply.getId());
					emailServiceImpl.deleteConfirmationEmail(playerIsApply.getConfirmationEmail().getId());
					redirectAttributes.addAttribute("error", "未验证邮件，已清空数据，请重新登陆");
					return "redirect:/player/login";
				}
			}catch (Exception ignored) {}
			session.setAttribute("apply", playerIsApply == null ? null : playerIsApply.getStatus());
			session.setAttribute("qq", qq);
			session.setAttribute("mcName", mcName);
			session.setAttribute("isLoggedIn", true);
			session.setAttribute("uuid", uuid);
			return "redirect:/player/home";
		}
		@GetMapping("/home")
		public String home(Model model, HttpSession session) {
			String qq = (String) session.getAttribute("qq");
			String base64 = HttpApiUtils.qqAvatarQuery(qq);
			if(base64 != null) model.addAttribute("avatar", base64);
			return "player/apply";
		}
		@GetMapping
		public String index(){
			return "redirect:/player/login";
		}
		@GetMapping("/login")
		public String adminLoginIndex(@RequestParam(name = "error", required = false) String error, Model model){
			model.addAttribute("error", error);
			return "player/login";
		}
		@GetMapping("/logout")
		public String logout(HttpSession session){
			session.invalidate();
			return "redirect:/player/login";
		}
		@GetMapping("/emailSuccess")
		public String emailSuccess(){
			return "player/email-success";
		}
	}

	@Controller
	@RequestMapping("/admin")
	public static class AdminAccountController{
		@Autowired
		private OperatorsService operatorsService;
		@Autowired
		private PlayersService playersService;
		@PostMapping("/login")
		public String adminLogin(
				@RequestParam(name = "username") String username,
				@RequestParam(name = "password") String password,
				HttpSession session,
				RedirectAttributes redirectAttributes
		){
			Operators login = operatorsService.login(username, password);
			if(login != null){
				session.setAttribute("adminAccount", login);
				session.setAttribute("isLoggedIn", true);
				return "redirect:/admin/home";
			}
			redirectAttributes.addAttribute("error", "账号密码错误");
			return "redirect:/admin/login";
		}

		@GetMapping("/welcome")
		public String welcome(Model model){
			int passCount = playersService.getPlayersCountByStatus((byte) 1);
			int unmarkCount = playersService.getPlayersCountByStatus((byte) 2);
			int denyCount = playersService.getPlayersCountByStatus((byte) 3);
			model.addAttribute("email", ConfigLoader.config.get("spring.mail.username"));
			model.addAttribute("passCount", passCount);
			model.addAttribute("unmarkCount", unmarkCount);
			model.addAttribute("denyCount", denyCount);
			return "admin/welcome-page";
		}

		@GetMapping("/home")
		public String home(){
			return "admin/index";
		}

		@GetMapping("/add")
		public String add(){
			return "admin/add-operator";
		}

		@GetMapping
		public String index(){
			return "redirect:/admin/login";
		}

		@GetMapping("/login")
		public String adminLoginIndex(@RequestParam(name = "error", required = false) String error, Model model){
			model.addAttribute("error", error);
			return "admin/login";
		}
		@ResponseBody
		@GetMapping("/logout")
		public Result<?> logout(HttpSession session){
			session.invalidate();
			return Result.success();
		}

		@GetMapping("/application")
		public String application(){
			return "admin/apply-manager";
		}

		@GetMapping("/questions")
		public String question(){
			return "admin/question-manager";
		}

		@GetMapping("/answers")
		public String answers(){
			return "admin/answer-manager";
		}
	}

}
