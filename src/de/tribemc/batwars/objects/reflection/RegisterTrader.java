package de.tribemc.batwars.objects.reflection;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.server.v1_7_R4.EntityTypes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import de.tribemc.batwars.main.BatWarsManager;

public class RegisterTrader {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean registerEntities() {
		try {
			Class entityTypeClass = EntityTypes.class;

			Field c = entityTypeClass.getDeclaredField("c");
			c.setAccessible(true);
			HashMap c_map = (HashMap) c.get(null);
			c_map.put("Trader", Trader.class);

			Field d = entityTypeClass.getDeclaredField("d");
			d.setAccessible(true);
			HashMap d_map = (HashMap) d.get(null);
			d_map.put(Trader.class, "Trader");

			Field e = entityTypeClass.getDeclaredField("e");
			e.setAccessible(true);
			HashMap e_map = (HashMap) e.get(null);
			e_map.put(120, Trader.class);

			Field f = entityTypeClass.getDeclaredField("f");
			f.setAccessible(true);
			HashMap f_map = (HashMap) f.get(null);
			f_map.put(Trader.class, 120);

			Field g = entityTypeClass.getDeclaredField("g");
			g.setAccessible(true);
			HashMap g_map = (HashMap) g.get(null);
			g_map.put("Trader", 120);

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public Trader spawnTrader(BatWarsManager bwm, final Location t) {
		final Object w = ((CraftWorld) t.getWorld()).getHandle();
		final Trader t_ = new Trader(
				(net.minecraft.server.v1_7_R4.World) ((CraftWorld) t.getWorld())
						.getHandle(), t);

		Bukkit.getScheduler().runTask(bwm.m, new Runnable() {
			public void run() {
				// t_.id = Block.getById(35);
				// t_.data = color;
				t_.setPosition(t.getX(), t.getY(), t.getZ());
				t_.setLocation(t.getX(), t.getY(), t.getZ(), t.getYaw(),
						t.getPitch());
				((net.minecraft.server.v1_7_R4.World) w).addEntity(t_,
						CreatureSpawnEvent.SpawnReason.CUSTOM);

			}
		});

		return t_;
	}
}
