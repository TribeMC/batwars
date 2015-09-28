package de.tribemc.batwars.objects;

public class BatLobbyData {

	private String mapWinner;

	public BatLobbyData(String map) {
		this.mapWinner = map;
	}

	public String getMap() {
		return this.mapWinner;
	}
}
