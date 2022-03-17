package me.moty.parkour.api;

import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerPreJoinParkourEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private Player player;
	private Consumer<Player> process;

	public PlayerPreJoinParkourEvent(Player player, Consumer<Player> process) {
		this.player = player;
		this.process = process;
	}

	public Player getPlayer() {
		return this.player;
	}

	public void pass() {
		process.accept(player);
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
