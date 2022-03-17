package me.moty.parkour.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import me.moty.parkour.game.ParkourGame;

public class PlayerLeaveParkourEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private final Player player;
	private final ParkourGame game;

	public PlayerLeaveParkourEvent(Player player, ParkourGame game) {
		this.player = player;
		this.game = game;
	}

	public Player getPlayer() {
		return this.player;
	}

	public ParkourGame getGame() {
		return this.game;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return HANDLERS_LIST;
	}

}
