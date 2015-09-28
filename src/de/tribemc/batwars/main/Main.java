package de.tribemc.batwars.main;

import java.util.List;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import de.tribemc.tribeessentials.objects.server.TEssAPI;

public class Main extends JavaPlugin {

	BatWarsManager bwm;

	@Override
	public void onEnable() {
		this.bwm = new BatWarsManager(TEssAPI.getInstance().getServer(), this);
		super.onEnable();
	}

	@Override
	public void onDisable() {
		List<World> worlds = bwm.getServer().getWorlds();

		for (World w : worlds) {
			if (!w.getName().equals("world")) {
				bwm.getServer().getAntiBuildListener().removeABWorld(w);
				if (bwm.getServer().unloadWorld(w, true)) {
					bwm.mm.remove(w);
				}
			}
		}
		super.onDisable();
	}
}
