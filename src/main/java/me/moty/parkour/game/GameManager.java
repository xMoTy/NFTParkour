package me.moty.parkour.game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.moty.parkour.NFTParkour;

public class GameManager {
	private NFTParkour m;
	private final HashMap<String, ParkourArena> arenas = new HashMap<>();
	private final List<ParkourGame> games = new ArrayList<>();
	private final File arenaFile;
	private FileConfiguration arena;

	public GameManager(NFTParkour m) {
		this.m = m;
		arenaFile = new File(m.getDataFolder().getAbsolutePath() + "/arenas.yml");
		init();
		load();
	}

	public void init() {
		if (!arenaFile.exists()) {
			if (!m.getDataFolder().exists())
				m.getDataFolder().mkdir();
			try {
				arenaFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		arena = YamlConfiguration.loadConfiguration(arenaFile);
	}

	public void load() {
		if (!arenas.isEmpty()) {
			arenas.clear();
			games.clear();
		}
		if (!arena.isSet("arenas") || arena.getConfigurationSection("arenas") == null)
			return;
		arena.getConfigurationSection("arenas").getKeys(false).stream().forEach(str -> {
			ParkourArena parkour = arena.getSerializable("arenas." + str, ParkourArena.class);
			arenas.put(str, parkour);
			games.add(new ParkourGame(parkour, this.m));
		});
	}

	public void save() {
		try {
			arena.save(arenaFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<ParkourGame> getGames() {
		return games;
	}

	public boolean existsArena(String name) {
		return arenas.containsKey(name);
	}

	public ParkourArena getArena(String name) {
		return arenas.get(name);
	}

	public boolean isInGame(Player p) {
		return games.stream().anyMatch(game -> game.getPlayers().contains(p));
	}

	public ParkourGame getGame(Player p) {
		return games.stream().filter(game -> game.getPlayers().contains(p)).findFirst().orElse(null);
	}

	public ParkourGame getGame(String arenaName) {
		return games.stream().filter(game -> game.getArena().getName().equalsIgnoreCase(arenaName)).findFirst()
				.orElse(null);
	}

	public void saveArena(ParkourArena arena) {
		this.arenas.put(arena.getName(), arena);
		this.arena.set("arenas." + arena.getName(), arena);
		games.add(new ParkourGame(arena, this.m));
		save();
	}

	public void deleteArena(String arena) {
		if (this.arenas.containsKey(arena))
			this.arenas.remove(arena);
		if (games.contains(getGame(arena)))
			games.remove(getGame(arena));
		this.arena.set("arenas." + arena, null);
		save();
	}
}
