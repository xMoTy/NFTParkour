package me.moty.parkour;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.Region;

import me.moty.parkour.api.PlayerJoinParkourEvent;
import me.moty.parkour.api.PlayerPreJoinParkourEvent;
import me.moty.parkour.game.GameStatus;
import me.moty.parkour.game.ParkourArena;
import me.moty.parkour.game.ParkourGame;

public class ParkourCommand implements CommandExecutor, TabCompleter {
	private NFTParkour m;
	private HashMap<Player, ParkourArena.Builder> gameBuilder = new HashMap<>();

	public ParkourCommand(NFTParkour m) {
		this.m = m;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String[] args) {
		if (!sender.isOp())
			return false;
		if (args.length < 1)
			return false;
		if (args[0].equalsIgnoreCase("reload")) {
			m.reloadConfiguration();
			m.getGameManager().init();
			m.getGameManager().load();
			sender.sendMessage(m.colorize("&a���󭫷s�t�m���\!"));
			return true;
		} else if (args[0].equalsIgnoreCase("join")) {
			if (args.length < 2)
				return false;
			if (!(sender instanceof Player))
				return false;
			Player p = (Player) sender;
			if (!m.getGameManager().existsArena(args[1]))
				return false;
			ParkourGame game = m.getGameManager().getGame(args[1]);
			if (game.getStatus() != GameStatus.PREPARING)
				return false;
			if (game.getPlayers().size() == game.getArena().getMaxPlayer())
				return false;
			PlayerPreJoinParkourEvent event = new PlayerPreJoinParkourEvent(p, (delay) -> {
				game.join(delay);
				PlayerJoinParkourEvent e = new PlayerJoinParkourEvent(p, game);
				this.m.getServer().getPluginManager().callEvent(e);
			});
			this.m.getServer().getPluginManager().callEvent(event);
			return true;
		} else if (args[0].equalsIgnoreCase("leave")) {
			if (!(sender instanceof Player))
				return false;
			Player p = (Player) sender;
			if (!m.getGameManager().isInGame(p))
				return false;
			ParkourGame game = m.getGameManager().getGame(p);
			game.quit(p, false);
			return true;
		} else if (args[0].equalsIgnoreCase("info")) {
			if (args.length < 2)
				return false;
			if (!m.getGameManager().existsArena(args[1]))
				return false;
			ParkourArena arena = m.getGameManager().getArena(args[1]);
			sender.sendMessage(m.colorize("&e���a�W��: &f" + arena.getName()));
			sender.sendMessage(m.colorize("&e�̤j�H��: &f" + arena.getMaxPlayer()));
			sender.sendMessage(m.colorize("&e�̤p�H��: &f" + arena.getMinPlayer()));
			sender.sendMessage(m.colorize("&e�C���ɪ�: &f" + arena.getGameTime()));
			sender.sendMessage(m.colorize("&e���~�I��: &f" + arena.getCheckpoints().size()));
			sender.sendMessage(m.colorize("&e�_�I�y��: &f" + arena.getStartLocation().getBlockX() + ", "
					+ arena.getStartLocation().getBlockY() + ", " + arena.getStartLocation().getBlockZ()));
			sender.sendMessage(m.colorize("&e���ݮy��: &f" + arena.getWaitingLocation().getBlockX() + ", "
					+ arena.getWaitingLocation().getBlockY() + ", " + arena.getWaitingLocation().getBlockZ()));
			return true;
		} else if (args[0].equalsIgnoreCase("create")) {
			if (args.length < 2)
				return false;
			if (m.getGameManager().existsArena(args[1]))
				return false;
			if (!(sender instanceof Player))
				return false;
			Player p = (Player) sender;
			if (gameBuilder.containsKey(p))
				sender.sendMessage(m.colorize("&c���s�C���Ыضi��!"));
			ParkourArena.Builder builder = new ParkourArena.Builder();
			builder.setName(args[1]);
			gameBuilder.put(p, builder);
			sender.sendMessage(m.colorize("&f" + args[1] + " &a�Ыئ��\! &7(�U�@�B: &e/parkour setarena&7)"));
			return true;
		} else if (args[0].equalsIgnoreCase("setarena")) {
			if (!(sender instanceof Player))
				return false;
			Player p = (Player) sender;
			if (!gameBuilder.containsKey(p))
				return false;
			BukkitPlayer wep = BukkitAdapter.adapt(p);
			if (!WorldEdit.getInstance().getSessionManager().get(wep)
					.isSelectionDefined(BukkitAdapter.adapt(p.getWorld()))) {
				sender.sendMessage(m.colorize("&c�C���ϰ�|�����!"));
				return false;
			}
			try {
				Region region = WorldEdit.getInstance().getSessionManager().get(wep).getSelection(wep.getWorld());
				gameBuilder.get(p).setArena(BukkitAdapter.adapt(p.getWorld(), region.getMaximumPoint()),
						BukkitAdapter.adapt(p.getWorld(), region.getMinimumPoint()));
				sender.sendMessage(m.colorize("&a�C���ϰ�]�w���\! &7(�U�@�B: &e/parkour setmax <�ƶq>&7)"));
				return true;
			} catch (IncompleteRegionException e) {
				e.printStackTrace();
				return false;
			}
		} else if (args[0].equalsIgnoreCase("setmax")) {
			if (args.length < 2)
				return false;
			if (!(sender instanceof Player))
				return false;
			Player p = (Player) sender;
			if (!gameBuilder.containsKey(p))
				return false;
			try {
				int num = Integer.parseInt(args[1]);
				gameBuilder.get(p).setMaxPlayer(num);
				sender.sendMessage(m.colorize("&a�̤j�H�Ƴ]�w���\! &7(�U�@�B: &e/parkour setmin <�ƶq>&7)"));
				return true;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return false;
			}
		} else if (args[0].equalsIgnoreCase("setmin")) {
			if (args.length < 2)
				return false;
			if (!(sender instanceof Player))
				return false;
			Player p = (Player) sender;
			if (!gameBuilder.containsKey(p))
				return false;
			try {
				int num = Integer.parseInt(args[1]);
				gameBuilder.get(p).setMinPlayer(num);
				sender.sendMessage(m.colorize("&a�̤p�H�Ƴ]�w���\! &7(�U�@�B: &e/parkour setgametime <�C���ɶ�(��)>&7)"));
				return true;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return false;
			}
		} else if (args[0].equalsIgnoreCase("setgametime")) {
			if (args.length < 2)
				return false;
			if (!(sender instanceof Player))
				return false;
			Player p = (Player) sender;
			if (!gameBuilder.containsKey(p))
				return false;
			try {
				int num = Integer.parseInt(args[1]);
				gameBuilder.get(p).setGameTime(num);
				sender.sendMessage(m.colorize("&a�C���ɶ��]�w���\! &7(�U�@�B: &e/parkour setwaiting&7)"));
				return true;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return false;
			}
		} else if (args[0].equalsIgnoreCase("setwaiting")) {
			if (!(sender instanceof Player))
				return false;
			Player p = (Player) sender;
			if (!gameBuilder.containsKey(p))
				return false;
			gameBuilder.get(p).setWaiting(p.getLocation());
			sender.sendMessage(m.colorize("&a�C�����ݰϳ]�w���\! &7(�U�@�B: &e/parkour setstart&7)"));
			return true;
		} else if (args[0].equalsIgnoreCase("setstart")) {
			if (!(sender instanceof Player))
				return false;
			Player p = (Player) sender;
			if (!gameBuilder.containsKey(p))
				return false;
			gameBuilder.get(p).setStart(p.getLocation());
			sender.sendMessage(m.colorize("&a�C���_�I�]�w���\! &7(�U�@�B: &e/parkour addcheckpoint&7)"));
			return true;
		} else if (args[0].equalsIgnoreCase("addcheckpoint")) {
			if (!(sender instanceof Player))
				return false;
			Player p = (Player) sender;
			if (!gameBuilder.containsKey(p))
				return false;
			if (p.getLocation().getBlock().isEmpty())
				return false;
			if (!p.getLocation().getBlock().getType().name().contains("PRESSURE_PLATE"))
				return false;
			Block plate = p.getLocation().getBlock();
			gameBuilder.get(p).addCheckpoints(plate.getLocation());
			sender.sendMessage(m.colorize("&a�C���ˬd�I�K�[���\! &7(�U�@�B: &e/parkour build&7)"));
		} else if (args[0].equalsIgnoreCase("build")) {
			if (!(sender instanceof Player))
				return false;
			Player p = (Player) sender;
			if (!gameBuilder.containsKey(p))
				return false;
			try {
				m.getGameManager().saveArena(gameBuilder.get(p).build());
				gameBuilder.remove(p);
				sender.sendMessage(m.colorize("&a�C���t�m���\!"));
			} catch (NullPointerException e) {
				sender.sendMessage(m.colorize("&c�C���t�m����! ��]�p�U:"));
				e.printStackTrace();
			}
			return true;
		}

		return false;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
			@NotNull String alias, @NotNull String[] args) {
		if (args.length == 1)
			return Arrays.asList("reload", "join", "quit", "create", "setarena", "setmax", "setmin", "setgametime",
					"setwaiting", "setstart", "addcheckpoint", "build");
		else if (args.length == 2 && args[0].equalsIgnoreCase("join"))
			return this.m.getGameManager().getGames().stream().filter(game -> game.getStatus() == GameStatus.PREPARING)
					.map(game -> game.getArena().getName()).collect(Collectors.toList());
		return null;
	}

}
