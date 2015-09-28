package de.tribemc.batwars.objects;

public enum BatSpawnerType {
	BRONZE(1), EISEN(8), GOLD(70);

	private int i;

	BatSpawnerType(int i) {
		this.i = i;
	}

	public int getDelay() {
		return this.i;
	}
}
