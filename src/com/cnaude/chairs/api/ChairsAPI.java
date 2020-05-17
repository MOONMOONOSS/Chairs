package com.cnaude.chairs.api;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.cnaude.chairs.core.Chairs;
import com.cnaude.chairs.core.PlayerSitData;

public class ChairsAPI {

	private static PlayerSitData getPlayerSitData() {
		return Chairs.getInstance().getPlayerSitData();
	}

	public static boolean isSitting(final Player player) {
		return getPlayerSitData().isSitting(player);
	}

	public static boolean isBlockOccupied(final Block block) {
		return getPlayerSitData().isBlockOccupied(block);
	}

	public static Player getBlockOccupiedBy(final Block block) {
		return getPlayerSitData().getPlayerOnChair(block);
	}

	public static boolean sit(final Player player, final Block blocktouccupy, final Location sitlocation) {
		return getPlayerSitData().sitPlayer(player, blocktouccupy, sitlocation);
	}

	public static void unsit(final Player player) {
		getPlayerSitData().unsitPlayerForce(player, true);
	}

}
