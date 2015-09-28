package de.tribemc.batwars.objects;

public class BatData {

	private BatTeam team;
	private boolean isSpectator;
	private long lastDeath;

	private int kills, hits, tode;

	public BatData(BatTeam team, boolean spec) {
		this.team = team;
		this.isSpectator = spec;

	}

	public boolean isSpectator() {
		return this.isSpectator;
	}

	public BatTeam getTeam() {
		return this.team;
	}

	public int getKills() {
		return this.kills;
	}

	public int getTode() {
		return this.tode;
	}

	public int getHits() {
		return this.hits;
	}

	public void setLastDeath(long l) {
		this.lastDeath = l;
	}

	public long getLastDeath() {
		return this.lastDeath;
	}

	public boolean hasSpawnProtection() {
		return this.lastDeath + 5000 > System.currentTimeMillis();
	}

	public void addKill() {
		this.kills++;
	}

	public void addTod() {
		this.tode++;
	}

	public void addHit() {
		this.hits++;
	}

}
