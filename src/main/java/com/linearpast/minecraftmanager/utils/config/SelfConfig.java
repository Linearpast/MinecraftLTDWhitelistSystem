package com.linearpast.minecraftmanager.utils.config;

import com.linearpast.minecraftmanager.utils.rcon.MinecraftRconService;
import io.graversen.minecraft.rcon.service.ConnectOptions;
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

	@Value("${minecraft.rcon.heart-time}")
	long heartTime;

	@Value("${email.enable}")
	public static boolean emailEnable;

	@Bean
	public MinecraftRconService rconService() {
		RconDetails details = new RconDetails(host, port, password);
		ConnectOptions connectOptions = new ConnectOptions(3, Duration.ofSeconds(3L), Duration.ofSeconds(heartTime));
		return new MinecraftRconService(details, connectOptions);
	}
}
