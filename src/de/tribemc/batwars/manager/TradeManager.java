package de.tribemc.batwars.manager;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.tribemc.batwars.main.BatWarsManager;
import de.tribemc.batwars.objects.TradeCategory;
import de.tribemc.batwars.objects.TradeInventorry;
import de.tribemc.batwars.objects.TradeItem;
import de.tribemc.tribeessentials.objects.server.TribeServer;

public class TradeManager {

	private BatWarsManager bwm;
	private TribeServer ts;

	private List<ItemStack> cats;
	private List<TradeInventorry> invs;

	public TradeManager(BatWarsManager bwm) {
		this.bwm = bwm;
		this.ts = bwm.getServer();
		this.cats = new LinkedList<>();
		this.invs = new LinkedList<>();
		setUpCatItems();
		setUpCatInvs();
	}

	private void setUpCatInvs() {

		// Blöcke
		TradeCategory cat0 = TradeCategory.BLOECKE;

		LinkedList<TradeItem> items0 = new LinkedList<>();
		// SandStone
		ItemStack item00 = ts
				.getItem(Material.SANDSTONE, (short) 0, null, null);
		item00.setAmount(2);
		items0.add(new TradeItem(item00, getBronze()));
		// GlowStone
		ItemStack item02 = ts
				.getItem(Material.GLOWSTONE, (short) 0, null, null);
		items0.add(new TradeItem(item02, getBronze(4)));
		// Glas
		ItemStack item01 = ts.getItem(Material.GLASS, (short) 0, null, null);
		item01.setAmount(8);
		items0.add(new TradeItem(item01, getBronze(16)));
		// Eis
		ItemStack item03 = ts.getItem(Material.ICE, (short) 0, null, null);
		item03.setAmount(2);
		items0.add(new TradeItem(item03, getBronze(4)));
		// Holz
		ItemStack item04 = ts.getItem(Material.WOOD, (short) 0, null, null);
		items0.add(new TradeItem(item04, getIron()));

		// Eisenblock
		ItemStack item05 = ts.getItem(Material.IRON_BLOCK, (short) 0, null,
				null);
		items0.add(new TradeItem(item05, getIron(3)));

		// Sandstein
		ItemStack item06 = ts.getItem(Material.ENDER_STONE, (short) 0, null,
				null);
		items0.add(new TradeItem(item06, getBronze(16)));
		this.invs.add(new TradeInventorry(cat0, items0, this));
		// Rüstung
		TradeCategory cat2 = TradeCategory.RUESTUNG;
		LinkedList<TradeItem> items2 = new LinkedList<>();

		// Leder Helm
		ItemStack item20 = ts.getItem(Material.LEATHER_HELMET, (short) 0, null,
				null);
		item20.addEnchantment(Enchantment.DURABILITY, 1);

		items2.add(new TradeItem(item20, getBronze()));
		// Leder Hose
		ItemStack item21 = ts.getItem(Material.LEATHER_LEGGINGS, (short) 0,
				null, null);
		item21.addEnchantment(Enchantment.DURABILITY, 1);

		items2.add(new TradeItem(item21, getBronze()));
		// Leder Schuhe
		ItemStack item22 = ts.getItem(Material.LEATHER_BOOTS, (short) 0, null,
				null);
		item22.addEnchantment(Enchantment.DURABILITY, 1);
		items2.add(new TradeItem(item22, getBronze()));

		// Kette 1
		ItemStack item23 = ts.getItem(Material.CHAINMAIL_CHESTPLATE, (short) 0,
				"§7Kettenhemd §7§lI", null);
		items2.add(new TradeItem(item23, getIron()));
		// Kette 2
		ItemStack item24 = ts.getItem(Material.CHAINMAIL_CHESTPLATE, (short) 0,
				"§7Kettenhemd §7§lII", null);
		item24.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		items2.add(new TradeItem(item24, getIron(3)));
		// Kette 3
		ItemStack item25 = ts.getItem(Material.CHAINMAIL_CHESTPLATE, (short) 0,
				"§7Kettenhemd §7§lIII", null);
		item25.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

		items2.add(new TradeItem(item25, getIron(6)));

		// Kette 5
		ItemStack item27 = ts.getItem(Material.CHAINMAIL_CHESTPLATE, (short) 0,
				"§7Kettenhemd §7§lV", null);
		item27.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		item27.addEnchantment(Enchantment.THORNS, 2);
		items2.add(new TradeItem(item27, getIron(24)));
		this.invs.add(new TradeInventorry(cat2, items2, this));

		// Waffen
		TradeCategory cat1 = TradeCategory.WAFFEN;

		LinkedList<TradeItem> items1 = new LinkedList<>();
		// Windbeutel 1
		ItemStack item10 = ts.getItem(Material.PAPER, (short) 0,
				"§fWindbeutel", null);
		item10.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		items1.add(new TradeItem(item10, getBronze(16)));
		// Windbeutel 2
		ItemStack item11 = ts.getItem(Material.PAPER, (short) 0,
				"§f§lWindbeutel", null);
		item11.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
		items1.add(new TradeItem(item11, getBronze(32)));
		// Holzschwert 1
		ItemStack item12 = ts.getItem(Material.WOOD_SWORD, (short) 0,
				"§cHolzschwert §c§lI", null);
		items1.add(new TradeItem(item12, getIron()));
		// Holzschwert 2
		ItemStack item13 = ts.getItem(Material.WOOD_SWORD, (short) 0,
				"§cHolzschwert §c§lII", null);
		item13.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		items1.add(new TradeItem(item13, getIron(3)));
		// Holzschwert 3
		ItemStack item14 = ts.getItem(Material.WOOD_SWORD, (short) 0,
				"§cHolzschwert §c§lIII", null);
		item14.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		items1.add(new TradeItem(item14, getIron(5)));
		// Eisenschwert I
		ItemStack item15 = ts.getItem(Material.IRON_SWORD, (short) 0,
				"§7Eisenschwert §7§lI", null);
		item15.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		items1.add(new TradeItem(item15, getGold(6)));
		// Eisenschwert II
		ItemStack item16 = ts.getItem(Material.IRON_SWORD, (short) 0,
				"§7Eisenschwert §7§lII", null);
		item16.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		items1.add(new TradeItem(item16, getGold(12)));
		this.invs.add(new TradeInventorry(cat1, items1, this));

		// Werkzeug
		TradeCategory cat4 = TradeCategory.WERKZEUG;
		LinkedList<TradeItem> items4 = new LinkedList<>();

		// Holz Hacke
		ItemStack item41 = ts.getItem(Material.WOOD_PICKAXE, (short) 0,
				"§3Spitzhacke §3§lI", null);
		items4.add(new TradeItem(item41, getBronze(6)));

		// Stein Hacke
		ItemStack item42 = ts.getItem(Material.STONE_PICKAXE, (short) 0,
				"§3Spitzhacke §3§lII", null);
		item42.addEnchantment(Enchantment.DIG_SPEED, 1);
		items4.add(new TradeItem(item42, getIron(2)));

		// Eisen Hacke
		ItemStack item43 = ts.getItem(Material.IRON_PICKAXE, (short) 0,
				"§3Spitzhacke §3§lIII", null);
		item43.addEnchantment(Enchantment.DIG_SPEED, 1);
		items4.add(new TradeItem(item43, getGold()));

		// Eisen Axt
		ItemStack item44 = ts.getItem(Material.IRON_AXE, (short) 0,
				"§3Axt §3§lI", null);
		item44.addEnchantment(Enchantment.DIG_SPEED, 1);
		items4.add(new TradeItem(item44, getGold(2)));

		this.invs.add(new TradeInventorry(cat4, items4, this));

		// Bogen
		TradeCategory cat5 = TradeCategory.BOGEN;
		LinkedList<TradeItem> items5 = new LinkedList<>();

		// Bogen I
		ItemStack item50 = ts.getItem(Material.BOW, (short) 0, "§6Bogen §6§lI",
				null);
		item50.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		items5.add(new TradeItem(item50, getGold(5)));

		// Bogen II
		ItemStack item51 = ts.getItem(Material.BOW, (short) 0,
				"§6Bogen §6§lII", null);
		item51.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		item51.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
		items5.add(new TradeItem(item51, getGold(8)));

		// Bogen II
		ItemStack item54 = ts.getItem(Material.BOW, (short) 0,
				"§6Bogen §6§lIII", null);
		item54.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		item54.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
		items5.add(new TradeItem(item54, getGold(12)));

		// Bogen III
		ItemStack item52 = ts.getItem(Material.BOW, (short) 0,
				"§6Bogen §6§lIV", null);
		item52.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		item52.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
		item52.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
		items5.add(new TradeItem(item52, getGold(20)));

		// Pfeil
		ItemStack item53 = ts.getItem(Material.ARROW, (short) 0, "§6Pfeil",
				null);
		items5.add(new TradeItem(item53, getGold()));

		this.invs.add(new TradeInventorry(cat5, items5, this));

		// Tränke
		TradeCategory cat6 = TradeCategory.TRAENKE;
		LinkedList<TradeItem> items6 = new LinkedList<>();
		// TODO

		/*
		 * // Heiltrank Level 1 ItemStack item60 = ts.getItem(Material.POTION,
		 * (short) 0, "§5Heil-Trank §lI", null); PotionMeta pm60 = (PotionMeta)
		 * item60.getItemMeta(); pm60.setMainEffect(PotionEffectType.HEAL);
		 * pm60.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 0, 0),
		 * true); item60.setItemMeta(pm60); items6.add(new TradeItem(item60,
		 * getIron(5)));
		 */
		// Heiltrank Level 2
		ItemStack item61 = ts.getItem(Material.POTION, (short) 8229,
				"§5Heil-Trank §lII", null);
		/*
		 * PotionMeta pm61 = (PotionMeta) item61.getItemMeta();
		 * pm61.setMainEffect(PotionEffectType.HEAL); pm61.addCustomEffect(new
		 * PotionEffect(PotionEffectType.HEAL, 0, 1), true);
		 * item61.setItemMeta(pm61);
		 */items6.add(new TradeItem(item61, getIron(8)));

		// Speed Trank
		ItemStack item62 = ts.getItem(Material.POTION, (short) 8194,
				"§5Speed-Trank", null);
		PotionMeta pm62 = (PotionMeta) item62.getItemMeta();
		pm62.setMainEffect(PotionEffectType.SPEED);
		pm62.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 120,
				0), true);
		item62.setItemMeta(pm62);
		items6.add(new TradeItem(item62, getIron(10)));

		// Stärke Trank
		ItemStack item63 = ts.getItem(Material.POTION, (short) 8291,
				"§5Stärke-Trank", null);
		PotionMeta pm63 = (PotionMeta) item63.getItemMeta();
		pm63.setMainEffect(PotionEffectType.INCREASE_DAMAGE);
		pm63.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
				20 * 60, 0), true);
		item63.setItemMeta(pm63);
		items6.add(new TradeItem(item63, getGold(10)));

		// Vergiftungstrank
		ItemStack item64 = ts.getItem(Material.POTION, (short) 16500,
				"§5Erblindungs-Trank", null);
		PotionMeta pm64 = (PotionMeta) item64.getItemMeta();
		pm64.setMainEffect(PotionEffectType.BLINDNESS);
		pm64.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS,
				20 * 15, 1), true);
		item64.setItemMeta(pm64);
		items6.add(new TradeItem(item64, getIron(32)));

		// Milch
		ItemStack item65 = ts.getItem(Material.MILK_BUCKET, (short) 0,
				"§3Wundermilch", null);
		items6.add(new TradeItem(item65, getGold(2)));

		this.invs.add(new TradeInventorry(cat6, items6, this));

		// Chest
		TradeCategory cat3 = TradeCategory.CHEST;
		LinkedList<TradeItem> items3 = new LinkedList<>();

		ItemStack item30 = ts.getItem(Material.CHEST, (short) 0, null, null);
		items3.add(new TradeItem(item30, getIron(2)));

		ItemStack item31 = ts.getChestListener().getShuffleItem();
		items3.add(new TradeItem(item31, getIron(6)));

		ItemStack item32 = ts.getItem(Material.ENDER_CHEST, (short) 0, null,
				null);
		items3.add(new TradeItem(item32, getGold(2)));

		ItemStack item33 = ts.getItem(Material.EYE_OF_ENDER, (short) 0,
				"§5Mobile Enderchest", null);
		items3.add(new TradeItem(item33, getGold(6)));

		this.invs.add(new TradeInventorry(cat3, items3, this));

		// Bridge
		TradeCategory cat7 = TradeCategory.BRUECKE;
		LinkedList<TradeItem> items7 = new LinkedList<>();
		// Brücke LEvel 1
		ItemStack item70 = ts.getItem(Material.LEASH, (short) 0,
				"§bBrücke §b§lI", "\n§7Temp");
		items7.add(new TradeItem(item70, getBronze(10)));

		// Brücke LEvel 2
		ItemStack item71 = ts.getItem(Material.LEASH, (short) 0,
				"§bBrücke §b§lII", "\n§7Temp");
		items7.add(new TradeItem(item71, getBronze(40)));

		// Brücke LEvel 3
		ItemStack item72 = ts.getItem(Material.LEASH, (short) 0,
				"§bBrücke §b§lIII", "\n§7Temp");
		items7.add(new TradeItem(item72, getIron(5)));

		// Brücke LEvel 4
		ItemStack item73 = ts.getItem(Material.LEASH, (short) 0,
				"§bBrücke §b§lIV", "\n§7Perm");
		items7.add(new TradeItem(item73, getIron(12)));

		// Brücke LEvel 5
		ItemStack item74 = ts.getItem(Material.LEASH, (short) 0,
				"§bBrücke §b§lV", "\n§7Perm");
		items7.add(new TradeItem(item74, getGold(4)));

		this.invs.add(new TradeInventorry(cat7, items7, this));

		// Spezial
		TradeCategory cat8 = TradeCategory.SPEZIAL;
		LinkedList<TradeItem> items8 = new LinkedList<>();
		// TODO

		ItemStack item81 = ts
				.getItem(
						Material.MONSTER_EGG,
						(short) 91,
						"§6§lJules",
						"\n§7Aktiviere mit §eRIGHT-CLICK\n§7Er rennt automatisch zum Gegner!\n§cEr kann auch dir hinterher laufen!");
		items8.add(new TradeItem(item81, getBronze(64)));

		ItemStack item86 = ts
				.getItem(
						Material.WOOD_HOE,
						(short) 0,
						"§6§lAntiBow",
						"\n§7Halte dieses Item in deiner Hand\n§7und du wirst nicht von Pfeilen getroffen!");
		item86.setAmount(5);
		items8.add(new TradeItem(item86, getBronze(64)));

		ItemStack item85 = ts
				.getItem(
						Material.WOOD_PLATE,
						(short) 0,
						"§5Teleporter",
						"\n§7Nutze dies für einen Teleport!\n§7Platziere mit §eRIGHT-Click\n§7Benutze indem du dich drauf stellst");
		items8.add(new TradeItem(item85, getIron(5)));

		ItemStack item80 = ts.getItem(Material.MONSTER_EGG, (short) 93,
				"§6§lFallschirm",
				"\n§7Aktiviere mit §eRIGHT-CLICK\n§7Beschleunige mit §eSHIFT");
		items8.add(new TradeItem(item80, getIron(8)));

		ItemStack item82 = ts.getItem(Material.TNT, (short) 0, "§6§lTNT",
				"\n§7Aktiviere mit §eRIGHT-CLICK\n§7Wirft das TNT!");
		items8.add(new TradeItem(item82, getGold(3)));

		ItemStack item84 = ts.getItem(Material.RAW_BEEF, (short) 0,
				"§cBlut-Steak",
				"\n§7Nutze dies für deine Bat!\nDeine Bat wird explodieren!");
		items8.add(new TradeItem(item84, getGold(5)));

		ItemStack item83 = ts.getItem(Material.ENDER_PEARL, (short) 0,
				"§6§lEnderperle", null);
		items8.add(new TradeItem(item83, getGold(15)));

		this.invs.add(new TradeInventorry(cat8, items8, this));
	}

	public List<ItemStack> getCatItems() {
		return this.cats;
	}

	public Inventory getInventorry(int i) {
		if (i >= 0 && i < this.invs.size())
			return this.invs.get(i).getInv();
		return null;
	}

	public ItemStack getCatItem(int i) {
		if (i >= 0 && i < this.cats.size())
			return this.cats.get(i);
		return null;
	}

	public BatWarsManager getManager() {
		return this.bwm;
	}

	private void setUpCatItems() {

		// BLOECKE(0), WAFFEN(1), RUESTUNG(2), ABBAU(3), BOGEN(4), TRAENKE(5),
		// BRIDGE(
		// 6), CHEST(7), SPEZIAL(8);

		// Bloecke
		ItemStack i0 = ts.getItem(Material.SANDSTONE, (short) 0, "§e§lBlöcke",
				"\n§7Kaufe Blöcke um wege zu bauen!");
		this.cats.add(i0);

		// Rüstung
		ItemStack i2 = ts.getItem(Material.CHAINMAIL_CHESTPLATE, (short) 0,
				"§e§lRüstung", "\n§7Kaufe Rüstung für Schutz!");
		this.cats.add(i2);
		// Waffen
		ItemStack i1 = ts.getItem(Material.IRON_SWORD, (short) 0, "§e§lWaffen",
				"\n§7Kaufe Waffen für den Nahkampf!");
		this.cats.add(i1);
		// Werkzeug
		ItemStack i3 = ts.getItem(Material.IRON_PICKAXE, (short) 0,
				"§e§lWerkzeug", "\n§7Kaufe Werkzeug zum Abbauen!");
		this.cats.add(i3);

		// Bogen
		ItemStack i4 = ts.getItem(Material.BOW, (short) 0, "§e§lBögen",
				"\n§7Kaufe Waffen für den Fernkampf!");
		this.cats.add(i4);

		// Tränke
		ItemStack i5 = ts.getItem(Material.POTION, (short) 0, "§e§lTränke",
				"\n§7Kaufe magische Tränke!");
		this.cats.add(i5);
		// Kiste
		ItemStack i7 = ts.getItem(Material.CHEST, (short) 0, "§e§lKisten",
				"\n§7Kaufe Kisten!");
		this.cats.add(i7);
		// Bridge
		ItemStack i6 = ts.getItem(Material.BRICK, (short) 0, "§e§lBrücken",
				"\n§7Kaufe Brücken um schnell zu bauen!");
		this.cats.add(i6);

		// Spezial
		ItemStack i8 = ts.getItem(Material.BLAZE_ROD, (short) 0, "§e§lSpezial",
				"\n§7Kaufe Extras für Überraschungen!");
		this.cats.add(i8);

	}

	public ItemStack getGold() {
		return getGold(1);
	}

	public ItemStack getIron() {
		return getIron(1);
	}

	public ItemStack getBronze() {
		return getBronze(1);
	}

	public ItemStack getGold(int i) {
		ItemStack item = ts.getItem(Material.GOLD_INGOT, (short) 0, "§6Gold",
				null);
		item.setAmount(i);
		return item;
	}

	public ItemStack getIron(int i) {
		ItemStack item = ts.getItem(Material.IRON_INGOT, (short) 0, "§7Eisen",
				null);
		item.setAmount(i);
		return item;

	}

	public ItemStack getBronze(int i) {
		ItemStack item = ts.getItem(Material.CLAY_BRICK, (short) 0, "§cBronze",
				null);
		item.setAmount(i);
		return item;

	}

	public ItemStack getSpacer() {
		return ts.getItem(Material.STAINED_GLASS_PANE, (short) 15, " ", null);
	}

	public TradeCategory isCategory(ItemStack item) {
		for (int i = 0; i < this.cats.size(); i++)
			if (this.cats.get(i).equals(item))
				return TradeCategory.valueOf(i);
		return null;
	}

	public TradeInventorry getTradeInv(TradeCategory cur) {
		for (TradeInventorry inv : this.invs)
			if (inv.getCat().equals(cur))
				return inv;
		return null;
	}
}
