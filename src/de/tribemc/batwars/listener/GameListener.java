package de.tribemc.batwars.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.tribemc.batwars.main.BatWarsManager;
import de.tribemc.batwars.main.MessageListener;
import de.tribemc.batwars.objects.BatBridge;
import de.tribemc.batwars.objects.BatData;
import de.tribemc.batwars.objects.BatTeam;
import de.tribemc.batwars.objects.Facing;
import de.tribemc.batwars.objects.TradeCategory;
import de.tribemc.batwars.objects.TradeInventorry;
import de.tribemc.tribeessentials.api.color.ColorConverter;
import de.tribemc.tribeessentials.eventlistener.tribe.ServerSecEvent;
import de.tribemc.tribeessentials.eventlistener.tribe.ServerTickEvent;
import de.tribemc.tribeessentials.eventlistener.tribe.SpielerChatEvent;
import de.tribemc.tribeessentials.eventlistener.tribe.SpielerCreateChatNameEvent;
import de.tribemc.tribeessentials.eventlistener.tribe.SpielerFixEvent;
import de.tribemc.tribeessentials.eventlistener.tribe.SpielerKnockBackEvent;
import de.tribemc.tribeessentials.objects.server.Spieler;
import de.tribemc.tribeessentials.objects.server.TribeServer;

public class GameListener implements Listener {

	private BatWarsManager bwm;
	private MessageListener ml;
	private TribeServer ts;

	public GameListener(BatWarsManager bwm) {
		this.bwm = bwm;
		this.ts = bwm.getServer();
		this.ml = bwm.getMessageListener();
	}

	@EventHandler
	public void onEntityBlockChange(EntityChangeBlockEvent e) {
		if (e.getEntity().getType().equals(EntityType.BAT)
				|| e.getEntity().getType().equals(EntityType.VILLAGER)) {
			e.setCancelled(true);
		} else if (e.getEntityType().equals(EntityType.PLAYER)) {
			if (e.getEntity().getFallDistance() >= 60) {
				((Player) e.getEntity()).damage(25D);
			}
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onSek(ServerSecEvent e) {
		bwm.getMap().colorize();
		bwm.onSek();
		bwm.getPortManager().onSek();

	}

	@EventHandler
	public void onTick(ServerTickEvent e) {
		bwm.getBridgeManager().buildBridges();
		bwm.getSpawnerManager().onTick();

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBuild(BlockPlaceEvent e) {
		if (e.isCancelled())
			return;
		if (!bwm.getMap().isKnown(e.getBlock())) {
			bwm.getMap().addBlock(e.getBlock());
		}
		if (e.getBlock().getType().equals(Material.ENDER_CHEST)) {
			Spieler sp = ts.getSpieler(e.getPlayer().getName());
			BatTeam bt = ((BatData) sp.getExtra()).getTeam();
			bt.addChest(e.getBlock().getLocation());
		} else if (e.getBlock().getType().equals(Material.WOOD_PLATE)) {
			Spieler sp = ts.getSpieler(e.getPlayer().getName());
			BatTeam bt = ((BatData) sp.getExtra()).getTeam();
			e.getBlock().setMetadata("Team",
					new FixedMetadataValue(bwm.m, bt.getName()));
			sp.sendMessage(ml.getMessage("game.place.teleporter"));
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (e.getBlock().getType().equals(Material.ENDER_CHEST)) {

			for (BatTeam bt : bwm.getMap().getTeams()) {
				if (bt.getChests().contains(e.getBlock().getLocation())) {
					bt.removeChest(e.getBlock().getLocation());
					bt.sendTeamMessage(ml.getMessage("game.break.urtc"));
					e.getPlayer().sendMessage(
							ml.getMessage("game.break.atc").replace("%0",
									bt.getColor() + bt.getName()));
				}
			}
			e.setCancelled(true);
			e.getBlock().setTypeIdAndData(0, (byte) 0, true);
			e.getBlock()
					.getLocation()
					.getWorld()
					.dropItemNaturally(
							e.getBlock().getLocation(),
							ts.getItem(Material.ENDER_CHEST, (short) 0, null,
									null));
		} else if (e.getBlock().getType().equals(Material.WOOD_PLATE)) {
			Spieler sp = ts.getSpieler(e.getPlayer().getName());
			if (bwm.getPortManager().hasTeleport(sp)) {
				bwm.getPortManager().removeTeleport(sp);
				sp.sendMessage(ml.getMessage("game.move.portcancel"));
			}
		}
	}

	@EventHandler
	public void onDemage(EntityDamageByEntityEvent e) {
		if (e.getEntityType().equals(EntityType.BAT)) {
			BatTeam bt = this.bwm.getMap().getTeam(e.getEntity());
			e.setCancelled(true);

			Spieler dmger = null;
			int dmg = 0;
			if (e.getDamager() instanceof Player) {
				dmger = ts.getSpieler(((Player) e.getDamager()).getName());
				if (!dmger.getGameMode().equals(GameMode.SURVIVAL)) {
					e.setCancelled(true);
					return;
				}
				if (dmger.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
					dmg = 20;
				else
					dmg = 15;
			} else if (e.getDamager() instanceof Projectile) {
				Projectile pro = (Projectile) e.getDamager();
				if (pro.getShooter() instanceof Player)
					dmger = ts
							.getSpieler(((Player) pro.getShooter()).getName());
				dmg = 10;
			} else {
				return;
			}
			if (dmger == null)
				return;
			if (dmger.hasGodMode())
				return;
			if (bt.isInTeam(dmger)) {
				dmger.sendMessage(ml.getMessage("game.bat.own"));
				return;
			}
			if (dmger.hasPotionEffect(PotionEffectType.BLINDNESS)) {
				dmger.sendMessage(ml.getMessage("game.bat.blind"));
				return;
			}
			double newHealth = bt.getHealth() - dmg;
			if (newHealth <= 0) {
				bt.setHealth(0);
				bt.onBatDeath();
				bt.sendTeamMessage(ml.getMessage("game.bat.death"));
				ts.broadcastMessage(ml.getMessage("game.bat.bcdeath")
						.replace("%0", bt.getColor() + bt.getName())
						.replace("%1", dmger.getChatName()));
				bwm.getScoreBoardManager().update(bt);
			} else {
				bt.setHealth(newHealth);
				bt.onBatHit();
				bt.sendTeamMessage(ml.getMessage("game.bat.hit"));
				bwm.getScoreBoardManager().update(bt);

			}

			dmger.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
					160, 0));
			if (dmg > 9 && bt.getExplosions() > 0) {
				bt.setExplosions(bt.getExplosions() - 1);
				bwm.spawnBatExplosion(bt);
			}
			dmger.sendMessage(ml.getMessage("game.bat.hitcomplete")
					.replace("%0", bt.getColor() + bt.getName())
					.replace("%1", dmg + ""));
		} else if (e.getEntityType().equals(EntityType.PLAYER)) {
			Spieler dmger = null;
			Spieler tar = ts.getSpieler(((Player) e.getEntity()).getName());
			BatData data = (BatData) tar.getExtra();
			BatTeam bt = (data).getTeam();

			if (e.getDamager() instanceof Player) {
				dmger = ts.getSpieler(((Player) e.getDamager()).getName());
				if (dmger.hasGodMode()) {
					return;
				}
			} else if (e.getDamager() instanceof Projectile) {
				Projectile pro = (Projectile) e.getDamager();
				if (pro.getShooter() instanceof Player) {
					dmger = ts
							.getSpieler(((Player) pro.getShooter()).getName());
					if (tar.getItemInHand() != null
							&& tar.getItemInHand().getType()
									.equals(Material.WOOD_HOE)) {

						ItemStack newHoe = tar.getItemInHand().clone();
						newHoe.setAmount(newHoe.getAmount() - 1);
						tar.setItemInHand(newHoe);
						if (tar.getItemInHand().getAmount() < 1) {
							tar.setItemInHand(null);
						}
						tar.updateInventory();
						e.setCancelled(true);
						return;
					}
				} else {
					e.setCancelled(true);
					return;
				}
			} else if (e.getDamager() instanceof TNTPrimed) {
				if (!e.getDamager().hasMetadata("tntTeam")) {
					e.setCancelled(true);
					return;
				}
				String team = e.getDamager().getMetadata("tntTeam").get(0)
						.asString();
				if (bt.getName().equals(team)) {
					e.setCancelled(true);
				}
				return;
			} else {
				if (e.getDamager().getType().equals(EntityType.LIGHTNING))
					e.setCancelled(true);
				return;
			}

			if (bt.isInTeam(dmger)) {
				e.setCancelled(true);
				return;
			}
			if (data.hasSpawnProtection()) {
				dmger.sendMessage(ml.getMessage("game.player.respawn").replace(
						"%0", tar.getChatName()));
				e.setCancelled(true);
				return;
			}
		} else {
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (!e.getFrom().getBlock().equals(e.getTo().getBlock())) {

			if (!((BatData) ts.getSpieler(e.getPlayer().getName()).getExtra())
					.isSpectator())
				for (Spieler te : ts.getOnlineSpieler()) {
					if (te.hasExtra())
						if (((BatData) te.getExtra()).isSpectator())
							if (te.getLocation().distance(e.getTo()) <= 3) {
								te.setVelocity(te.getLocation().toVector()
										.subtract(e.getFrom().toVector()));
							}
				}

			if (e.getPlayer().getFallDistance() > 2
					&& e.getTo().getBlockY() < this.bwm.getMap().getTeam(0)
							.getSpawnLocation().getBlockY() - 30) {
				e.getPlayer().damage(e.getPlayer().getHealth());
			} else if (bwm.getMap().isOutSide(e.getTo())) {
				e.getPlayer().setVelocity(
						bwm.getMap().getSpawn().toVector()
								.subtract(e.getTo().toVector()).normalize()
								.setY(-3));
				e.getPlayer().sendMessage(ml.getMessage("game.move.outside"));
				return;
			} else if (e.getFrom().getBlock().getType()
					.equals(Material.WOOD_PLATE)) {
				Spieler sp = ts.getSpieler(e.getPlayer().getName());
				if (bwm.getPortManager().hasTeleport(sp)) {
					bwm.getPortManager().removeTeleport(sp);
					sp.sendMessage(ml.getMessage("game.move.portcancel"));
				}
			} else if (e.getTo().getBlock().getType()
					.equals(Material.WOOD_PLATE)
					&& !e.getTo().getBlock().isBlockPowered()) {
				Spieler sp = ts.getSpieler(e.getPlayer().getName());
				BatTeam bt = ((BatData) sp.getExtra()).getTeam();
				if (e.getTo().getBlock().hasMetadata("Team"))
					if (e.getTo().getBlock().getMetadata("Team").get(0)
							.asString().equals(bt.getName())) {
						bwm.getPortManager().createPort(sp,
								bt.getSpawnLocation(), false);
					} else {
						bwm.getPortManager().createPort(sp,
								bt.getSpawnLocation(), true);
					}
			}
		}

		if (e.getPlayer().getPassenger() != null) {
			if (e.getPlayer().getPassenger() instanceof Chicken)
				if (e.getPlayer().isOnGround()) {
					e.getPlayer().getPassenger().remove();
				} else {
					e.getPlayer().setFallDistance(3);
					if (e.getPlayer().isSneaking()) {
						e.getPlayer().setVelocity(
								e.getPlayer().getVelocity().multiply(0.7));
					} else {
						e.getPlayer().setVelocity(
								e.getPlayer().getVelocity().multiply(0.4));
					}
				}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		try {
			Spieler sp = ts.getSpieler(e.getPlayer().getName());
			BatTeam bt = ((BatData) sp.getExtra()).getTeam();
			sp.getInventory().clear();
			if (bt.canRespawn() || !bt.isIngame()) {
				e.setRespawnLocation(bt.getSpawnLocation());
			} else {
				bwm.onPlayerLeave(sp, false);
			}
		} catch (Exception event) {
			e.setRespawnLocation(bwm.getMap().getSpawn());
		}

	}

	/*
	 * @EventHandler(priority = EventPriority.HIGH) public void
	 * onDemageHit(EntityDamageEvent e) { if (!e.isCancelled()) if
	 * (e.getEntityType().equals(EntityType.PLAYER)) { if (((LivingEntity)
	 * e.getEntity()).getHealth() - e.getDamage() <= 0) { e.setCancelled(true);
	 * Spieler sp = ts.getSpieler(((Player) e.getEntity()) .getName());
	 * onDeath(sp, (((Player) e.getEntity()).getKiller() != null) ? ts
	 * .getSpieler(((Player) e.getEntity()) .getKiller().getName()) : null);
	 * BatTeam bt = ((BatData) sp.getExtra()).getTeam(); sp.heal(); sp.clear();
	 * if (bt.canRespawn()) { sp.teleport(bt.getSpawnLocation());
	 * sp.teleport(sp.getLocation().add(0, 0.1, 0)); } else { bwm.toGhost(sp); }
	 * } } }
	 */

	@EventHandler
	public void onDemage(EntityDamageEvent e) {
		if (e.getCause().equals(DamageCause.SUFFOCATION)
				&& e.getEntity() instanceof Bat)
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent e) {
		e.getDrops().clear();
		e.setNewLevel(0);
		e.setDeathMessage(null);
		onDeath(ts.getSpieler(e.getEntity().getName()),
				(e.getEntity().getKiller() != null) ? ts.getSpieler(e
						.getEntity().getKiller().getName()) : null);
	}

	public void onDeath(Spieler tar, Spieler killer) {
		BatData data = (BatData) tar.getExtra();
		if (data == null)
			return;

		if (data.isSpectator())
			return;
		BatTeam bt = data.getTeam();
		if (bt == null)
			return;
		if (data.getLastDeath() - System.currentTimeMillis() > -50)
			return;
		data.setLastDeath(System.currentTimeMillis());
		if (killer == null) {
			ts.broadcastMessage(ml.getMessage("game.death.self").replace("%0",
					tar.getChatName()));
		} else {
			killer.sendMessage(ml.getMessage("game.death.killed").replace("%0",
					tar.getChatName()));
			ts.broadcastMessage(ml.getMessage("game.death.other")
					.replace("%0", tar.getChatName())
					.replace("%1", killer.getChatName()));
			if (!bt.canRespawn()) {
				bwm.onPlayerLeave(tar, false);
				return;
			}
			if (tar.getLocation().distance(bt.getBatSpawn()) > 16) {
				bt.setHealth(bt.getHealth() - 1);
				if (bt.getHealth() == 0)
					bt.onBatDeath();
				bwm.getScoreBoardManager().update(bt);
			}

		}
		tar.setExtra(data);
		if (tar.isDead())
			tar.respawn();
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if (e.getCurrentItem() != null
				&& !e.getCurrentItem().getType().equals(Material.AIR)) {

			if (e.getInventory().getTitle() != null) {
				if (e.getInventory().getTitle().startsWith("§6§lShop - §b§l")) {

					if (e.getRawSlot() < e.getInventory().getSize()) {
						e.setCancelled(true);
						TradeCategory tc = this.bwm.getTradeManager()
								.isCategory(e.getCurrentItem());
						if (tc != null) {
							e.getWhoClicked().openInventory(
									this.bwm.getTradeManager().getInventorry(
											tc.getID()));
						} else {
							if (e.getAction().equals(
									InventoryAction.HOTBAR_SWAP)) {
								e.getWhoClicked().openInventory(
										this.bwm.getTradeManager()
												.getInventorry(
														e.getHotbarButton()));
								return;
							}
							if (e.getCurrentItem().getType()
									.equals(Material.STAINED_GLASS_PANE))
								return;
							TradeCategory cur = TradeCategory.valueOf(e
									.getInventory().getTitle()
									.replace("§6§lShop - §b§l", ""));
							TradeInventorry inv = this.bwm.getTradeManager()
									.getTradeInv(cur);
							if (inv == null
									|| !(e.getWhoClicked() instanceof Player))
								return;
							inv.handleClick(((Player) e.getWhoClicked()),
									e.getCurrentItem(), e.isShiftClick());
						}
					} else {
						/*
						 * System.out.println("Action: " + e.getAction());
						 * System.out.println("SlotType: " + e.getSlotType());
						 * System.out.println("ClickType: " + e.getClick());
						 * System.out .println("CurrentItem: " +
						 * e.getCurrentItem()); System.out.println("Curser: " +
						 * e.getCursor()); System.out.println("RawSlot: " +
						 * e.getRawSlot()); System.out.println("Slot: " +
						 * e.getSlot()); System.out.println("Restult: " +
						 * e.getResult());
						 */if (e.getAction().equals(
								InventoryAction.COLLECT_TO_CURSOR)
								|| e.getAction()
										.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)
								|| e.getClick().equals(ClickType.DOUBLE_CLICK)) {
							e.setCancelled(true);

						}
					}
				}
			}
		}
	}

	@EventHandler
	@Deprecated
	public void onInvDrag(InventoryDragEvent e) {

		if (e.getView().getTopInventory() != null
				&& e.getView().getTopInventory().getTitle() != null) {
			if (e.getView().getTopInventory().getTitle()
					.startsWith("§6§lShop - §b§l")) {
				int topSlot = e.getView().getTopInventory().getSize();
				for (int i : e.getRawSlots()) {
					if (i < topSlot) {
						e.setCancelled(true);
						e.setResult(Result.DENY);
						return;
					}
				}
			}

		}

	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Villager) {
			e.setCancelled(true);
			e.getPlayer().openInventory(bwm.getTradeManager().getInventorry(0));
		} else if (e.getRightClicked() instanceof Bat) {
			if (e.getPlayer().getItemInHand() != null
					&& e.getPlayer().getItemInHand().getType()
							.equals(Material.RAW_BEEF)) {
				Spieler sp = ts.getSpieler(e.getPlayer().getName());
				BatTeam bt = ((BatData) sp.getExtra()).getTeam();
				if (!bt.canRespawn()) {
					sp.sendMessage(ml.getMessage("game.interact.noBat"));
					return;
				}
				if (e.getRightClicked().getEntityId() == bt.getBat()
						.getEntityId()) {
					e.setCancelled(true);
					if (e.getPlayer().getItemInHand().getAmount() == 1) {
						e.getPlayer()
								.setItemInHand(new ItemStack(Material.AIR));
					} else {
						e.getPlayer()
								.getItemInHand()
								.setAmount(
										e.getPlayer().getItemInHand()
												.getAmount() - 1);
					}
					bt.setExplosions(bt.getExplosions() + 1);
					sp.sendMessage(ml.getMessage("game.interact.explosive")
							.replace("%0", bt.getExplosions() + ""));
					return;
				} else {
					e.setCancelled(true);
					sp.sendMessage(ml.getMessage("game.interact.noturs"));
					return;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				&& e.getClickedBlock().getType().equals(Material.ENDER_CHEST)) {
			e.setCancelled(true);
			Spieler sp = ts.getSpieler(e.getPlayer().getName());
			BatTeam bt = ((BatData) sp.getExtra()).getTeam();
			if (bt.getChests().contains(e.getClickedBlock().getLocation())) {
				sp.openInventory(bt.getEnderChest());
			} else {
				sp.sendMessage(ml.getMessage("game.interact.notUrTC"));
			}
		}

		if (e.getItem() != null && !e.getItem().getType().equals(Material.AIR)) {
			if (e.getItem().getType().equals(Material.LEASH)) {
				if (e.getItem().getItemMeta().getDisplayName() != null) {
					String name = e.getItem().getItemMeta().getDisplayName();
					Facing f = Facing.exaktFacing(e.getPlayer()
							.getEyeLocation().getYaw());
					Spieler sp = ts.getSpieler(e.getPlayer().getName());
					BatTeam bt = ((BatData) sp.getExtra()).getTeam();
					BatBridge bridge = null;
					if (name.equals("§bBrücke §b§lI")) {
						bridge = new BatBridge(e.getPlayer().getLocation()
								.clone().subtract(0, 1, 0), 32, 160,
								(byte) bt.getColorID(), f, 6000);
					} else if (name.equals("§bBrücke §b§lII")) {
						bridge = new BatBridge(e.getPlayer().getLocation()
								.clone().subtract(0, 1, 0), 32, 95,
								(byte) bt.getColorID(), f, 12000);
					} else if (name.equals("§bBrücke §b§lIII")) {
						bridge = new BatBridge(e.getPlayer().getLocation()
								.clone().subtract(0, 1, 0), 32, 159,
								(byte) bt.getColorID(), f, 0);
					} else if (name.equals("§bBrücke §b§lIV")) {
						bridge = new BatBridge(e.getPlayer().getLocation()
								.clone().subtract(0, 1, 0), 48, 159,
								(byte) bt.getColorID(), f, 0);
					} else if (name.equals("§bBrücke §b§lV")) {
						bridge = new BatBridge(e.getPlayer().getLocation()
								.clone().subtract(0, 1, 0), 128, 95,
								(byte) bt.getColorID(), f, 0);
					}
					if (e.getItem().getAmount() == 1) {
						e.getPlayer()
								.setItemInHand(new ItemStack(Material.AIR));
					} else {
						e.getItem().setAmount(e.getItem().getAmount() - 1);
					}
					e.getPlayer().updateInventory();
					if (bridge != null)
						this.bwm.getBridgeManager().addBridge(bridge);
				}
			} else if (e.getItem().getType().equals(Material.MONSTER_EGG)) {
				e.setCancelled(true);
				if (e.getItem().getDurability() == (short) 93) {
					Chicken c = (Chicken) e
							.getPlayer()
							.getWorld()
							.spawnEntity(e.getPlayer().getLocation(),
									EntityType.CHICKEN);
					c.setAdult();
					c.setHealth(0.5D);
					e.getPlayer().setPassenger(c);

					if (e.getItem().getAmount() == 1) {
						e.getPlayer()
								.setItemInHand(new ItemStack(Material.AIR));
					} else {
						e.getItem().setAmount(e.getItem().getAmount() - 1);
					}
				} else if (e.getItem().getDurability() == (short) 91
						&& e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
						&& e.getBlockFace().equals(BlockFace.UP)) {
					if (e.getItem().getAmount() == 1) {
						e.getPlayer()
								.setItemInHand(new ItemStack(Material.AIR));
					} else {
						e.getItem().setAmount(e.getItem().getAmount() - 1);
					}

					Spieler sp = ts.getSpieler(e.getPlayer().getName());
					BatTeam bt = ((BatData) sp.getExtra()).getTeam();

					int distance = 200;
					Spieler tar = sp;
					for (Spieler temp : ts.getOnlineSpieler()) {
						if (!bt.isInTeam(temp)
								&& !((BatData) temp.getExtra()).isSpectator()
								&& temp != sp) {
							int dis = (int) temp.getLocation().distance(
									sp.getLocation());
							if (dis < distance) {
								distance = dis;
								tar = temp;
							}
						}
					}
					bwm.spawnSheep(
							e.getClickedBlock().getLocation().add(0, 1, 0),
							tar.getPlayer(),
							ColorConverter.chatToDye(bt.getColor()),
							bt.getName());

				}
			} else if (e.getItem().getType().equals(Material.TNT)) {
				e.setCancelled(true);
				if (e.getItem().getAmount() == 1) {
					e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
				} else {
					e.getItem().setAmount(e.getItem().getAmount() - 1);
				}

				Spieler sp = ts.getSpieler(e.getPlayer().getName());
				BatTeam bt = ((BatData) sp.getExtra()).getTeam();
				TNTPrimed tnt = (TNTPrimed) e
						.getPlayer()
						.getWorld()
						.spawnEntity(e.getPlayer().getEyeLocation(),
								EntityType.PRIMED_TNT);
				tnt.setFuseTicks(100);
				tnt.setIsIncendiary(false);
				tnt.setMetadata("tntTeam",
						new FixedMetadataValue(bwm.m, bt.getName()));
				tnt.setVelocity(e.getPlayer().getEyeLocation().getDirection()
						.multiply(1.7));
			} else if (e.getItem().getType().equals(Material.EYE_OF_ENDER)) {
				e.setCancelled(true);
				Spieler sp = ts.getSpieler(e.getPlayer().getName());
				BatTeam bt = ((BatData) sp.getExtra()).getTeam();
				sp.openInventory(bt.getEnderChest());
			}
		}
	}

	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		if (e.getFoodLevel() < ((Player) e.getEntity()).getFoodLevel()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onCraft(CraftItemEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onEnchant(EnchantItemEvent e) {
		if (e.getEnchanter() != null)
			e.setCancelled(true);
	}

	@EventHandler
	public void onBrew(BrewEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onFix(SpielerFixEvent e) {
		BatTeam bt = ((BatData) e.getSpieler().getExtra()).getTeam();
		e.setTeamPrefix("§8[" + bt.getColor());
		e.setTeamSuffix("§8]§r");
		e.setFixNear(false);
	}

	@EventHandler
	public void onChatCreateEvent(SpielerCreateChatNameEvent e) {
		if (!e.getSpieler().hasExtra())
			return;
		BatTeam bt = ((BatData) e.getSpieler().getExtra()).getTeam();
		e.setPrefix("§8[" + bt.getColor() + bt.getName() + "§8] "
				+ bt.getColor());
		e.setSuffix("§r");
		e.setUseGroupPrefix(false);
	}

	@EventHandler
	public void onBlockExplode(EntityExplodeEvent e) {
		int i = 0;
		while (i < e.blockList().size()) {
			if (bwm.getMap().isKnown(e.blockList().get(i))
					&& !e.blockList().get(i).getType().equals(Material.CHEST)) {
				i++;
			} else
				e.blockList().remove(i);
		}
	}

	@EventHandler
	public void onChat(SpielerChatEvent e) {
		if (e.getMessage().startsWith("@")) {
			BatTeam bt = ((BatData) e.getSpieler().getExtra()).getTeam();
			if (!bt.isIngame()) {
				e.getSpieler().sendMessage(
						ml.getMessage("game.chat.needingame"));
				e.setCancelled(true);
				return;
			}
			e.setMessage("§e" + e.getMessage().replaceAll("@", ""));
			e.setBroadcast(true);
		} else {
			e.setCancelled(true);
			BatTeam bt = ((BatData) e.getSpieler().getExtra()).getTeam();
			bt.sendTeamMessage(e.getSpieler().getChatName() + "§8: §7"
					+ e.getMessage());
			bwm.getServer()
					.getConsoleSender()
					.sendMessage(
							"[TeamChat] " + e.getSpieler().getChatName()
									+ "§8: §7" + e.getMessage());
		}
	}

	@EventHandler
	public void onKnockBack(SpielerKnockBackEvent e) {
		if (e.getSpieler().getItemInHand().getType().equals(Material.BOW)) {
			e.multiply(2);
		}
	}

	@EventHandler
	public void onBowDMG(PlayerItemDamageEvent e) {
		if (e.getItem().getType().equals(Material.BOW)) {
			e.setDamage(e.getDamage() * 3);
		}
	}

}
