package de.tribemc.batwars.objects;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TradeItem {

	private ItemStack item;
	private ItemStack price;
	private ItemStack display;

	public TradeItem(ItemStack item, ItemStack price) {
		this.item = item;
		this.price = price;
		this.display = new ItemStack(item);
		ItemMeta im = this.display.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§9Information");
		lore.add("§e=-=-=-=-=-=-=");
		lore.add("§cPreis: §7" + this.price.getAmount() + "x "
				+ this.price.getItemMeta().getDisplayName());
		lore.add("§aDu erhälst: §7"
				+ this.item.getAmount()
				+ "x "
				+ ((this.item.getItemMeta().hasDisplayName()) ? this.item
						.getItemMeta().getDisplayName() : "§e"
						+ this.item.getType().name()));
		lore.add("§e=-=-=-=-=-=-=");
		im.setLore(lore);
		this.display.setItemMeta(im);
	}

	public ItemStack getDisplayItem() {
		return this.display;
	}

	public ItemStack getItem() {
		return this.item;
	}

	public ItemStack getPrice() {
		return this.price;

	}
}
