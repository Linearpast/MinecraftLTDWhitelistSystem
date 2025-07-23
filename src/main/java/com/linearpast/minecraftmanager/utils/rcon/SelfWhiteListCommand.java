package com.linearpast.minecraftmanager.utils.rcon;

import com.linearpast.minecraftmanager.utils.config.SelfConfig;
import io.graversen.minecraft.rcon.commands.base.BaseTargetedCommand;
import io.graversen.minecraft.rcon.util.Target;
import io.graversen.minecraft.rcon.util.WhiteListModes;
import lombok.Getter;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Getter
public class SelfWhiteListCommand extends BaseTargetedCommand {
	private final WhiteListModes whiteListMode;

	public SelfWhiteListCommand(Target target, WhiteListModes whiteListMode) {
		super(target);
		this.whiteListMode = Objects.requireNonNull(whiteListMode);
	}

	public String command() {
		return switch (this.getWhiteListMode()) {
			case ADD, REMOVE ->
					StringSubstitutor.replace(SelfConfig.command + " ${mode} ${target}", Map.of("mode", this.getWhiteListMode().getModeName(), "target", this.getTarget()));
			case LIST, OFF, ON, RELOAD -> SelfConfig.command + " " + this.getWhiteListMode().getModeName();
		};
	}
}
