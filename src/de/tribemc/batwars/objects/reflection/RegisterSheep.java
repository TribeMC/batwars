package de.tribemc.batwars.objects.reflection;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.server.v1_7_R4.EntityTypes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import de.tribemc.batwars.main.BatWarsManager;

public class RegisterSheep {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean registerEntities() {
		try {
			Class entityTypeClass = EntityTypes.class;

			Field c = entityTypeClass.getDeclaredField("c");
			c.setAccessible(true);
			HashMap c_map = (HashMap) c.get(null);
			c_map.put("Sheeep", Sheep.class);

			Field d = entityTypeClass.getDeclaredField("d");
			d.setAccessible(true);
			HashMap d_map = (HashMap) d.get(null);
			d_map.put(Sheep.class, "Sheeep");

			Field e = entityTypeClass.getDeclaredField("e");
			e.setAccessible(true);
			HashMap e_map = (HashMap) e.get(null);
			e_map.put(Integer.valueOf(91), Sheep.class);

			Field f = entityTypeClass.getDeclaredField("f");
			f.setAccessible(true);
			HashMap f_map = (HashMap) f.get(null);
			f_map.put(Sheep.class, Integer.valueOf(91));

			Field g = entityTypeClass.getDeclaredField("g");
			g.setAccessible(true);
			HashMap g_map = (HashMap) g.get(null);
			g_map.put("Sheeep", Integer.valueOf(91));

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public Sheep spawnSheep(BatWarsManager bwm, final Location t,
			Player target, final int color) {
		final Object w = ((CraftWorld) t.getWorld()).getHandle();
		final Sheep t_ = new Sheep(
				(net.minecraft.server.v1_7_R4.World) ((CraftWorld) t.getWorld())
						.getHandle(),
				(net.minecraft.server.v1_7_R4.Entity) ((CraftPlayer) target)
						.getHandle());

		Bukkit.getScheduler().runTask(bwm.m, new Runnable() {
			public void run() {
				// t_.id = Block.getById(35);
				// t_.data = color;
				t_.setPosition(t.getX(), t.getY(), t.getZ());
				((net.minecraft.server.v1_7_R4.World) w).addEntity(t_,
						CreatureSpawnEvent.SpawnReason.CUSTOM);
				t_.setColor(color);

			}
		});

		return t_;
	}
}
