package me.moty.parkour.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import me.moty.parkour.game.ParkourGame;

public class ParkourStartEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private ParkourGame game;

	public ParkourStartEvent(ParkourGame game) {
		this.game = game;
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
