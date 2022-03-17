package me.moty.parkour;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.moty.parkour.api.PlayerJoinParkourEvent;
import me.moty.parkour.api.PlayerPreJoinParkourEvent;
import me.moty.parkour.game.GameStatus;
import me.moty.parkour.game.ParkourGame;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class ParkourListener implements Listener {
	private NFTParkour m;

	public ParkourListener(NFTParkour m) {
		this.m = m;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction() != Action.PHYSICAL)
			return;
		if (e.getClickedBlock() == null)
			return;
		if (!m.getGameManager().isInGame(e.getPlayer()))
			return;
		ParkourGame game = m.getGameManager().getGame(e.getPlayer());
		Block block = e.getClickedBlock();
		if (!game.getArena().getArenaCuboid().isIn(block.getLocation()))
			return;
		if (game.getStatus() != GameStatus.INGAME)
			return;
		if (!block.getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
				&& !block.getType().equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE))
			return;
		e.setCancelled(true);
		if (block.getType().equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
			if (game.getLastCheckpoint(e.getPlayer()).equals(block.getLocation()))
				return;
			int num = game.getArena().getCheckpoints().stream()
					.filter(b -> b.equals(block.getLocation()) && game.getArena().getCheckpoints().indexOf(b) != -1)
					.map(b -> game.getArena().getCheckpoints().indexOf(b)).findFirst().orElse(-1);
			if (game.isPassedCheckpoint(e.getPlayer(), num))
				return;
			game.setCheckpoint(e.getPlayer(), num);
			e.getPlayer().sendMessage(m.colorize("&e你經過了 &a中繼點#" + (num + 1) + " &e!"));
			e.getPlayer().playSound(Sound.sound(Key.key("block.note_block.bell"), Sound.Source.RECORD, 1.5f, 2f));
		} else {
			if (!game.isPassedCheckpoint(e.getPlayer(), game.getArena().getCheckpoints().size() - 1))
				return;
			game.finish(e.getPlayer());
			e.getPlayer().sendMessage(m.colorize("&a你到達了終點!"));
			e.getPlayer().playSound(Sound.sound(Key.key("entity.firework_rocket.launch"), Sound.Source.RECORD, 1f, 1f));
		}
	}

	@EventHandler
	public void join(PlayerPreJoinParkourEvent e) {
		e.pass();
	}

	@EventHandler
	public void join(PlayerJoinParkourEvent e) {
		if (e.getGame().getPlayers().size() >= e.getGame().getArena().getMinPlayer())
			e.getGame().nextStage();
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (!m.getGameManager().isInGame(p))
			return;
		ParkourGame game = m.getGameManager().getGame(p);
		game.quit(p, false);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		Player p = (Player) e.getEntity();
		if (m.getGameManager().isInGame(p))
			e.setCancelled(true);
		if (e.getCause() == DamageCause.VOID)
			p.teleportAsync(m.getGameManager().getGame(p).getLastCheckpoint(p));
	}

	@EventHandler
	public void onBuild(BlockPlaceEvent e) {
		Player p = (Player) e.getPlayer();
		if (m.getGameManager().isInGame(p))
			e.setCancelled(true);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = (Player) e.getPlayer();
		if (m.getGameManager().isInGame(p))
			e.setCancelled(true);
	}

	@EventHandler
	public void onFoodBarChange(FoodLevelChangeEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		Player p = (Player) e.getEntity();
		if (!m.getGameManager().isInGame(p))
			return;
		if (e.getFoodLevel() < 20)
			e.setFoodLevel(20);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (!m.getGameManager().isInGame(e.getPlayer()))
			return;
		ParkourGame game = m.getGameManager().getGame(e.getPlayer());
		if (e.getPlayer().getLocation().getY() < game.getArena().getArenaCuboid().getLowestY()) {
			if (game.getStatus() == GameStatus.INGAME)
				e.getPlayer().teleportAsync(game.getLastCheckpoint(e.getPlayer()).add(.5, .5, .5));
			else
				e.getPlayer().teleportAsync(game.getArena().getWaitingLocation());
		}

	}

}
