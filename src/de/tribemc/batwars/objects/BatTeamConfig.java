package de.tribemc.batwars.objects;


public class BatTeamConfig {

	private String name;
	private String cfg;
	private int max;
	private short data;

	public BatTeamConfig(String cfg, String name, int max, int i) {
		this.name = name;
		this.max = max;
		this.cfg = cfg;
		this.data = (short) i;
	}

	public String getName() {
		return this.name;
	}

	public int getMax() {
		return this.max;
	}

	public short getItemData() {
		return this.data;
	}

	public String getCFGName() {
		return this.cfg;
	}
}
