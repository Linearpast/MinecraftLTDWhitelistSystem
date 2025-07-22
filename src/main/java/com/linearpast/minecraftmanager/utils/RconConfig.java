package com.linearpast.minecraftmanager.utils;

import io.graversen.minecraft.rcon.service.ConnectOptions;
import io.graversen.minecraft.rcon.service.MinecraftRconService;
import io.graversen.minecraft.rcon.service.RconDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RconConfig {
	@Value("${minecraft.rcon.host}") String host;
	@Value("${minecraft.rcon.port}") int port;
	@Value("${minecraft.rcon.password}") String password;

	@Bean
	public MinecraftRconService rconService() {
		RconDetails details = new RconDetails(host, port, password);
		return new MinecraftRconService(details, ConnectOptions.defaults());
	}
}
