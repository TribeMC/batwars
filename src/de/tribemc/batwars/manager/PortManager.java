package de.tribemc.batwars.manager;

import java.util.LinkedList;

import org.bukkit.Location;

import de.tribemc.batwars.main.BatWarsManager;
import de.tribemc.batwars.objects.BatPort;
import de.tribemc.tribeessentials.api.particle.ParticleEffect;
import de.tribemc.tribeessentials.objects.server.Spieler;

public class PortManager {

	private BatWarsManager bwm;
	private LinkedList<BatPort> ports;

	public PortManager(BatWarsManager bwm) {
		this.bwm = bwm;
		reset();
	}

	public void reset() {
		this.ports = new LinkedList<>();

	}

	@SuppressWarnings("deprecation")
	public void createPort(Spieler sp, Location tar, boolean instant) {
		if (instant) {
			createInstantParticle(sp.getLocation());
			sp.getLocation().getBlock().setTypeIdAndData(0, (byte) 0, true);
			sp.teleport(tar);
			sp.sendMessage(bwm.getMessageListener().getMessage("port.instant"));
			return;
		}
		this.ports.add(new BatPort(sp, tar, 5, bwm.getPlugin()));
		sp.sendMessage(bwm.getMessageListener().getMessage("port.start"));

	}

	private void createInstantParticle(Location loc) {
		ParticleEffect.HEART.display(2, 2, 2, 0, 5, loc, 10);
	}

	public boolean hasTeleport(Spieler sp) {
		return getTeleport(sp) != null;
	}

	public void removeTeleport(Spieler sp) {
		BatPort bp = getTeleport(sp);
		if (bp != null) {
			this.ports.remove(bp);
			bp.cancel();
		}
	}

	public BatPort getTeleport(Spieler sp) {
		for (BatPort bp : this.ports)
			if (bp.getSpieler().equals(sp))
				return bp;
		return null;
	}

	public void onSek() {
		int i = 0;
		while (i < this.ports.size()) {

			BatPort bp = this.ports.get(i);
			if (bp.getDelay() == 0) {
				finishPort(bp);
			} else {
				bp.setDelay(bp.getDelay() - 1);
				i++;
			}

		}
	}

	@SuppressWarnings("deprecation")
	private void finishPort(final BatPort bp) {
		bp.getSpieler().sendMessage(
				bwm.getMessageListener().getMessage("port.complete"));
		bwm.getServer().getScheduler()
				.scheduleSyncDelayedTask(bwm.m, new Runnable() {

					@Override
					public void run() {
						bp.getSpieler().getLocation().getBlock()
								.setTypeIdAndData(0, (byte) 0, true);
					}
				});

		bp.getSpieler().teleport(bp.getTargetLocation());
		this.ports.remove(bp);
	}
}
