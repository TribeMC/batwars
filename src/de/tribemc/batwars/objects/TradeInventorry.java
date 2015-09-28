package de.tribemc.batwars.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import de.tribemc.batwars.main.MessageListener;
import de.tribemc.batwars.manager.TradeManager;

public class TradeInventorry {

	private TradeCategory cat;
	private LinkedList<TradeItem> items;
	private Inventory inv;
	private TradeManager tm;
	private MessageListener ml;

	public TradeInventorry(TradeCategory cat, LinkedList<TradeItem> items,
			TradeManager tm) {
		this.cat = cat;
		this.tm = tm;
		this.ml = tm.getManager().getMessageListener();
		this.items = items;

		createInv();
	}

	private void createInv() {
		inv = Bukkit.createInventory(null, 36, "§6§lShop - §b§l" + cat.name());

		for (int i = 0; i < 9; i++) {
			inv.setItem(i, (i == cat.getID()) ? addGlowEffekt(tm.getCatItem(i))
					: tm.getCatItem(i));
		}
		int toAdd = (items.size() <= 1) ? 4 : (items.size() <= 3) ? 3 : (items
				.size() <= 5) ? 2 : (items.size() <= 7) ? 1 : 0;
		for (int i = 0; i < items.size(); i++) {
			ItemStack item = new ItemStack(items.get(i).getPrice());
			ItemMeta im = item.getItemMeta();
			List<String> lore = new ArrayList<>();
			lore.add("§7Du benötigst §a" + item.getAmount() + "x §r"
					+ im.getDisplayName() + " §7!");
			im.setLore(lore);
			item.setItemMeta(im);
			inv.setItem(18 + i + toAdd, item);
			inv.setItem(27 + i + toAdd, items.get(i).getDisplayItem());
		}

		while (inv.firstEmpty() != -1)
			inv.setItem(inv.firstEmpty(), this.tm.getSpacer());
	}

	public ItemStack addGlowEffekt(ItemStack item) {

		net.minecraft.server.v1_7_R4.ItemStack nmsStack = CraftItemStack
				.asNMSCopy(item);
		NBTTagCompound tag = null;
		if (!nmsStack.hasTag()) {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}
		if (tag == null)
			tag = nmsStack.getTag();
		NBTTagList ench = new NBTTagList();
		tag.set("ench", ench);
		nmsStack.setTag(tag);
		return CraftItemStack.asCraftMirror(nmsStack);
	}

	public Inventory getInv() {
		return this.inv;
	}

	public TradeCategory getCat() {
		return this.cat;
	}

	@SuppressWarnings("deprecation")
	public void handleClick(Player p, ItemStack clicked, boolean shift) {
		p.updateInventory();
		TradeItem ti = getTradeItem(clicked);
		if (ti == null) {
			p.sendMessage(ml.getMessage("trade.NoItem"));
			return;
		}

		int buyAmount = (shift) ? ti.getItem().getMaxStackSize()
				/ ti.getItem().getAmount() : 1;

		if (!hasNeeded(p, ti.getPrice(), ti.getPrice().getAmount() * buyAmount)) {
			if (shift) {
				int has = getAmount(p, ti.getPrice());
				buyAmount = (has / ti.getPrice().getAmount());

			}
			if (!shift || buyAmount == 0) {
				p.sendMessage(ml
						.getMessage("trade.noEnoughNeeded")
						.replace("%0",
								ti.getPrice().getItemMeta().getDisplayName())
						.replace(
								"%1",
								""
										+ (ti.getPrice().getAmount() * (ti
												.getItem().getMaxStackSize() / ti
												.getItem().getAmount()))));
				return;
			}
		}
		removeNeeded(p, ti.getPrice(), ti.getPrice().getAmount() * buyAmount);
		addItem(p, ti.getItem(), ti.getItem().getAmount() * buyAmount);
		p.playSound(p.getLocation(), Sound.CLICK, 1F, 1F);
	}

	public TradeItem getTradeItem(ItemStack item) {
		for (TradeItem ti : this.items)
			if (ti.getDisplayItem().equals(item))
				return ti;
		return null;
	}

	@SuppressWarnings("deprecation")
	public void removeNeeded(Player p, ItemStack i, int amount) {

		ItemStack[] contens = p.getInventory().getContents();

		int to = amount;
		for (int c = 0; c < contens.length; c++) {
			ItemStack inv = contens[c];
			if (inv != null && !inv.getType().equals(Material.AIR)) {

				if (i.getType().equals(inv.getType())
						&& i.getData().getData() == (inv.getData().getData())
						&& to > 0)

					if (inv.getAmount() > to) {

						contens[c].setAmount(inv.getAmount() - to);
						to = 0;
					} else {
						to -= inv.getAmount();
						contens[c] = new ItemStack(Material.AIR);
					}
			}
		}
		p.getInventory().setContents(contens);
		p.updateInventory();
	}

	public void addItem(Player p, ItemStack i, int j) {
		ItemStack item = i.clone();
		item.setAmount(j);
		if (this.cat.equals(TradeCategory.RUESTUNG)) {
			// COLORIZE
			if (item.getItemMeta() instanceof LeatherArmorMeta) {
				LeatherArmorMeta lm = (LeatherArmorMeta) item.getItemMeta();
				lm.setColor(((BatData) tm.getManager().getServer()
						.getSpieler(p.getName()).getExtra()).getTeam()
						.getRawColor());
				item.setItemMeta(lm);
			}
			// Set Armor
			if (p.getInventory().getBoots() == null
					&& item.getType().equals(Material.LEATHER_BOOTS)) {
				p.getInventory().setBoots(item);
				return;
			} else if (p.getInventory().getLeggings() == null
					&& item.getType().equals(Material.LEATHER_LEGGINGS)) {
				p.getInventory().setLeggings(item);
				return;
			} else if (p.getInventory().getChestplate() == null
					&& item.getType().equals(Material.CHAINMAIL_CHESTPLATE)) {
				p.getInventory().setChestplate(item);
				return;
			} else if (p.getInventory().getHelmet() == null
					&& item.getType().equals(Material.LEATHER_HELMET)) {
				p.getInventory().setHelmet(item);
				return;
			}
		}
		if (p.getInventory().firstEmpty() == -1) {
			p.getWorld().dropItem(p.getLocation(), item);
		} else {
			p.getInventory().addItem(item);
		}
	}

	public boolean hasNeeded(Player p, ItemStack i, int amount) {
		int to = amount;
		for (ItemStack inv : p.getInventory().getContents()) {
			if (inv != null && !inv.getType().equals(Material.AIR))
				if (i.getType().equals(inv.getType())
						&& i.getData().equals(inv.getData()) && to > 0)
					if (inv.getAmount() >= to) {
						to = 0;
					} else {
						to -= inv.getAmount();
					}
		}
		if (to > 0)
			return false;

		return true;
	}

	public int getAmount(Player p, ItemStack i) {
		int amount = 0;
		for (ItemStack inv : p.getInventory().getContents()) {
			if (inv != null && !inv.getType().equals(Material.AIR))
				if (i.getType().equals(inv.getType())
						&& i.getData().equals(inv.getData()))
					amount += inv.getAmount();
		}
		return amount;
	}

	public boolean hasNeeded(Player p, ItemStack i) {
		return hasNeeded(p, i, i.getAmount());
	}

}
