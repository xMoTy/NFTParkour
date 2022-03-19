package me.moty.parkour;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import me.moty.parkour.game.Cuboid;
import me.moty.parkour.game.GameManager;
import me.moty.parkour.game.ParkourArena;
import net.md_5.bungee.api.ChatColor;

public class NFTParkour extends JavaPlugin {

	private File dataFile;
	private FileConfiguration config;

	private static NFTParkour instance;
	private GameManager gm;

	public String host, database, username, password;
	public int port;
	public boolean ssl;

	@Override
	public void onEnable() {
		setInstance(this);
		getLogger().info("Powered by xMoTy#3812");
		reloadConfiguration();

		getServer().getPluginManager().registerEvents(new ParkourListener(this), this);
		ParkourCommand cmd = new ParkourCommand(this);
		getCommand("parkour").setExecutor(cmd);
		getCommand("parkour").setTabCompleter(cmd);

		ConfigurationSerialization.registerClass(Cuboid.class);
		ConfigurationSerialization.registerClass(ParkourArena.class);
		gm = new GameManager(this);
//		storage = new ParkourSQL(this);
	}

	public void reloadConfiguration() {
		dataFile = new File(getDataFolder().getAbsolutePath() + "/config.yml");
		if (!dataFile.exists()) {
			getDataFolder().mkdir();
			saveResource("config.yml", false);
		}
		reloadConfig();
		config = getConfig();

		host = config.getString("mysql.host");
		database = config.getString("mysql.database");
		username = config.getString("mysql.username");
		password = config.getString("mysql.password");
		port = config.getInt("mysql.port");
		ssl = config.getBoolean("mysql.ssl");
	}

	public GameManager getGameManager() {
		return gm;
	}

//	public ParkourSQL getStorage() {
//		return this.storage;
//	}

	public String colorize(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static NFTParkour getInstance() {
		return instance;
	}

	private static void setInstance(NFTParkour m) {
		NFTParkour.instance = m;
	}
}
