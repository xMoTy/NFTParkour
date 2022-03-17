package me.moty.parkour.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import me.moty.parkour.game.GameRecord;

public class PlayerFinishParkourEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private final Player player;
	private final int placement;
	private final GameRecord record;

	public PlayerFinishParkourEvent(Player player, int placement, GameRecord record) {
		this.player = player;
		this.placement = placement;
		this.record = record;
	}

	public Player getPlayer() {
		return this.player;
	}

	public int getPlacement() {
		return placement;
	}

	public GameRecord getGameRecord() {
		return record;
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
