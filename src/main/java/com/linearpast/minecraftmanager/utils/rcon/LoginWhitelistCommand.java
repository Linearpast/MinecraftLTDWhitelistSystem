package com.linearpast.minecraftmanager.utils.rcon;

import com.linearpast.minecraftmanager.utils.WhitelistTarget;
import com.linearpast.minecraftmanager.utils.config.SelfConfig;
import io.graversen.minecraft.rcon.commands.base.ICommand;
import io.graversen.minecraft.rcon.util.WhiteListModes;
import lombok.Getter;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;
import java.util.Objects;

@Getter
public record LoginWhitelistCommand(WhitelistTarget whitelistTarget, WhiteListModes whiteListMode) implements ICommand {
	public LoginWhitelistCommand(WhitelistTarget whitelistTarget, WhiteListModes whiteListMode) {
		this.whitelistTarget = whitelistTarget;
		this.whiteListMode = Objects.requireNonNull(whiteListMode);
	}

	public String command() {
		return switch (this.whiteListMode()) {
			case ADD -> StringSubstitutor.replace(SelfConfig.addCommand + " ${name} ${uuid}", Map.of(
					"name", this.whitelistTarget().name(),
					"uuid", this.whitelistTarget().uuid())
			);
			case REMOVE, LIST, OFF, ON, RELOAD -> "";
		};
	}
}
