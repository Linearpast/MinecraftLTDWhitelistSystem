package com.linearpast.minecraftmanager.controller;

import com.linearpast.minecraftmanager.service.impl.EmailServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class EmailController {
	@Autowired
	private EmailServiceImpl emailServiceImpl;

	@GetMapping("/api/confirm")
	public void confirm(@RequestParam String token, HttpServletResponse response) throws IOException {
		if(!emailServiceImpl.validateToken(token)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		emailServiceImpl.markTokenAsUsed(token);
		response.sendRedirect("/player/emailSuccess");
	}
}
