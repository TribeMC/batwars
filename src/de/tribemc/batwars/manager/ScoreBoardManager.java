package de.tribemc.batwars.manager;

import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import de.tribemc.batwars.main.BatWarsManager;
import de.tribemc.batwars.objects.BatTeam;
import de.tribemc.tribeessentials.objects.server.Spieler;
import de.tribemc.tribeessentials.objects.server.TribeServer;

public class ScoreBoardManager {

	private Scoreboard sb;
	private BatWarsManager bwm;
	private TribeServer ts;
	private Objective ob;

	public ScoreBoardManager(BatWarsManager bwm) {
		this.bwm = bwm;
		this.ts = bwm.getServer();
		this.sb = ts.getScoreboardManager().getMainScoreboard();
	}

	public void setUp(final String map) {
		ts.getScheduler().scheduleSyncDelayedTask(bwm.m, new Runnable() {
			@Override
			public void run() {
				ob = sb.registerNewObjective("BatWars", "ddd");

				ob.setDisplaySlot(DisplaySlot.SIDEBAR);
				setDisplayName("0");

				// Map
				Score sm = ob.getScore("§8=-=-=-=");
				sm.setScore(-1);

				// Map
				Score s1 = ob.getScore("§e" + map);
				s1.setScore(-2);

				// Map
				Score s2 = ob.getScore("§8by §1§lTribe");
				s2.setScore(-3);
				for (BatTeam bt : bwm.getMap().getTeams()) {
					if (bt.isIngame()) {
						Score s = ob.getScore(bt.getColor() + bt.getName());
						s.setScore((int) bt.getHealth());
					}
				}
			}

		});
	}

	public void setDisplayName(String time) {
		if (ob != null && time.length() < 4)
			ob.setDisplayName("§8BatWars §e" + time + "'");

	}

	public void update(BatTeam bt) {
		Score s = ob.getScore(bt.getColor() + bt.getName());
		if (bt.isIngame()) {
			if (bt.canRespawn()) {
				s.setScore((int) bt.getHealth());
			} else
				s.setScore(bt.countMember());
		} else {
			s.setScore(0);
		}
	}

	public void show(Spieler sp) {
		sp.setScoreboard(sb);
	}

	public void reset() {
		ts.getScheduler().scheduleSyncDelayedTask(bwm.m, new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (sb == null)
					return;
				sb.clearSlot(DisplaySlot.SIDEBAR);
				for (OfflinePlayer op : sb.getPlayers())
					sb.resetScores(op);
				if (ob == null)
					ob = sb.getObjective("BatWars");

				if (ob == null)
					return;
				ob.unregister();
				ob = null;
			}
		});

	}
}
