package com.linearpast.minecraftmanager.utils.rcon;

import com.linearpast.minecraftmanager.utils.config.SelfConfig;
import io.graversen.minecraft.rcon.IMinecraftClient;
import io.graversen.minecraft.rcon.MinecraftClient;
import io.graversen.minecraft.rcon.MinecraftRcon;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.*;

import io.graversen.minecraft.rcon.service.ConnectOptions;
import io.graversen.minecraft.rcon.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecraftRconUtils {
	private static final Logger log = LoggerFactory.getLogger(MinecraftRconUtils.class);
	private final ConnectTask task;
	private final RconDetails rconDetails;
	private final ScheduledExecutorService executorService;
	private volatile IMinecraftClient minecraftClient;
	private volatile MinecraftRcon minecraftRcon;
	private volatile boolean isConnected;
	private volatile CountDownLatch connectionLatch;

	public MinecraftRconUtils(RconDetails rconDetails, ConnectOptions connectOptions) {
		this.rconDetails = rconDetails;
		this.executorService = Executors.newScheduledThreadPool(2);
		this.task = new ConnectTask(connectOptions, rconDetails);
		startConnectionWatcher(connectOptions.getConnectionWatcherInterval().toSeconds());
	}

	public void disconnect(){
		isConnected = false;
		try {minecraftClient.close();
		}catch (Exception ignored){}
		setMinecraftClient(null);
	}

	public void connect(){
		boolean connected = isConnected(Duration.ofSeconds(1));
		if(connected) return;
		doConnect();
	}

	public boolean isConnected(Duration timeout) {
		if(!isConnected) return false;
		if(minecraftClient == null) return false;
		if(minecraftRcon == null) return false;
		try {
			return rconTest(minecraftClient, timeout);
		}catch(Exception e) {
			return false;
		}
	}

	private boolean rconTest(IMinecraftClient minecraftClient, Duration timeout){
		try {
			minecraftClient.sendRawSilently(SelfConfig.testCommand).get(timeout.toSeconds(), TimeUnit.SECONDS);
			return true;
		} catch (ExecutionException | TimeoutException | InterruptedException var3) {
			log.error("Lost connection to {}", this.rconDetails.getHostname());
			try {minecraftClient.close();
			} catch (IOException ignored) {}
			return false;
		}

	}

	public Optional<MinecraftRcon> minecraftRcon() {
		return Optional.ofNullable(this.minecraftRcon);
	}

	public void doConnect() {
		doConnect(5);
	}
	public void doConnect(int timeout) {
		try {
			if(this.connectionLatch != null){
				this.connectionLatch.await(timeout, TimeUnit.SECONDS);
				this.connectionLatch = new CountDownLatch(1);
			}
			Future<MinecraftClient> submit = this.executorService.submit(task);
			this.minecraftClient = submit.get(timeout, TimeUnit.SECONDS);
			this.minecraftRcon = new MinecraftRcon(this.minecraftClient);
			this.isConnected = true;
			if(this.connectionLatch != null){
				this.connectionLatch.countDown();
			}
		} catch (Exception var2) {
			Exception e = var2;
			disconnect();
			if (this.minecraftClient != null){
				this.connectionLatch.countDown();
			}
			log.error("Connection fail to {}", this.rconDetails.getHostname(), var2);
		}
	}

	private void startConnectionWatcher(long intervalSeconds) {
		this.executorService.scheduleWithFixedDelay(new TestConnect(this.rconDetails), 0, intervalSeconds, TimeUnit.SECONDS);
	}

	public void setMinecraftClient(MinecraftClient minecraftClient) {
		this.minecraftClient = minecraftClient;
		if(this.minecraftClient != null) this.minecraftRcon = new MinecraftRcon(this.minecraftClient);
		else this.minecraftRcon = null;
	}
	public void setConnected(boolean connected) {
		this.isConnected = connected;
	}

	private class TestConnect implements Runnable {
		private final RconDetails rconDetails;
		TestConnect(RconDetails rconDetails) {
			this.rconDetails = rconDetails;
		}

		@Override
		public void run() {
			try {
				if(isConnected(Duration.ofSeconds(1))) return;
				minecraftClient = MinecraftClient.connect(this.rconDetails.getHostname(), this.rconDetails.getPassword(), this.rconDetails.getPort());
				setMinecraftClient((MinecraftClient) minecraftClient);
				setConnected(true);
			}catch (Exception var2) {
				disconnect();
				log.error("Connection fail to {}", this.rconDetails.getHostname(), var2);
			}
		}
	}
}
