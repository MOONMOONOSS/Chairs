package com.cnaude.chairs.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerChairSitEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;

	private Location sitLocation;

	public PlayerChairSitEvent(final Player who, final Location sitLocation) {
		super(who);

		this.sitLocation = sitLocation;
	}

	public Location getSitLocation() {
		return sitLocation.clone();
	}

	public void setSitLocation(final Location location) {
		sitLocation = location.clone();
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(final boolean cancelled) {
		this.cancelled = cancelled;
	}
}
