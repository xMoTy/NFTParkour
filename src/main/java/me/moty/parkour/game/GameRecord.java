package me.moty.parkour.game;

import java.util.UUID;

public class GameRecord {
	private final UUID uuid;
	private final long timeStamp;
	private final int placement;
	private final long timeSpent;
	private final String arenaName;

	public GameRecord(UUID uuid, long timeStamp, int placement, long timeSpent, String arenaName) {
		this.uuid = uuid;
		this.timeStamp = timeStamp;
		this.timeSpent = timeSpent;
		this.placement = placement;
		this.arenaName = arenaName;
	}

	public UUID getUUID() {
		return uuid;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public int getPlacement() {
		return placement;
	}

	public long getTimeSpent() {
		return timeSpent;
	}

	public String getArenaName() {
		return arenaName;
	}
}
