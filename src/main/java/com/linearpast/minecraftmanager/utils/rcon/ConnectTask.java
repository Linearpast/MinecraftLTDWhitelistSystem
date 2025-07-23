package com.linearpast.minecraftmanager.utils.rcon;

import io.graversen.minecraft.rcon.MinecraftClient;
import io.graversen.minecraft.rcon.RconConnectException;
import io.graversen.minecraft.rcon.service.ConnectOptions;
import io.graversen.minecraft.rcon.service.RconDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class ConnectTask implements Callable<MinecraftClient> {
	private static final Logger log = LoggerFactory.getLogger(ConnectTask.class);
	private final ConnectOptions connectOptions;
	private final RconDetails rconDetails;

	ConnectTask(ConnectOptions connectOptions, RconDetails rconDetails) {
		this.connectOptions = connectOptions;
		this.rconDetails = rconDetails;
		log.debug("{}", connectOptions);
	}

	public MinecraftClient call() throws Exception {
		int currentAttempt = 0;

		while(true) {
			if (currentAttempt < this.connectOptions.getMaxRetries() && !Thread.currentThread().isInterrupted()) {
				++currentAttempt;
				log.debug("Connection attempt {}", currentAttempt);

				MinecraftClient var8;
				try {
					var8 = MinecraftClient.connect(this.rconDetails.getHostname(), this.rconDetails.getPassword(), this.rconDetails.getPort());
				} catch (Exception var6) {
					Exception e = var6;
					log.error("Connection attempt failed", e);
					continue;
				} finally {
					if (currentAttempt < this.connectOptions.getMaxRetries()) {
						this.sleep();
					} else {
						log.warn("Ran out of retries after {} total attempts", currentAttempt);
					}

				}

				return var8;
			}

			throw new RconConnectException("Unable to connect to Minecraft server after %d retries", new Object[]{currentAttempt - 1});
		}
	}

	private void sleep() {
		try {
			log.debug("Pausing for {} ms", this.connectOptions.getTimeBetweenRetries().toMillis());
			Thread.sleep(this.connectOptions.getTimeBetweenRetries().toMillis());
		} catch (InterruptedException var2) {
			InterruptedException e = var2;
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}

	}
}
