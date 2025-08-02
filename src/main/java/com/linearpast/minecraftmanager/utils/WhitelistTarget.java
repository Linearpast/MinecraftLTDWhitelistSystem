package com.linearpast.minecraftmanager.utils;

import com.linearpast.minecraftmanager.entity.Players;

public record WhitelistTarget(String name, String uuid) {
	public static WhitelistTarget player(Players players) {
		return new WhitelistTarget(players.getPlayerName(), players.getUuid());
	}
}
