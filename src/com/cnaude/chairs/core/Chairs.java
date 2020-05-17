package com.cnaude.chairs.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.cnaude.chairs.commands.ChairsCommand;
import com.cnaude.chairs.listeners.NANLoginListener;
import com.cnaude.chairs.listeners.TrySitEventListener;
import com.cnaude.chairs.listeners.TryUnsitEventListener;
import com.cnaude.chairs.sitaddons.ChairEffects;
import com.cnaude.chairs.sitaddons.CommandRestrict;

public class Chairs extends JavaPlugin {
	private final static Chairs instance;

	public static Chairs getInstance() {
		return instance;
	}

	public Chairs() {
		instance = this;
	}

	public static Entity spawnChairsArrow(final Location location) {
		final Arrow arrow = location.getWorld().spawnArrow(location, new final Vector(), 0, 0);
		arrow.setGravity(false);
		arrow.setInvulnerable(true);
		arrow.setPickupStatus(PickupStatus.DISALLOWED);

		return arrow;
	}

	private final ChairsConfig config = new final ChairsConfig(this);
	public ChairsConfig getChairsConfig() {
		return config;
	}
	private final PlayerSitData psitdata = new final PlayerSitData(this);
	public PlayerSitData getPlayerSitData() {
		return psitdata;
	}


	public final ChairEffects chairEffects = new final ChairEffects(this);
	public final SitUtils utils = new final SitUtils(this);

	@Override
	public void onEnable() {
		try {
			Files.copy(Chairs.class.getClassLoader().getResourceAsStream("main/resources/config_help.txt"), new final File(getDataFolder(), "main/resources/config_help.txt").toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {}

		reloadConfig();
		getServer().getPluginManager().registerEvents(new final NANLoginListener(), this);
		getServer().getPluginManager().registerEvents(new final TrySitEventListener(this), this);
		getServer().getPluginManager().registerEvents(new final TryUnsitEventListener(this), this);
		getServer().getPluginManager().registerEvents(new final CommandRestrict(this), this);
		getCommand("chairs").setExecutor(new final ChairsCommand(this));
	}

	@Override
	public void onDisable() {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			if (psitdata.isSitting(player))
				psitdata.unsitPlayerForce(player, true);
		}
		chairEffects.cancelHealing();
		chairEffects.cancelPickup();
		saveSitDisabled();
	}

	@Override
	public void reloadConfig() {
		config.reloadConfig();

		if (config.effectsHealEnabled)
			chairEffects.restartHealing();
		else
			chairEffects.cancelHealing();

		if (config.effectsItemPickupEnabled)
			chairEffects.restartPickup();
		else
			chairEffects.cancelPickup();
	}

	protected void loadSitDisabled() {
		try {
			for (final String line: Files.readAllLines(new final File(getDataFolder(), "sit-disabled.txt").toPath())) {
				try {
					getPlayerSitData().disableSitting(UUID.fromString(line));
				} catch (IllegalArgumentException e) {}
			}
		} catch (IOException e) {}
	}

	protected void saveSitDisabled() {
		try {
			final File sitDisabledFile = new final File(getDataFolder(), "sit-disabled.txt");
			if (!sitDisabledFile.exists())
				sitDisabledFile.createNewFile();
			try (final PrintWriter writer = new final PrintWriter(sitDisabledFile, "UTF-8")) {
				writer.println("# The following players disabled Chairs for themselves");
				for (final UUID uuid : getPlayerSitData().sitDisabled) {
					writer.println(uuid.toString());
				}
			}
		} catch (IOException e) {}
	}
}
