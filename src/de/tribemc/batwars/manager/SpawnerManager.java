package de.tribemc.batwars.manager;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.tribemc.batwars.main.BatWarsManager;
import de.tribemc.batwars.objects.BatSpawner;
import de.tribemc.batwars.objects.BatSpawnerType;

public class SpawnerManager {

	private BatWarsManager bwm;
	private TradeManager tm;
	private List<BatSpawner> enabled;
	private List<BatSpawner> toEnable;
	private List<BatSpawner> disabled;

	private int count;
	private int upTo;

	public SpawnerManager(BatWarsManager bwm) {
		this.bwm = bwm;
		this.tm = this.bwm.getTradeManager();
		reset();
		this.count = 1;
		this.upTo = 10;
	}

	public void addSpawner(BatSpawner spawner) {
		this.disabled.add(spawner);
	}

	public void enable(BatSpawnerType type) {
		for (BatSpawner bs : this.disabled) {
			if (bs.getType().equals(type)) {
				this.toEnable.add(bs);
			}
		}
		for (BatSpawner bs : this.toEnable) {
			this.disabled.remove(bs);
		}

	}

	public void onTick() {
		count--;
		if (count == 0) {
			onSek();
			count = upTo;
		}
	}

	public void onSek() {
		if (this.toEnable.size() != 0)
			while (this.toEnable.size() > 0) {
				this.enabled.add(this.toEnable.get(0));
				this.toEnable.remove(this.toEnable.get(0));
			}
		for (final BatSpawner bs : this.enabled) {
			bs.minDelay();
			if (bs.getDelay() == 0) {
				bwm.getServer().getScheduler()
						.scheduleSyncDelayedTask(bwm.m, new Runnable() {

							@Override
							public void run() {
								if (bs.getType().equals(BatSpawnerType.BRONZE)) {
									dropItem(bs.getLocation(), tm.getBronze());

								} else if (bs.getType().equals(
										BatSpawnerType.EISEN)) {
									dropItem(bs.getLocation(), tm.getIron());

								} else {
									dropItem(bs.getLocation(), tm.getGold());

								}
							}
						});

			}

		}
	}

	public void dropItem(Location loc, ItemStack item) {
		loc.getWorld().dropItemNaturally(loc, item)
				.setVelocity(new Vector(0, 0, 0));
		;
	}

	public void speedUp() {
		upTo = 8;
	}

	public void reset() {
		this.enabled = new LinkedList<>();
		this.toEnable = new LinkedList<>();
		this.disabled = new LinkedList<>();
	}
}
