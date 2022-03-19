package me.moty.parkour.game;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import me.moty.parkour.api.ParkourEndEvent;
import me.moty.parkour.api.ParkourStartEvent;
import me.moty.parkour.api.PlayerFinishParkourEvent;
import me.moty.parkour.api.PlayerLeaveParkourEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class ParkourGame {
	private final Plugin plugin;
	private final ParkourArena arena;
	private final Set<Player> players = new HashSet<>();
	private final HashMap<Player, Integer> checkpoints = new HashMap<>();
	private final HashMap<Integer, GameRecord> finished = new HashMap<>();
	private long start = System.currentTimeMillis();
	private GameStatus status = GameStatus.PREPARING;
	private BukkitTask task;

	public ParkourGame(ParkourArena arena, Plugin plugin) {
		this.plugin = plugin;
		this.arena = arena;
	}

	public ParkourArena getArena() {
		return arena;
	}

	public Set<Player> getPlayers() {
		return players;
	}

	public GameStatus getStatus() {
		return status;
	}

	public Location getLastCheckpoint(Player p) {
		if (checkpoints.get(p) == -1)
			return arena.getStartLocation().clone();
		return arena.getCheckpoints().get(checkpoints.get(p)).clone();
	}

	public boolean isPassedCheckpoint(Player p, int num) {
		return checkpoints.get(p) >= num;
	}

	public void setCheckpoint(Player p, int num) {
		this.checkpoints.put(p, num);
	}

	public GameRecord getGameRecord(int placement) {
		return finished.get(placement);
	}

	public Player getFinishedPlayer(int placement) {
		return plugin.getServer().getPlayer(finished.get(placement).getUUID());
	}

	public boolean hasPlacement(int placement) {
		return finished.size() >= placement;
	}

	public void join(Player player) {
		players.add(player);
		checkpoints.put(player, -1);
		player.teleportAsync(arena.getWaitingLocation());
	}

	public void quit(Player player, boolean end) {
		if (!end)
			players.remove(player);
		checkpoints.remove(player);
		PlayerLeaveParkourEvent event = new PlayerLeaveParkourEvent(player, this);
		plugin.getServer().getPluginManager().callEvent(event);
	}

	public void finish(Player p) {
		long now = System.currentTimeMillis();
		long left = now - start;
		GameRecord record = new GameRecord(p.getUniqueId(), now, finished.size() + 1, (left / 1000), arena.getName());
		p.sendMessage(Component.text().append(Component.text("耗費時間: " + left / 1000 + " 秒", NamedTextColor.YELLOW)));
		finished.put(finished.size() + 1, record);
		p.teleportAsync(arena.getWaitingLocation());
		PlayerFinishParkourEvent event = new PlayerFinishParkourEvent(p, record.getPlacement(), record);
		plugin.getServer().getPluginManager().callEvent(event);
	}

	public void start() {
		nextStage();
		players.stream().forEach(p -> {
			p.teleportAsync(arena.getStartLocation());
			p.showTitle(Title.title(Component.text().content("遊戲開始").color(NamedTextColor.GREEN).build(),
					Component.empty(), Times.of(Duration.ofSeconds(2), Duration.ofSeconds(1), Duration.ofSeconds(1))));
			p.playSound(Sound.sound(Key.key("entity.ender_dragon.ambient"), Sound.Source.RECORD, 1f, 1f));
		});
		start = System.currentTimeMillis();
		ParkourStartEvent event = new ParkourStartEvent(this);
		plugin.getServer().getPluginManager().callEvent(event);
	}

	public void gameOver() {
		players.forEach(p -> {
			if (!finished.keySet().stream().map(i -> plugin.getServer().getPlayer(finished.get(i).getUUID()))
					.collect(Collectors.toList()).contains(p))
				p.teleportAsync(arena.getWaitingLocation());
			p.sendMessage(Component.text()
					.append(Component.text("1st. ", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
					.append(Component.text((finished.size() >= 1 ? getFinishedPlayer(1).getName() : "無"),
							NamedTextColor.WHITE))
					.hoverEvent(HoverEvent.showText(Component
							.text("耗費時間: " + (finished.size() >= 1 ? getGameRecord(1).getTimeSpent() : 0) + " 秒")
							.color(NamedTextColor.YELLOW))));
			p.sendMessage(Component.text()
					.append(Component.text("2nd. ", NamedTextColor.AQUA).decorate(TextDecoration.BOLD))
					.append(Component.text((finished.size() >= 2 ? getFinishedPlayer(2).getName() : "無"),
							NamedTextColor.WHITE))
					.hoverEvent(HoverEvent.showText(Component
							.text("耗費時間: " + (finished.size() >= 2 ? getGameRecord(2).getTimeSpent() : 0) + " 秒")
							.color(NamedTextColor.YELLOW))));
			p.sendMessage(Component.text()
					.append(Component.text("3rd. ", NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
					.append(Component.text((finished.size() >= 3 ? getFinishedPlayer(3).getName() : "無"),
							NamedTextColor.WHITE))
					.hoverEvent(HoverEvent.showText(Component
							.text("耗費時間: " + (finished.size() >= 3 ? getGameRecord(3).getTimeSpent() : 0) + " 秒")
							.color(NamedTextColor.YELLOW))));
		});
	}

	public void end() {
		ParkourEndEvent event = new ParkourEndEvent(this);
		plugin.getServer().getPluginManager().callEvent(event);
		players.stream().forEach(p -> {
			quit(p, true);
			p.teleportAsync(p.getWorld().getSpawnLocation());
		});
		players.clear();
		finished.clear();
		nextStage();
	}

	public void stopCountin() {
		status = GameStatus.PREPARING;
		task.cancel();
		players.forEach(p -> {
			p.sendActionBar(Component.text("&c倒數停止，玩家人數不足"));
			p.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.RECORD, 1F, .5F));
		});
	}

	public void nextStage() {
		switch (status) {
		case END:
			status = GameStatus.PREPARING;
			task.cancel();
			break;
		case INGAME:
			status = GameStatus.END;
			task.cancel();
			task = new EndTask().runTaskTimer(plugin, 0, 20);
			break;
		case PREPARING:
			status = GameStatus.STARTING;
			task = new StartingTask().runTaskTimer(plugin, 0, 20);
			break;
		case STARTING:
			status = GameStatus.INGAME;
			task.cancel();
			task = new InGameTask().runTaskTimer(plugin, 0, 20);
			break;
		default:
			break;

		}
	}

	public class StartingTask extends BukkitRunnable {
		private int countdown = 30;

		@Override
		public void run() {
			if (!status.equals(GameStatus.STARTING))
				return;
			if (players.size() < arena.getMinPlayer()) {
				stopCountin();
				return;
			}
			if (countdown == 0) {
				start();
				return;
			}
			players.stream().forEach(p -> {
				p.sendActionBar(Component.text("倒數 " + countdown + " 秒").color(NamedTextColor.GOLD));
				p.playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.RECORD, 1f, 1f));
			});
			countdown--;
		}

	}

	public class InGameTask extends BukkitRunnable {
		private long gameTime = arena.getGameTime() * 1000 * 60;

		@Override
		public void run() {
			if (System.currentTimeMillis() - start > gameTime) {
				nextStage();
				return;
			}
			if (players.size() < 1) {
				nextStage();
				return;
			}
			if (finished.size() >= 3) {
				nextStage();
				return;
			}
		}

	}

	public class EndTask extends BukkitRunnable {
		private int countdown = 15;

		@Override
		public void run() {
			if (countdown == 0) {
				end();
				return;
			}
			if (countdown == 10)
				gameOver();
			countdown--;
		}

	}
}
