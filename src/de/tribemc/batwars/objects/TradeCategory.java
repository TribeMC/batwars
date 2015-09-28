package de.tribemc.batwars.objects;

public enum TradeCategory {
	BLOECKE(0), RUESTUNG(1), WAFFEN(2), WERKZEUG(3), BOGEN(4), TRAENKE(5), CHEST(
			6), BRUECKE(7), SPEZIAL(8);

	private int i;

	private TradeCategory(int i) {
		this.i = i;
	}

	public int getID() {
		return this.i;
	}

	public static TradeCategory valueOf(int i) {
		for (TradeCategory tc : TradeCategory.values())
			if (tc.getID() == i)
				return tc;
		return null;
	}
}
