package de.tribemc.batwars.objects;

import java.io.File;
import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import de.tribemc.tribeessentials.objects.AntiBuildBlock;
import de.tribemc.tribeessentials.objects.AntiBuildWorld;

public class BatMap {

	private World world;
	private AntiBuildWorld abworld;
	private BatMapConfig cfg;
	private YamlConfiguration config;
	private LinkedList<BatTeam> teams;
	private Vector min, max;
	private Location spawn;
	private Location endLoc;
	private BatData spec;

	public BatMap(BatMapConfig bmc, World w, AntiBuildWorld abworld) {
		this.cfg = bmc;
		this.world = w;
		this.config = YamlConfiguration.loadConfiguration(new File(cfg
				.getName() + "/config.yml"));
		this.teams = new LinkedList<>();
		this.abworld = abworld;
		this.min = new Vector(this.config.getDouble("Map.Boarder.MinX"), 0,
				this.config.getDouble("Map.Boarder.MinZ"));
		this.max = new Vector(this.config.getDouble("Map.Boarder.MaxX"), 256,
				this.config.getDouble("Map.Boarder.MaxZ"));

	}

	public void createSpecData() {
		this.spec = new BatData(new BatTeam("Geist", ChatColor.BLUE, null,
				getSpawn(), null, null, null, (short) 2), true);
		this.spec.getTeam().setIngame(false);
	}

	public World getWorld() {
		return this.world;
	}

	public void setSpawn(Location loc) {
		this.spawn = loc;
		this.endLoc = loc.clone();
	}

	public void setEndLoc(Location loc) {
		this.endLoc = loc;
	}

	public BatMapConfig getConfig() {
		return this.cfg;
	}

	public void addTeam(BatTeam team) {
		this.teams.add(team);
	}

	public void removeTeam(BatTeam team) {
		team.end();
		this.teams.remove(team);
	}

	public YamlConfiguration getYAML() {
		return this.config;
	}

	public BatTeam getTeam(String i) {
		for (BatTeam bt : this.teams)
			if (bt.getName().equalsIgnoreCase(i))
				return bt;
		return null;
	}

	public BatTeam getTeam(int i) {
		return this.teams.get(0);
	}

	public void addProtectedBlock(Location loc) {
		this.abworld.addBlock(new AntiBuildBlock(loc.getBlock(), 0, 0, 0, -1,
				1, false, false, false));
	}

	public void addBlock(Location loc) {
		this.abworld.addBlock(new AntiBuildBlock(loc.getBlock(), 0, 0, 0, 1, 1,
				false, false, false));
	}

	public void addBlock(Block block) {
		this.abworld.addBlock(new AntiBuildBlock(block, 0, 0, 0, 1, 1, false,
				false, false));
	}

	public boolean isKnown(Block block) {
		return this.abworld.isSpezialBlock(block);
	}

	public BatTeam getTeam(Entity e) {
		for (BatTeam t : this.teams) {
			if (t.getBat().getEntityId() == e.getEntityId())
				return t;
		}
		return null;
	}

	public boolean canBuild(Location loc) {
		if (isOutSide(loc))
			return false;
		AntiBuildBlock ab = this.abworld.getBlock(loc);
		if (ab == null)
			return true;
		return ab.getAllowBuild() > 0;
	}

	public void colorize() {
		for (BatTeam bt : this.teams)
			bt.colorize();
	}

	public Location getSpawn() {
		return this.spawn;
	}

	public BatData getSpecData() {
		if (this.spec == null)
			createSpecData();
		return this.spec;
	}

	public int getIngameTeams() {
		int i = 0;
		for (BatTeam bt : this.teams) {
			if (bt.isIngame())
				i++;
		}
		return i;
	}

	public BatTeam getWinner() {
		for (BatTeam bt : this.teams)
			if (bt.isIngame())
				return bt;
		return null;
	}

	public Location getFinal() {
		return this.endLoc;
	}

	public LinkedList<BatTeam> getTeams() {
		return this.teams;
	}

	public boolean isOutSide(Location loc) {
		return !loc.toVector().isInAABB(min, max);
	}

}
