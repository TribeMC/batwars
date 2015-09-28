package de.tribemc.batwars.manager;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import de.tribemc.batwars.main.BatWarsManager;
import de.tribemc.batwars.objects.BatMap;
import de.tribemc.batwars.objects.BatMapConfig;
import de.tribemc.tribeessentials.objects.AntiBuildWorld;

public class MapManager {

	private List<BatMapConfig> maps;

	private BatWarsManager bwm;
	private Random r;
	private int mapAmount;
	private CopyMapManager cmm;

	public MapManager(BatWarsManager bwm) {
		this.bwm = bwm;
		this.maps = new LinkedList<>();
		this.mapAmount = 2;
		this.r = new Random();
		this.cmm = new CopyMapManager(bwm);

		for (File f : cmm.getDir().listFiles()) {
			if (f.isDirectory()) {
				this.maps.add(new BatMapConfig(f));
			}
		}
	}

	public List<BatMapConfig> randomMapList() {
		if (this.mapAmount >= maps.size()) {
			return this.maps;
		}
		List<BatMapConfig> temp = new LinkedList<>();
		for (int i = 0; i < this.mapAmount; i++) {
			BatMapConfig random = this.maps.get(r.nextInt(this.mapAmount));
			while (temp.contains(random)) {
				random = this.maps.get(r.nextInt(this.mapAmount));
			}
			temp.add(random);
		}
		return temp;
	}

	@SuppressWarnings("deprecation")
	public void prepareMap(final String map) {
		bwm.getServer().getScheduler()
				.scheduleAsyncDelayedTask(bwm.m, new Runnable() {

					@Override
					public void run() {
						remove(map);
						final BatMapConfig bmc = getBMConfig(map);

						cmm.copyMap(bmc.getName());
						bwm.getServer().getScheduler()
								.scheduleSyncDelayedTask(bwm.m, new Runnable() {

									@Override
									public void run() {
										final World w = Bukkit.getServer()
												.createWorld(
														new WorldCreator(bmc
																.getName()));
										w.setGameRuleValue("doFireTick",
												"false");
										w.setGameRuleValue("doMobSpawning",
												"false");
										w.setGameRuleValue("doDaylightCycle",
												"false");
										bwm.getServer()
												.getScheduler()
												.scheduleAsyncDelayedTask(
														bwm.m, new Runnable() {

															@Override
															public void run() {
																AntiBuildWorld abworld = new AntiBuildWorld(
																		w, 1,
																		1, 1,
																		1, -1,
																		false,
																		false,
																		true);
																bwm.getServer()
																		.getAntiBuildListener()
																		.addABWorld(
																				abworld);
																BatMap bm = new BatMap(
																		bmc, w,
																		abworld);

																bwm.onPrePareGameStart(bm);
															}
														});
									}
								});

					}
				});

	}

	public BatMapConfig getBMConfig(String name) {
		for (BatMapConfig bmc : this.maps) {
			if (bmc.getName().equalsIgnoreCase(name))
				return bmc;

		}
		return null;
	}

	public void remove(World w) {
		cmm.deleteFiles(new File(w.getName()));
	}

	public void remove(String w) {
		cmm.deleteFiles(new File(w));
	}
}
