package me.moty.parkour.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SerializableAs("Cuboid")
public class Cuboid implements ConfigurationSerializable {

	private final int xMin;
	private final int xMax;
	private final int yMin;
	private final int yMax;
	private final int zMin;
	private final int zMax;
	private final double xMinCentered;
	private final double xMaxCentered;
	private final double yMinCentered;
	private final double yMaxCentered;
	private final double zMinCentered;
	private final double zMaxCentered;
	private final World world;

	public Cuboid(final Location point1, final Location point2, boolean fromBottomToTop) {
		this.world = point1.getWorld();
		this.xMin = Math.min(point1.getBlockX(), point2.getBlockX());
		this.xMax = Math.max(point1.getBlockX(), point2.getBlockX());
		if (fromBottomToTop) {
			this.yMin = world.getMinHeight();
			this.yMax = world.getMaxHeight();
		} else {
			this.yMin = Math.min(point1.getBlockY(), point2.getBlockY());
			this.yMax = Math.max(point1.getBlockY(), point2.getBlockY());
		}
		this.zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
		this.zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
		this.xMinCentered = this.xMin + 0.5;
		this.xMaxCentered = this.xMax + 0.5;
		this.yMinCentered = this.yMin + 0.5;
		this.yMaxCentered = this.yMax + 0.5;
		this.zMinCentered = this.zMin + 0.5;
		this.zMaxCentered = this.zMax + 0.5;
	}

	public Cuboid(Map<String, Object> paramMap) {
		this.world = Bukkit.getWorld((String) paramMap.get("world"));
		this.xMax = (int) paramMap.get("x-max");
		this.xMin = (int) paramMap.get("x-min");
		this.yMax = (int) paramMap.get("y-max");
		this.yMin = (int) paramMap.get("y-min");
		this.zMax = (int) paramMap.get("z-max");
		this.zMin = (int) paramMap.get("z-min");
		this.xMinCentered = this.xMin + 0.5;
		this.xMaxCentered = this.xMax + 0.5;
		this.yMinCentered = this.yMin + 0.5;
		this.yMaxCentered = this.yMax + 0.5;
		this.zMinCentered = this.zMin + 0.5;
		this.zMaxCentered = this.zMax + 0.5;
	}

	public Cuboid(final Location point1, final Location point2) {
		this(point1, point2, false);
	}

	public Iterator<Block> blockList() {
		final ArrayList<Block> bL = new ArrayList<>(this.getTotalBlockSize());
		for (int x = this.xMin; x <= this.xMax; ++x) {
			for (int y = this.yMin; y <= this.yMax; ++y) {
				for (int z = this.zMin; z <= this.zMax; ++z) {
					final Block b = this.world.getBlockAt(x, y, z);
					bL.add(b);
				}
			}
		}
		return bL.iterator();
	}

	public Location getCenter() {
		return new Location(this.world, (this.xMax - this.xMin) / 2 + this.xMin,
				(this.yMax - this.yMin) / 2 + this.yMin, (this.zMax - this.zMin) / 2 + this.zMin);
	}

	public double getDistance() {
		return this.getPoint1().distance(this.getPoint2());
	}

	public double getDistanceSquared() {
		return this.getPoint1().distanceSquared(this.getPoint2());
	}

	public int getHeight() {
		return this.yMax - this.yMin + 1;
	}

	public Location getPoint1() {
		return new Location(this.world, this.xMin, this.yMin, this.zMin);
	}

	public Location getPoint2() {
		return new Location(this.world, this.xMax, this.yMax, this.zMax);
	}

	public Location getRandomLocation() {
		final Random rand = new Random();
		final int x = rand.nextInt(Math.abs(this.xMax - this.xMin) + 1) + this.xMin;
		final int y = rand.nextInt(Math.abs(this.yMax - this.yMin) + 1) + this.yMin;
		final int z = rand.nextInt(Math.abs(this.zMax - this.zMin) + 1) + this.zMin;
		return new Location(this.world, x, y, z);
	}

	public int getTotalBlockSize() {
		return this.getHeight() * this.getXWidth() * this.getZWidth();
	}

	public int getXWidth() {
		return this.xMax - this.xMin + 1;
	}

	public int getZWidth() {
		return this.zMax - this.zMin + 1;
	}

	public boolean isIn(final Location loc) {
		return loc.getWorld() == this.world && loc.getBlockX() >= this.xMin && loc.getBlockX() <= this.xMax
				&& loc.getBlockY() >= this.yMin && loc.getBlockY() <= this.yMax && loc.getBlockZ() >= this.zMin
				&& loc.getBlockZ() <= this.zMax;
	}

	public boolean isIn(final Player player) {
		return this.isIn(player.getLocation());
	}

	public boolean isInWithMarge(final Location loc, final double marge) {
		return loc.getWorld() == this.world && loc.getX() >= this.xMinCentered - marge
				&& loc.getX() <= this.xMaxCentered + marge && loc.getY() >= this.yMinCentered - marge
				&& loc.getY() <= this.yMaxCentered + marge && loc.getZ() >= this.zMinCentered - marge
				&& loc.getZ() <= this.zMaxCentered + marge;
	}

	public int getLowestY() {
		return this.yMin;
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("x-max", xMax);
		map.put("x-min", xMin);
		map.put("y-max", yMax);
		map.put("y-min", yMin);
		map.put("z-max", zMax);
		map.put("z-min", zMin);
		map.put("world", world.getName());
		return map;
	}
}