package de.tribemc.batwars.manager;

import java.io.File;

import de.tribemc.batwars.main.BatWarsManager;
import de.tribemc.batwars.objects.BatWarsLogger;

public class LoggerManager {

	private BatWarsLogger log;

	private BatWarsManager bwm;

	public LoggerManager(BatWarsManager bwm) {
		this.bwm = bwm;
		File f = new File("plugins/BatWars/Logger");
		if (!f.exists())
			f.mkdir();
	}

	public void createLogger() {
		File f = null;
		while (f == null || f.exists()) {
			f = new File("plugins/BatWars/Logger/" + getRandom() + ".txt");
		}
		log = new BatWarsLogger(f);
	}

	public BatWarsLogger getLogger() {
		return this.log;
	}

	public BatWarsManager getManager() {
		return this.bwm;
	}

	public String getRandom() {
		return System.currentTimeMillis() + "";
	}
}
