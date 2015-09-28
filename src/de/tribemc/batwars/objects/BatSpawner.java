package de.tribemc.batwars.objects;

import org.bukkit.Location;

public class BatSpawner {

	private int curDelay;
	private BatSpawnerType type;
	private Location loc;

	public BatSpawner(BatSpawnerType type, Location loc) {
		this.curDelay = type.getDelay();
		this.type = type;
		this.loc = loc;
	}

	public int getDelay() {
		return this.curDelay;
	}

	public int getTickDelay() {
		return this.type.getDelay();
	}

	public BatSpawnerType getType() {
		return this.type;
	}

	public void minDelay() {
		if (curDelay < 0)
			curDelay = this.type.getDelay();
		this.curDelay--;

	}

	public Location getLocation() {
		return this.loc;
	}
}
