package de.tribemc.batwars.objects;

import net.minecraft.server.v1_7_R4.EntityBat;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import net.minecraft.server.v1_7_R4.World;

import org.bukkit.entity.Bat;

public class CustomBat extends EntityBat {

	public CustomBat(World arg0) {
		super(arg0);
		this.a(0.5F, 0.9F);
		this.setAsleep(false);
		this.getAttributeInstance(GenericAttributes.maxHealth).setValue(200);
		this.setHealth(200);
		this.fireProof = true;
	}

	@Override
	public void g(double d0, double d1, double d2) {
	}

	public Bat getBat() {
		return (Bat) this.getBukkitEntity();
	}

}
