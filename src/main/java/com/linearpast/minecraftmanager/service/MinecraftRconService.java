package com.linearpast.minecraftmanager.service;

import com.linearpast.minecraftmanager.utils.config.SelfConfig;
import com.linearpast.minecraftmanager.utils.rcon.MinecraftRconUtils;
import io.graversen.minecraft.rcon.service.ConnectOptions;
import io.graversen.minecraft.rcon.service.RconDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class MinecraftRconService {
	@Bean
	public MinecraftRconUtils rconService(SelfConfig selfConfig) {
		RconDetails details = new RconDetails(selfConfig.host, selfConfig.port, selfConfig.password);
		ConnectOptions connectOptions = new ConnectOptions(3, Duration.ofSeconds(3L), Duration.ofSeconds(selfConfig.heartTime));
		return new MinecraftRconUtils(details, connectOptions);
	}
}
