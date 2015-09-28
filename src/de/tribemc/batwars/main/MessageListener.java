package de.tribemc.batwars.main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageListener {

	private File f;
	private YamlConfiguration cfg;
	private HashMap<String, String> messages;

	public MessageListener() {
		f = new File("plugins/BatWars", "messages.yml");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		this.messages = new LinkedHashMap<>();
		this.cfg = YamlConfiguration.loadConfiguration(f);
		copyDefaults();
		loadMessages();

	}

	private void loadMessages() {
		String reset = ChatColor.translateAlternateColorCodes('&',
				cfg.getString("Color"));
		for (String s : cfg.getKeys(true)) {
			String temp = ChatColor.translateAlternateColorCodes('&',
					cfg.getString(s));
			try {
				temp = temp.replaceAll("&z", reset);
			} catch (Exception e) {

			}

			this.messages.put(s.toLowerCase(), temp);
		}
	}

	private void copyDefaults() {
		cfg.addDefault("Prefix", "&8[&f&lBatWars&8] &z");
		cfg.addDefault("Color", "&6");

		cfg.addDefault("CMD.HelpHeader", "Hilfe für den Command &e%FOR&z:");
		cfg.addDefault("CMD.Help", "&c/%CMD &8- &z%DESCRIPTION");

		// cfg.addDefault("", "");
		cfg.addDefault("bwm.gamestart",
				"Der Kampf beginnt!\n\n&zVerteidige deine Bat und töte die des Gegners!\n\n§6Der Trailer: §ehttps://youtu.be/dBUgOtVXSeo\n§6Mlede Bugs: §a§lhttp://tribemc.de/board/11/");
		cfg.addDefault("bwm.End.Winner",
				"&aDu hast diese Runde BatWars gewonnen!");
		cfg.addDefault("bwm.restart.kick",
				"§cDer Server startet neu!\n\nIn wenigen Sekunden startet eine neue Runde!");
		cfg.addDefault("bwm.join.spectator",
				"Du bist ein Zuschauer!\n\n&cJegliche Eingriffe ins Spiel werden bestart!");
		cfg.addDefault("bwm.sek.blitz",
				"&cDeine Bat wurde von einem &f&lBlitz &cgetroffen!");
		cfg.addDefault("bwm.out.quit",
				"Der Spieler %0 &zhat das Spiel verlassen!");
		cfg.addDefault("bwm.out.out",
				"Der Spieler %0 &zist aus dem Spiel ausgeschieden!");
		cfg.addDefault("bwm.out.team", "&cDas Team %0 &cist ausgeschieden!");
		cfg.addDefault("game.break.urtc",
				"§cEine Teamchest von dir wurde zerstört!");
		cfg.addDefault("game.break.atc",
				"§aDu hast eine Teamchest von %0 &aabgebaut!");
		cfg.addDefault("game.bat.own",
				"§cDu kannst deine eigene Bat nicht angreifen!");
		cfg.addDefault("game.bat.blind",
				"§cDu kannst die Bat nicht angreifen, wenn du Blind bist!");
		cfg.addDefault("game.bat.death", "§c§lDeine Bat wurde getötet!");
		cfg.addDefault("game.bat.bcdeath",
				"Die Bat von %0 &zwurde von %1 &zgetötet!");
		cfg.addDefault("game.bat.hit", "§cDeine Bat wurde geschlagen!");
		cfg.addDefault("game.bat.hitcomplete",
				"&aDu hast der Bat von Team %0 &e&l%1 Schaden &agemacht!");
		cfg.addDefault("game.player.respawn", "%0 &chat noch Respawn-Schutz!");
		cfg.addDefault("game.move.outside", "&cHier geht es nicht lang!");
		cfg.addDefault("game.death.self", "%0 &zist gestorben!");
		cfg.addDefault("game.death.other", "%0 &zwurde von %1 &zgetötet!");
		cfg.addDefault("game.death.killed", "&aDu hast %0 &agetötet!");
		cfg.addDefault("game.interact.noBat", "&cDu hast keine Bat!");
		cfg.addDefault("game.interact.explosive",
				"&aDeine Bat wird nun &e&l%0 mal &aexplodieren!");
		cfg.addDefault("game.interact.notUr", "&cDies ist nicht deine Bat!");
		cfg.addDefault("game.interact.notUrTc",
				"&cDiese Teamchest ist nicht von deinem Team!");
		cfg.addDefault("game.chat.needingame",
				"&cDu musst Ingame sein, um globale Nachrichten schicken zu können!");
		cfg.addDefault("trade.noitem", "&cDies ist kein gültiges Shop-Item!");
		cfg.addDefault("trade.noEnoughNeeded",
				"&cFür diese Aktion benötigst du &e&l%1 x %0&c!");
		cfg.addDefault("aftershow.time",
				"Der Server startet in &a%0 Sekunden &zneu!");
		cfg.addDefault("bwm.end.TeamWin", "Das Team %0 &zhat gewonnen!");
		cfg.addDefault("port.complete", "Du wurdest Teleportiert!");
		cfg.addDefault("game.move.portcancel",
				"Der Teleport wurde abgebrochen!");
		cfg.addDefault("port.instant", "Du bist über einen Teleport eines gegnerischen Teams gelaufen!");
		cfg.addDefault("port.start", "Der Teleportvorgang wurde gestartet!");
		cfg.options().copyDefaults(true);
		save();
	}

	private void save() {
		try {
			cfg.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getMSG(String what) {
		if (!this.messages.containsKey(what.toLowerCase())) {
			return what;
		}
		return this.messages.get(what.toLowerCase());
	}

	public String getPrefix() {
		return this.messages.get("prefix");
	}

	public int messageAmount() {
		return this.messages.size();
	}

	public String getMessage(String what) {
		return getPrefix() + getMSG(what);
	}

}
