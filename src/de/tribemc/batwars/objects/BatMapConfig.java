package de.tribemc.batwars.objects;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class BatMapConfig {

	private String name;
	private int maxplayers;
	private int minplayers;
	private LinkedList<BatTeamConfig> teams;
	private ItemStack display;

	public BatMapConfig(String name, int max, int min,
			LinkedList<BatTeamConfig> teams, ItemStack display) {
		this.name = name;
		this.maxplayers = max;
		this.minplayers = min;
		this.teams = teams;
		this.display = display;
	}

	public BatMapConfig(File f) {
		File cfgfile = new File(f, "config.yml");

		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(cfgfile);
		this.name = cfg.getString("Map.Name");
		this.maxplayers = cfg.getInt("Map.Player.Max");
		this.minplayers = cfg.getInt("Map.Player.Min");
		this.display = cfg.getItemStack("Map.Display");

		this.teams = new LinkedList<>();
		for (String s : cfg.getConfigurationSection("Team").getKeys(false)) {
			this.teams.add(new BatTeamConfig(s, cfg.getString("Team." + s
					+ ".Name"), cfg.getInt("Map.Player.MaxTeam"), cfg
					.getInt("Team." + s + ".ColorInt")));
		}
	}

	public String getName() {
		return this.name;
	}

	public int getMax() {
		return this.maxplayers;
	}

	public int getMin() {
		return this.minplayers;
	}

	public List<BatTeamConfig> getTeams() {
		return this.teams;
	}

	public ItemStack getDisplay() {
		return this.display;
	}
}
