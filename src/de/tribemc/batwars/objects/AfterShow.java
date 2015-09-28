package de.tribemc.batwars.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import de.tribemc.batwars.main.BatWarsManager;
import de.tribemc.tribeessentials.api.color.ColorConverter;
import de.tribemc.tribeessentials.api.particle.ParticleEffect;
import de.tribemc.tribeessentials.eventlistener.tribe.ServerSecEvent;
import de.tribemc.tribeessentials.objects.server.TribeServer;

public class AfterShow implements Listener {

	private Location loc;
	private TribeServer ts;
	private BatWarsManager bwm;
	private Color color;
	private Random r;
	int count;
	int timer;

	public AfterShow(Location loc, BatWarsManager bwm) {
		this.loc = loc;
		this.ts = bwm.getServer();
		this.r = new Random();
		this.bwm = bwm;
		this.color = ColorConverter.chatColorToColor(this.bwm.getMap()
				.getWinner().getColor());
		this.count = 5;
		this.timer = 30;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onSek(ServerSecEvent e) {

		if (timer == 0) {
			end();
			return;
		}
		timer--;
		if ((double) timer % 5D == 0) {
			ts.broadcastMessage(bwm.getMessageListener()
					.getMessage("aftershow.time").replace("%0", (timer) + ""));
		}
		ts.getScheduler().scheduleSyncDelayedTask(bwm.m, new Runnable() {

			@Override
			public void run() {
				int i = r.nextInt(2);

				while (i > -1) {
					Firework fw = (Firework) loc.getWorld().spawnEntity(
							getRandomLocation(), EntityType.FIREWORK);
					FireworkMeta fm = fw.getFireworkMeta();
					fm.addEffects(getEffects());
					fw.setFireworkMeta(fm);
					i--;
				}
				count--;
				if (count == 0 && timer > 5) {
					count = 7;
					try {
						ParticleEffect one = null;
						while (one == null || !one.useAble()) {
							one = ParticleEffect.values()[r.nextInt(43)];
						}
						ParticleEffect two = null;
						while (two == null || !two.useAble()) {
							two = ParticleEffect.values()[r.nextInt(43)];
						}
						ts.getParticleManager().formRadial(one, two, loc);
					} catch (Exception excep) {
						ts.getParticleManager().formSpriale(
								ParticleEffect.LAVA, loc);
					}
				}
			}
		});

	}

	private List<FireworkEffect> getEffects() {
		List<FireworkEffect> temp = new LinkedList<>();

		temp.add(FireworkEffect.builder().trail(r.nextBoolean())
				.withColor(getColor()).with(Type.values()[r.nextInt(5)])
				.build());

		return temp;
	}

	private Color getColor() {
		return this.color;
	}

	private Location getRandomLocation() {
		return loc.clone().add(
				(r.nextBoolean()) ? r.nextInt(20) : r.nextInt(20) * -1,
				r.nextInt(5),
				(r.nextBoolean()) ? r.nextInt(20) : r.nextInt(20) * -1);
	}

	public void spawn() {
	}

	public void end() {
		HandlerList.unregisterAll(this);
		ts.getScheduler().scheduleSyncDelayedTask(bwm.m, new Runnable() {
			@Override
			public void run() {
				bwm.onPostGameComplete();
			}
		});
	}

}
