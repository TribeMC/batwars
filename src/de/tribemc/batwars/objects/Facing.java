package de.tribemc.batwars.objects;

import de.tribemc.tribeessentials.objects.server.Spieler;

public enum Facing {

	SOUTH(0, 1, true), SOUTH_WEST(-1, 1, false), WEST(-1, 0, true), NORTH_WEST(
			-1, -1, false), NORTH(0, -1, true), NORTH_EAST(1, -1, false), EAST(
			1, 0, true), SOUTH_EAST(1, 1, false);

	int x, z;
	boolean a;

	Facing(int x, int z, boolean a) {
		this.x = x;
		this.z = z;
		this.a = a;
	}

	public int getX() {
		return this.x;
	}

	public int getZ() {
		return this.z;
	}

	public boolean isAxis() {
		return this.a;
	}

	public static Facing exaktFacing(float yaw) {
		return Facing.values()[Math.round(yaw / 45f) & 0x7];
	}

	public static Facing exaktFacing(Spieler sp) {
		return Facing.values()[Math.round(sp.getEyeLocation().getYaw() / 45f) & 0x7];
	}
}
