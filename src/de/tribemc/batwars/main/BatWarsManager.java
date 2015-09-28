package de.tribemc.batwars.main;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.event.HandlerList;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.UnknownDependencyException;

import de.tribemc.batwars.listener.GameListener;
import de.tribemc.batwars.listener.ServerConnect;
import de.tribemc.batwars.manager.BridgeManager;
import de.tribemc.batwars.manager.LoggerManager;
import de.tribemc.batwars.manager.MapManager;
import de.tribemc.batwars.manager.PortManager;
import de.tribemc.batwars.manager.ScoreBoardManager;
import de.tribemc.batwars.manager.SpawnerManager;
import de.tribemc.batwars.manager.TradeManager;
import de.tribemc.batwars.objects.AfterShow;
import de.tribemc.batwars.objects.BatData;
import de.tribemc.batwars.objects.BatLobbyData;
import de.tribemc.batwars.objects.BatMap;
import de.tribemc.batwars.objects.BatMapConfig;
import de.tribemc.batwars.objects.BatSpawner;
import de.tribemc.batwars.objects.BatSpawnerType;
import de.tribemc.batwars.objects.BatTeam;
import de.tribemc.batwars.objects.BatTeamConfig;
import de.tribemc.batwars.objects.BatWarsLogger;
import de.tribemc.batwars.objects.reflection.RegisterBatty;
import de.tribemc.batwars.objects.reflection.RegisterSheep;
import de.tribemc.batwars.objects.reflection.RegisterTrader;
import de.tribemc.batwars.objects.reflection.Sheep;
import de.tribemc.mglobby.main.Main;
import de.tribemc.mglobby.mapvote.MapVoteManager;
import de.tribemc.mglobby.objects.GameTeam;
import de.tribemc.mglobby.objects.LobbyData;
import de.tribemc.mglobby.objects.MapVote;
import de.tribemc.mglobby.teamselect.TeamSelectManager;
import de.tribemc.tribeessentials.listener.AntiBuildListener;
import de.tribemc.tribeessentials.objects.server.Spieler;
import de.tribemc.tribeessentials.objects.server.TribeServer;

public class BatWarsManager {

	public de.tribemc.batwars.main.Main m;
	public ServerStatus s;
	private TribeServer ts;

	MapManager mm;
	private TradeManager tm;

	private BatMap map;
	private GameListener gl;
	private AfterShow am;
	private BridgeManager bm;
	private SpawnerManager sm;
	private PortManager pm;
	private ScoreBoardManager sbm;

	private long gameSek;

	private RegisterSheep rs;
	private RegisterBatty rb;
	private RegisterTrader rt;
	private MessageListener ml;
	private LoggerManager lgg;

	public enum ServerStatus {
		RESTART, LOBBY, PREPAREGAME, INGAME, POSTGAME;
	}

	public BatWarsManager(TribeServer ts, de.tribemc.batwars.main.Main m) {
		this.ts = ts;

		this.m = m;
		this.ml = new MessageListener();
		this.mm = new MapManager(this);
		this.tm = new TradeManager(this);
		this.bm = new BridgeManager(this);
		this.sbm = new ScoreBoardManager(this);
		this.pm = new PortManager(this);
		this.lgg = new LoggerManager(this);

		this.rs = new RegisterSheep();
		this.rs.registerEntities();
		this.rb = new RegisterBatty();
		this.rb.registerEntities();
		this.rt = new RegisterTrader();
		this.rt.registerEntities();

		ts.getPluginManager().registerEvents(new ServerConnect(this), m);
		onRestartStart();
	}

	public TribeServer getServer() {
		return this.ts;
	}

	public de.tribemc.batwars.main.Main getPlugin() {
		return this.m;
	}

	@SuppressWarnings("deprecation")
	public void onRestartStart() {
		this.s = ServerStatus.RESTART;
		lgg.createLogger();
		System.out.println("[BatWars] Serverstatus: Restart");
		ts.getScheduler().scheduleAsyncDelayedTask(m, new Runnable() {

			@Override
			public void run() {
				if (map != null) {
					String world = map.getWorld().getName();
					ts.unloadWorld(world, true);
					mm.remove(world);
				}
				map = null;
				bm.reset();
				sbm.reset();
				pm.reset();
				gameSek = 0;
				System.out.println("[BatWars] Restart: Plugin resettet");
				getLogger().log("RESTART: Plugin resettet");
				List<World> worlds = ts.getWorlds();

				for (final World w : worlds) {
					if (!w.getName().equals("world")) {
						ts.getScheduler().scheduleSyncDelayedTask(m,
								new Runnable() {

									@Override
									public void run() {
										String name = w.getName();
										ts.getAntiBuildListener()
												.removeABWorld(w);
										if (ts.unloadWorld(name, true)) {
											mm.remove(name);
											System.out
													.println("[BatWars] Restart: Welt erfolgreich gelöscht");
											getLogger().log(
													"RESTART: Welt resettet");

										}
									}
								});

					}
				}
				System.out.println("[BatWars] Restart: Welten resettet");
				if (gl != null)
					HandlerList.unregisterAll(gl);
				System.out.println("[BatWars] Restart: Listener unregister");

				onRestartComplete();
			}
		});
	}

	public void onRestartComplete() {
		System.out.println("[BatWars] Restart: Abgeschlossen");
		getLogger().log("RESTART: abgeschlossen");

		onLobbyStart();
	}

	public BatWarsLogger getLogger() {
		return lgg.getLogger();
	}

	public void onLobbyStart() {
		ts.setWhitelist(false);
		System.out.println("[BatWars] Lobby: Beginne mit Setup");

		try {
			if (!ts.getPluginManager().isPluginEnabled("MGLobby"))
				m.getPluginLoader().enablePlugin(
						ts.getPluginManager().getPlugin("MGLobby"));
			System.out.println("[BatWars] Lobby: Lobby enabled");
			getLogger().log("LOBBY: Plugin geladen");

		} catch (UnknownDependencyException e) {
			e.printStackTrace();
			Bukkit.getServer().shutdown();
		}
		List<BatMapConfig> maps = mm.randomMapList();
		LinkedList<MapVote> mapvote = new LinkedList<>();
		List<GameTeam> teams = new LinkedList<>();
		for (BatMapConfig bmc : maps)
			mapvote.add(new MapVote(bmc.getName(), bmc.getDisplay()));
		for (BatTeamConfig btc : maps.get(0).getTeams())
			teams.add(new GameTeam(btc.getName(), btc.getMax(), btc
					.getItemData()));
		MapVoteManager mvm = new MapVoteManager(ts, mapvote, Main.getInstane()
				.getLobby());
		TeamSelectManager tsm = new TeamSelectManager(ts, teams);
		de.tribemc.mglobby.main.Main
				.getInstane()
				.getLobby()
				.setup(45, maps.get(0).getMax(), maps.get(0).getMin(), true,
						mvm, tsm);
		System.out.println("[BatWars] Lobby: Daten an Lobby uebergeben");
		getLogger().log("LOBBY: Daten an Plugin uebergeben");

		AntiBuildListener abl = ts.getAntiBuildListener();
		abl.setAllowBreak(-1);
		abl.setAllowBuild(-1);
		abl.setAllowDrop(-1);
		abl.setAllowPickUp(-1);
		abl.setAllowInteract(-1);
		System.out
				.println("[BatWars] Lobby: Allgemeine Welteinstellungen gesetzt");

		this.s = ServerStatus.LOBBY;
		System.out.println("[BatWars] Serverstatus: Lobby");
		getLogger().log("LOBBY: geladen");

	}

	public void onLobbyComplete(BatLobbyData bld) {
		System.out.println("[BatWars] Lobby: Abgeschlossen");

		this.s = ServerStatus.PREPAREGAME;
		System.out.println("[BatWars] Serverstatus: Preparing");
		this.mm.prepareMap(bld.getMap());

	}

	@SuppressWarnings("static-access")
	public void onPrePareGameStart(final BatMap map) {
		this.map = map;
		gl = new GameListener(m.bwm);
		ts.getScheduler().scheduleSyncDelayedTask(this.m, new Runnable() {

			@Override
			public void run() {
				YamlConfiguration cfg = map.getYAML();

				for (BatTeamConfig btc : map.getConfig().getTeams()) {
					Location spawn = new Location(map.getWorld(), cfg
							.getDouble("Team." + btc.getName()
									+ ".Location.PlayerSpawn.GetX"), cfg
							.getDouble("Team." + btc.getName()
									+ ".Location.PlayerSpawn.GetY"), cfg
							.getDouble("Team." + btc.getName()
									+ ".Location.PlayerSpawn.GetZ"),
							(float) cfg.getDouble("Team." + btc.getName()
									+ ".Location.PlayerSpawn.GetYaw"),
							(float) cfg.getDouble("Team." + btc.getName()
									+ ".Location.PlayerSpawn.GetPitch"));
					spawn.getChunk().load();
					Location batspawn = new Location(map.getWorld(), cfg
							.getDouble("Team." + btc.getName()
									+ ".Location.BatSpawn.GetX"), cfg
							.getDouble("Team." + btc.getName()
									+ ".Location.BatSpawn.GetY"), cfg
							.getDouble("Team." + btc.getName()
									+ ".Location.BatSpawn.GetZ"));
					batspawn.getChunk().load();
					Location tradespawn = new Location(map.getWorld(), cfg
							.getDouble("Team." + btc.getName()
									+ ".Location.TradeSpawn.GetX"), cfg
							.getDouble("Team." + btc.getName()
									+ ".Location.TradeSpawn.GetY"), cfg
							.getDouble("Team." + btc.getName()
									+ ".Location.TradeSpawn.GetZ"));
					tradespawn.getChunk().load();
					Villager v = (Villager) rt.spawnTrader(getManager(),
							tradespawn).getBukkitEntity();
					v.setAdult();
					v.setRemoveWhenFarAway(false);

					Bat bat = (Bat) rb.spawnBatty(getManager(), batspawn)
							.getBukkitEntity();
					bat.setRemoveWhenFarAway(false);
					BatTeam bt = new BatTeam(btc.getName(), ChatColor
							.valueOf(cfg.getString("Team." + btc.getCFGName()
									+ ".Color")), bat, spawn, batspawn,
							tradespawn, v, (short) cfg.getDouble("Team."
									+ btc.getCFGName() + ".ColorInt"));

					map.addTeam(bt);
					map.addProtectedBlock(batspawn);
					map.addProtectedBlock(spawn);
					map.addProtectedBlock(spawn.clone().add(0, 1, 0));
					map.addProtectedBlock(spawn.clone().add(0, 2, 0));
					map.addProtectedBlock(tradespawn);
					map.addProtectedBlock(tradespawn.clone().add(0, 1, 0));

				}
				System.out.println("[BatWars] Preapre: Teams aufgesetzt");
				getLogger().log("PREPARE: Teams geladen");

				map.setSpawn(new Location(map.getWorld(), cfg
						.getDouble("Map.Location.Spectator.GetX"), cfg
						.getDouble("Map.Location.Spectator.GetY"), cfg
						.getDouble("Map.Location.Spectator.GetZ"), (float) cfg
						.getDouble("Map.Location.Spectator.GetYaw"),
						(float) cfg
								.getDouble("Map.Location.Spectator.GetPitch")));
				map.setEndLoc(new Location(map.getWorld(), cfg
						.getDouble("Map.Location.End.GetX"), cfg
						.getDouble("Map.Location.End.GetY"), cfg
						.getDouble("Map.Location.End.GetZ"), (float) cfg
						.getDouble("Map.Location.End.GetYaw"), (float) cfg
						.getDouble("Map.Location.End.GetPitch")));
				sm = new SpawnerManager(getManager());
				for (String s : cfg.getConfigurationSection("Map.Spawner")
						.getKeys(false)) {
					sm.addSpawner(new BatSpawner(BatSpawnerType.valueOf(cfg
							.getString("Map.Spawner." + s + ".Type")),
							new Location(map.getWorld(), cfg
									.getDouble("Map.Spawner." + s
											+ ".Location.GetX"), cfg
									.getDouble("Map.Spawner." + s
											+ ".Location.GetY"), cfg
									.getDouble("Map.Spawner." + s
											+ ".Location.GetZ"))));
				}
				System.out.println("[BatWars] Preapre: Maps aufgesetzt");
				getLogger().log("PREPARE: Map geladen");

				for (Spieler sp : ts.getOnlineSpieler()) {
					if (sp.getExtra() instanceof LobbyData) {
						LobbyData ld = (LobbyData) sp.getExtra();
						BatData bd = new BatData(map.getTeam(ld.getTeam()),
								false);
						sp.setExtra(bd);
					} else {
						System.err.println("[BatWars] Konnte den Spieler "
								+ sp.getName() + " nicht hinzufügen!");
					}
				}
				System.out.println("[BatWars] Preapre: Spieler aufgesetzt");
				getLogger().log("PREPARE: Spieler geladen");

				for (BatTeam bt : map.getTeams())
					if (bt.countMember() == 0) {
						bt.setIngame(false);
					}

				getScoreBoardManager().setUp(map.getWorld().getName());
				ts.getPluginManager().registerEvents(gl, m);
				if (map.getIngameTeams() <= 1) {
					onPostGameComplete();
				}
				System.out
						.println("[BatWars] Preapre: Scoreboard / Checks aufgesetzt / durchgefuehrt");

				m.getPluginLoader().disablePlugin(
						m.getPlugin(de.tribemc.mglobby.main.Main.class));
				System.out.println("[BatWars] Preapre: Lobby disabled");

				onPrePareGameComplete();
			}
		});

	}

	public void onPrePareGameComplete() {
		System.out.println("[BatWars] Preapre: abegeschlossen");
		ts.broadcastMessage(ml.getMessage("bwm.GameStart"));
		onIngameStart();
	}

	public void onIngameStart() {
		this.s = ServerStatus.INGAME;
		System.out.println("[BatWars] Serverstatus: Ingame");

		AntiBuildListener abl = ts.getAntiBuildListener();
		abl.setAllowBreak(-1);
		abl.setAllowBuild(1);
		abl.setAllowDrop(1);
		abl.setAllowPickUp(1);
		abl.setAllowInteract(1);
		System.out.println("[BatWars] Ingame: Antibuild aufgesetzt");

		for (Spieler sp : ts.getOnlineSpieler()) {
			sp.teleport(((BatData) sp.getExtra()).getTeam().getSpawnLocation());
			sp.setGodMode(false);
			sp.setVanished(false);
			sp.heal();
			sp.fix();
			getScoreBoardManager().show(sp);
		}
		System.out.println("[BatWars] Ingame: Spieler aufgesetzt");

		this.sm.enable(BatSpawnerType.BRONZE);
		System.out.println("[BatWars] Ingame: Spiel gestartet");

	}

	@Deprecated
	public void onIngameComplete() {
	}

	public BatMap getMap() {
		return this.map;
	}

	public void onPostGameStart(BatTeam winner, List<Spieler> winPlayer) {
		this.s = ServerStatus.POSTGAME;
		System.out.println("[BatWars] Serverstatus: Postgame");

		am = new AfterShow(this.map.getFinal(), this);
		HandlerList.unregisterAll(this.gl);
		ts.getPluginManager().registerEvents(am, m);
		am.spawn();
		System.out.println("[BatWars] Postgame: AfterShow aufgesetzt");
		ts.broadcastMessage(ml.getMessage("bwm.end.TeamWin").replace("%0",
				winner.getColor() + winner.getName()));
		for (Spieler sp : winPlayer) {
			BatData data = (BatData) sp.getExtra();
			addStats(sp, data.getKills(), data.getHits(), data.getTode());
			sp.sendMessage(ml.getMessage("bwm.End.Winner"));

		}
		for (Spieler sp : ts.getOnlineSpieler()) {
			sp.teleport(this.map.getFinal());
			sp.setAllowFlight(true);
			sp.setFlying(true);
			sp.setGodMode(true);
			sp.clear();
			sp.setVanished(false);
			sp.setAllowPickUp(-1);
			sp.setAllowDrop(-1);
			sp.setAllowInteract(-1);
		}
		System.out.println("[BatWars] Postgame: Stats / Spieler erledigt");

	}

	public void onPostGameComplete() {
		for (Spieler sp : ts.getOnlineSpieler()) {
			sp.kickPlayer("§cRestart");
		}
		ts.getScheduler().scheduleSyncDelayedTask(m, new Runnable() {

			@Override
			public void run() {

				while (ts.getOnlineSpieler().size() > 0) {
					ts.getOnlineSpieler().get(0)
							.kickPlayer(ml.getMSG("bwm.restart.kick"));
				}
				System.out.println("[BatWars] Postgame: Spieler gekickt");
				getLogger().log("POST: Spieler gekickt");

				onRestartStart();
				System.out.println("[BatWars] Postgame: Restart eingeleitet");
			}
		}, 40);
		this.lgg.getLogger().save();
	}

	public ServerStatus getStatus() {
		return this.s;
	}

	public void specJoin(Spieler sp) {
		sp.teleport(this.map.getSpawn());
		getScoreBoardManager().show(sp);

		toGhost(sp);
	}

	public void toGhost(Spieler sp) {
		sp.setAllowPickUp(-1);
		sp.setAllowDrop(-1);
		sp.setAllowInteract(-1);
		sp.setVanished(true);
		ts.setVanish(sp, 0, true);
		sp.setAllowFlight(true);
		sp.setGodMode(true);
		sp.setFlying(true);
		sp.sendMessage(ml.getMessage("bwm.join.spectator"));
		sp.fix();
		sp.heal();
	}

	public BridgeManager getBridgeManager() {
		return this.bm;
	}

	public TradeManager getTradeManager() {
		return this.tm;
	}

	public SpawnerManager getSpawnerManager() {
		return this.sm;
	}

	public void onSek() {
		this.gameSek++;
		if (gameSek == 10) {
			sm.enable(BatSpawnerType.EISEN);
		} else if (gameSek == 40) {
			sm.enable(BatSpawnerType.GOLD);
		} else if (gameSek == 59) {
			for (BatTeam bt : this.map.getTeams()) {
				if (bt.canRespawn()) {
					bt.setHealth(100);
					getScoreBoardManager().update(bt);
				}
			}
		} else if ((double) gameSek % 60.0 == 0) {
			sbm.setDisplayName("" + ((int) gameSek / 60));
		} else if (gameSek > 499) {
			if (gameSek == 351) {
				getSpawnerManager().speedUp();
			} else if (gameSek == 599) {
				for (final BatTeam bt : this.map.getTeams()) {
					if (bt.getHealth() > 75) {
						bt.setHealth(75);
						bt.sendTeamMessage(ml.getMessage("bwm.sek.blitz"));
						ts.getScheduler().scheduleSyncDelayedTask(m,
								new Runnable() {

									@Override
									public void run() {
										map.getWorld().spawnEntity(
												bt.getBatSpawn(),
												EntityType.LIGHTNING);
									}
								});

						getScoreBoardManager().update(bt);
					}
				}
			} else if (gameSek == 899) {
				for (final BatTeam bt : this.map.getTeams()) {
					if (bt.getHealth() > 40) {
						bt.setHealth(40);
						bt.sendTeamMessage(ml.getMessage("bwm.sek.blitz"));
						ts.getScheduler().scheduleSyncDelayedTask(m,
								new Runnable() {

									@Override
									public void run() {
										map.getWorld().spawnEntity(
												bt.getBatSpawn(),
												EntityType.LIGHTNING);
									}
								});

						getScoreBoardManager().update(bt);
					}
				}
			} else if (gameSek == 1801) {
				for (final BatTeam bt : this.map.getTeams()) {

					bt.setHealth(0);
					bt.onBatDeath();
					bt.sendTeamMessage(ml.getMessage("bwm.sek.blitz"));
					ts.getScheduler().scheduleSyncDelayedTask(m,
							new Runnable() {

								@Override
								public void run() {
									map.getWorld().spawnEntity(
											bt.getBatSpawn(),
											EntityType.LIGHTNING);
								}
							});

					getScoreBoardManager().update(bt);

				}
			}
		}

	}

	public void onPlayerLeave(Spieler sp, boolean quit) {
		BatData data = (BatData) sp.getExtra();
		BatTeam team = data.getTeam();
		if (!team.isIngame())
			return;
		if (quit)
			ts.broadcastMessage(ml.getMessage("bwm.out.quit").replace("%0",
					sp.getChatName()));
		else {
			ts.broadcastMessage(ml.getMessage("bwm.out.out").replace("%0",
					sp.getChatName()));
			toGhost(sp);
		}
		getScoreBoardManager().update(team);
		if (pm.hasTeleport(sp))
			pm.removeTeleport(sp);
		addStats(sp, data.getKills(), data.getHits(), data.getTode());
		sp.setExtra(this.map.getSpecData());
		int player = team.countMember();
		if (player == 0) {
			team.setIngame(false);
			ts.broadcastMessage(ml.getMessage("bwm.out.team").replace("%0",
					"" + team.getColor() + team.getName()));

			int ingame = this.map.getIngameTeams();
			if (ingame <= 1) {
				BatTeam winner = this.map.getWinner();
				List<Spieler> winPlayer = new LinkedList<>();
				for (Spieler tar : this.ts.getOnlineSpieler()) {
					BatData bd = (BatData) tar.getExtra();
					if (bd.getTeam().equals(winner))
						winPlayer.add(tar);
				}

				onPostGameStart(winner, winPlayer);
			}
		}
	}

	private void addStats(Spieler sp, int kills, int hits, int tode) {
		// TODO
	}

	public BatWarsManager getManager() {
		return this;
	}

	public ScoreBoardManager getScoreBoardManager() {
		return this.sbm;
	}

	public void spawnSheep(Location add, Player player, DyeColor color,
			String team) {
		Sheep s = this.rs.spawnSheep(this, add, player, 0);
		TNTPrimed tnt = (TNTPrimed) add.getWorld().spawnEntity(add,
				EntityType.PRIMED_TNT);
		tnt.setFuseTicks(100);
		tnt.setIsIncendiary(false);
		tnt.setMetadata("tntTeam", new FixedMetadataValue(m, team));
		s.getBukkitEntity().setPassenger(tnt);
		((org.bukkit.entity.Sheep) s.getBukkitEntity()).setColor(color);
	}

	public AfterShow getShow() {
		return this.am;
	}

	public void spawnBatExplosion(BatTeam bt) {
		TNTPrimed tnt = (TNTPrimed) bt.getBatSpawn().getWorld()
				.spawnEntity(bt.getBatSpawn(), EntityType.PRIMED_TNT);
		tnt.setFuseTicks(1);
		tnt.setIsIncendiary(false);
		tnt.setYield(5F);
		tnt.setMetadata("tntTeam", new FixedMetadataValue(m, bt.getName()));
	}

	public MessageListener getMessageListener() {
		return this.ml;
	}

	public PortManager getPortManager() {
		return this.pm;
	}
}
