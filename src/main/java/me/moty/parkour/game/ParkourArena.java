package me.moty.parkour.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

@SerializableAs("ParkourArena")
public class ParkourArena implements ConfigurationSerializable {
	private final String name;
	private final Cuboid arena;
	private final int gameTime;
	private final int maxPlayer, minPlayer;
	private final List<Location> checkpoints;
	private final Location start, waiting;

	public ParkourArena(String name, Cuboid arena, Location start, Location waiting, int gameTime, int maxPlayer,
			int minPlayer, List<Location> checkpoints) {
		this.name = name;
		this.arena = arena;
		this.start = start;
		this.waiting = waiting;
		this.gameTime = gameTime;
		this.minPlayer = minPlayer;
		this.maxPlayer = maxPlayer;
		this.checkpoints = checkpoints;
	}

	@SuppressWarnings("unchecked")
	public ParkourArena(Map<String, Object> paramMap) {
		this.name = (String) paramMap.get("name");
		this.arena = (Cuboid) paramMap.get("arena");
		this.start = (Location) paramMap.get("start");
		this.waiting = (Location) paramMap.get("waiting");
		this.gameTime = (int) paramMap.get("game-time");
		this.maxPlayer = (int) paramMap.get("max-player");
		this.minPlayer = (int) paramMap.get("min-player");
		this.checkpoints = (List<Location>) paramMap.get("checkpoints");
	}

	public String getName() {
		return name;
	}

	public Cuboid getArenaCuboid() {
		return arena;
	}

	public World getWorld() {
		return arena.getPoint2().getWorld();
	}

	public int getGameTime() {
		return gameTime;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}

	public int getMinPlayer() {
		return minPlayer;
	}

	public List<Location> getCheckpoints() {
		return checkpoints;
	}

	public Location getStartLocation() {
		return start;
	}

	public Location getWaitingLocation() {
		return waiting;
	}

	public static class Builder {
		private Cuboid arena;
		private String name;
		private int gameTime;
		private int maxPlayer, minPlayer;
		private final List<Location> checkpoints = new ArrayList<>();
		private Location start, waiting;

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setArena(Location loc1, Location loc2) {
			arena = new Cuboid(loc1, loc2);
			return this;
		}

		public Builder setGameTime(int minutes) {
			this.gameTime = minutes;
			return this;
		}

		public Builder setMaxPlayer(int players) {
			this.maxPlayer = players;
			return this;
		}

		public Builder setMinPlayer(int players) {
			this.minPlayer = players;
			return this;
		}

		public Builder addCheckpoints(Location loc) {
			this.checkpoints.add(loc);
			return this;
		}

		public Builder setStart(Location loc) {
			start = loc;
			return this;
		}

		public Builder setWaiting(Location loc) {
			waiting = loc;
			return this;
		}

		public ParkourArena build() {
			Validate.notNull(arena, "Arena hasn't selected!");
			Validate.notNull(start, "Starting location hasn't selected!");
			Validate.notNull(waiting, "Waiting location hasn't selected!");
			Validate.notNull(gameTime, "Game time hasn't been set!");
			Validate.notNull(maxPlayer, "Max player hasn't been set!");
			Validate.notNull(minPlayer, "Min player hasn't been set!");
			return new ParkourArena(name, arena, start, waiting, gameTime, maxPlayer, minPlayer, checkpoints);
		}

	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("game-time", gameTime);
		map.put("max-player", maxPlayer);
		map.put("min-player", minPlayer);
		map.put("start", start);
		map.put("waiting", waiting);
		map.put("arena", arena);
		map.put("checkpoints", checkpoints);
		return map;
	}
}
