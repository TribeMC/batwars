package de.tribemc.batwars.objects;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.tribemc.tribeessentials.api.color.ColorConverter;
import de.tribemc.tribeessentials.api.particle.ParticleEffect;
import de.tribemc.tribeessentials.api.particle.ParticleEffect.OrdinaryColor;
import de.tribemc.tribeessentials.objects.server.Spieler;
import de.tribemc.tribeessentials.objects.server.TEssAPI;

public class BatTeam {

	String name;
	ChatColor color;
	Color raw;
	Bat bat;
	Location batspawn;
	Location spawn;
	Location tradeSpawn;
	Villager v;
	double health;
	boolean respawn;
	boolean ingame;
	private OrdinaryColor oc;
	private Inventory echest;
	private LinkedList<Location> chests;
	private int explosions;
	private short colorid;

	public BatTeam(String name, ChatColor color, Bat bat, Location spawn,
			Location batspawn, Location tradeSpawn, Villager v, short colorid) {
		this.name = name;
		this.color = color;
		this.bat = bat;
		this.spawn = spawn;
		this.respawn = true;
		this.ingame = true;
		this.raw = ColorConverter.chatColorToColor(this.color);
		this.batspawn = batspawn;
		this.v = v;
		this.tradeSpawn = tradeSpawn;
		this.explosions = 0;

		this.oc = new OrdinaryColor(this.color);
		this.health = 15;
		this.echest = Bukkit.createInventory(null, InventoryType.ENDER_CHEST,
				"§5§lEnderchest: " + color + name);
		this.chests = new LinkedList<>();
		this.colorid = colorid;
		// this.bat.setAwake(false);
		// this.bat.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
		// 99999, 10, false));

	}

	public String getName() {
		return this.name;
	}

	public Bat getBat() {
		return this.bat;
	}

	public void setBatName(String s) {
		this.bat.setCustomName(s);
	}

	public int getExplosions() {
		return this.explosions;
	}

	public void setExplosions(int i) {
		this.explosions = i;
	}

	public void setHealth(double d) {
		if (d <= 0) {
			this.respawn = false;
			this.bat.setHealth(0);
		}
		this.health = d;

	}

	public double getHealth() {
		return this.health;
	}

	public void addPotionEffekt(PotionEffect effect) {
		for (Spieler sp : TEssAPI.getInstance().getServer().getOnlineSpieler()) {
			BatData data = (BatData) sp.getExtra();
			if (this.equals(data.getTeam()))
				effect.apply(sp);
		}
	}

	public void sendTeamMessage(String msg) {
		for (Spieler sp : TEssAPI.getInstance().getServer().getOnlineSpieler()) {
			BatData data = (BatData) sp.getExtra();
			if (this.equals(data.getTeam()))
				sp.sendMessage(msg);
		}

	}

	public void end() {
		this.ingame = false;
	}

	public boolean isIngame() {
		return this.ingame;
	}

	public boolean canRespawn() {
		return this.respawn;
	}

	public boolean isInTeam(Spieler sp) {
		return ((BatData) sp.getExtra()).getTeam().equals(this);
	}

	public void onBatDeath() {
		PotionEffect pe = new PotionEffect(PotionEffectType.REGENERATION,
				999999, 0);
		addPotionEffekt(pe);

		this.respawn = false;
	}

	public void onBatHit() {
		PotionEffect pe = new PotionEffect(PotionEffectType.FAST_DIGGING, 200,
				3);
		addPotionEffekt(pe);
		PotionEffect pe1 = new PotionEffect(PotionEffectType.REGENERATION, 200,
				2);
		addPotionEffekt(pe1);
	}

	public Location getSpawnLocation() {
		return this.spawn;
	}

	public Location getTradeSpawn() {
		return this.tradeSpawn;
	}

	public Location getBatSpawn() {
		return this.batspawn;
	}

	public Villager getVillager() {
		return this.v;
	}

	public int countMember() {
		int i = 0;
		for (Spieler sp : TEssAPI.getInstance().getServer().getOnlineSpieler()) {
			BatData data = (BatData) sp.getExtra();
			if (data.getTeam().equals(this))
				i++;
		}
		return i;
	}

	public void colorize() {
		if (!this.bat.isDead()) {
			List<Player> temp = new LinkedList<>();
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getWorld().equals(this.batspawn.getWorld())
						&& p.getLocation().distanceSquared(this.batspawn) < 35)
					temp.add(p);
			}
			if (temp.size() > 0) {
				ParticleEffect.SPELL_MOB.display(this.oc, this.batspawn, temp);
				ParticleEffect.SPELL_MOB.display(this.oc, this.batspawn.clone()
						.add(0.25, 0, 0.25), temp);
				ParticleEffect.SPELL_MOB.display(this.oc, this.batspawn.clone()
						.add(-0.25, 0.0, -0.25), temp);
			}
		}

		for (Location loc : this.chests) {
			ParticleEffect.SPELL_MOB.display(this.oc, loc, 35);

		}
	}

	public ChatColor getColor() {
		return this.color;
	}

	public void setIngame(boolean b) {
		this.ingame = b;
		if (!b) {
			if (bat != null) {
				this.bat.remove();
				this.respawn = false;
			}
		}
	}

	public Color getRawColor() {
		return this.raw;
	}

	public void setRespawn(boolean b) {
		this.respawn = b;
	}

	public Inventory getEnderChest() {
		return this.echest;
	}

	public LinkedList<Location> getChests() {
		return this.chests;
	}

	public void addChest(Location loc) {
		this.chests.add(loc);
	}

	public void removeChest(Location loc) {
		this.chests.remove(loc);
	}

	public short getColorID() {
		return this.colorid;
	}
}
