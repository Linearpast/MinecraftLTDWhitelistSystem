package com.linearpast.minecraftmanager.utils.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SelfConfig {

	public static String command;
	public static String testCommand;
	public static String addCommand;

	//rcon
	@Value("${minecraft.rcon.host}")
	public String host;

	@Value("${minecraft.rcon.port}")
	public int port;

	@Value("${minecraft.rcon.password}")
	public String password;

	@Value("${minecraft.rcon.heart-time}")
	public long heartTime;

	@Value("${email.enable}")
	public boolean emailEnable;

	@Value("${minecraft.rcon.wlcmd}")
	public void whiteListCommand(String command) {
		SelfConfig.command = command;
	};

	@Value("${minecraft.rcon.test-cmd}")
	public void testCommand(String command) {
		SelfConfig.testCommand = command;
	}

	@Value("${minecraft.rcon.add-cmd}")
	public void addCommand(String command) {
		SelfConfig.addCommand = command;
	}
}
