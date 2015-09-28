package de.tribemc.batwars.objects;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

public class BatBridge {

	public int distance;
	public int id;
	public byte data;

	private Location loc;
	public Vector vec;

	public boolean change;
	public Vector vec2;

	private long despawn;
	private boolean startet;

	public BatBridge(Location loc, int distance, int id, byte data,
			Facing face, long despawn) {
		this.distance = distance;
		this.id = id;
		this.data = data;
		this.loc = loc;
		this.despawn = despawn;

		if (face.isAxis()) {
			this.vec = new Vector(face.getX(), 0, face.getZ());
		} else {
			this.change = true;
			this.vec = new Vector(face.getX(), 0, 0);
			this.vec2 = new Vector(0, 0, face.getZ());
		}
	}

	public Vector getVector() {
		if (!change)
			return this.vec;
		else
			return (distance % 2 == 0) ? this.vec : this.vec2;
	}

	public void minDistance() {
		this.distance--;
	}

	public Location getLocation() {
		return this.loc;
	}

	public boolean isFinish() {
		return this.distance <= 0;
	}

	@SuppressWarnings("deprecation")
	public void set() {
		loc.getBlock().setTypeIdAndData(this.id, this.data, true);
		loc.getWorld().playEffect(loc, Effect.STEP_SOUND, id);
		loc.getWorld().playSound(loc, Sound.ZOMBIE_WOODBREAK, 4F, 2F);
		if (!hasStartet())
			this.startet = true;
	}

	public boolean hasDespawn() {
		return this.despawn > 0;
	}

	public boolean hasStartet() {
		return this.startet;
	}

	public long getDespawn() {
		return this.despawn;
	}
}
