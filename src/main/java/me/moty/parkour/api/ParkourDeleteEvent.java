package me.moty.parkour.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import me.moty.parkour.game.ParkourArena;

public class ParkourDeleteEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private ParkourArena arena;

	public ParkourDeleteEvent(ParkourArena arena) {
		this.arena = arena;
	}

	public ParkourArena getArena() {
		return this.arena;
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
