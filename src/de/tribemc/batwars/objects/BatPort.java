package de.tribemc.batwars.objects;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import de.tribemc.tribeessentials.api.particle.ParticleEffect;
import de.tribemc.tribeessentials.objects.server.Spieler;

public class BatPort {

	Spieler spieler;
	private Location target;
	private int delay;
	private BukkitTask rb;
	private Plugin pl;

	public BatPort(Spieler sp, Location loc, int delay, Plugin plugin) {
		this.spieler = sp;
		this.target = loc;
		this.delay = delay;
		this.pl = plugin;
		final ParticleEffect type = ParticleEffect.FIREWORKS_SPARK;
		this.rb = new BukkitRunnable() {
			double phi = 0;

			public void run() {
				phi += Math.PI / 10;
				for (double theta = 0; theta <= 2 * Math.PI; theta += +Math.PI / 40) {
					double x = 1.5 * Math.cos(theta) * Math.sin(phi);
					double y = 1.5 * Math.cos(phi);
					double z = 1.5 * Math.sin(theta) * Math.sin(phi);
					type.display(0, 0, 0, 0, 1, spieler.getLocation().clone()
							.add(x, y, z), 10);

				}
				if (phi > 2 * Math.PI) {
					this.cancel();
				}
			}

		}.runTaskTimer(pl, 0, 2);
	}

	public Spieler getSpieler() {
		return this.spieler;
	}

	public Location getTargetLocation() {
		return this.target;
	}

	public int getDelay() {
		return this.delay;
	}

	public void setDelay(int i) {
		this.delay = i;
	}

	public void cancel() {
		this.rb.cancel();
	}

}
