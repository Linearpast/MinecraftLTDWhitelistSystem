package com.linearpast.minecraftmanager.utils.config;

import io.graversen.minecraft.rcon.service.ConnectOptions;
import io.graversen.minecraft.rcon.service.MinecraftRconService;
import io.graversen.minecraft.rcon.service.RconDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class SelfConfig {
	//rcon
	@Value("${minecraft.rcon.host}")
	String host;

	@Value("${minecraft.rcon.port}")
	int port;

	@Value("${minecraft.rcon.password}")
	String password;

	@Value("${minecraft.rcon.wlcmd}")
	public static String command;

	@Value("${email.enable}")
	public static boolean emailEnable;

	@Bean
	public MinecraftRconService rconService() {
		RconDetails details = new RconDetails(host, port, password);
		return new MinecraftRconService(details, new ConnectOptions(3, Duration.ofSeconds(3L), Duration.ofMinutes(30)));
	}
}
