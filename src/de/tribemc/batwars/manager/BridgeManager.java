package de.tribemc.batwars.manager;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import de.tribemc.batwars.main.BatWarsManager;
import de.tribemc.batwars.objects.BatBridge;

public class BridgeManager {

	private List<BatBridge> inBuild;
	private BatWarsManager bwm;
	private boolean b;

	public BridgeManager(BatWarsManager bwm) {
		this.bwm = bwm;
		this.inBuild = new LinkedList<>();
	}

	public void buildBridge(final BatBridge b) {
		final Location temp = b.getLocation().add(b.getVector());
		if (!temp.getBlock().getType().equals(Material.AIR) && b.hasStartet()) {
			removeBridge(b);
		} else {
			this.bwm.getServer().getScheduler()
					.scheduleSyncDelayedTask(bwm.m, new Runnable() {
						@Override
						public void run() {
							if (temp.getBlock().getType().equals(Material.AIR)) {
								if (bwm.getMap().canBuild(temp)) {
									if (b.hasDespawn()) {
										bwm.getServer()
												.getBlockRegenerationListener()
												.addBlock(
														temp.getBlock(),
														System.currentTimeMillis()
																+ b.getDespawn());
										bwm.getMap().addBlock(temp);
									}

									b.set();

								} else
									removeBridge(b);
							}
						}
					});
		}
		b.minDistance();
		if (b.isFinish())
			removeBridge(b);
	}

	private void removeBridge(BatBridge b) {
		this.inBuild.remove(b);
	}

	public void buildBridges() {
		b = !b;
		if (b) {
			if (this.inBuild.size() > 0)
				for (BatBridge b : this.inBuild
						.toArray(new BatBridge[this.inBuild.size()])) {
					buildBridge(b);
				}
		}
	}

	public void addBridge(BatBridge b) {
		this.inBuild.add(b);
	}

	public void reset() {
		this.inBuild.clear();
	}

	public boolean buildAble() {
		return !b;
	}
}
