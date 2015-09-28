package de.tribemc.batwars.objects.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.server.v1_7_R4.AttributeInstance;
import net.minecraft.server.v1_7_R4.EntityBat;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import net.minecraft.server.v1_7_R4.Navigation;
import net.minecraft.server.v1_7_R4.World;

import org.bukkit.Location;

public class Batty extends EntityBat {

	private Location loc;

	public Batty(World world, Location loc) {
		super(world);
		this.loc = loc;
		try {
			Field b = this.goalSelector.getClass().getDeclaredField("b");
			b.setAccessible(true);
			b.set(this.goalSelector, new ArrayList<>());
			Field field = Navigation.class.getDeclaredField("e");
			field.setAccessible(true);
			AttributeInstance ai = (AttributeInstance) field.get(this
					.getNavigation());
			ai.setValue(128);
			this.getAttributeInstance(GenericAttributes.b).setValue(128D);
			this.getAttributeInstance(GenericAttributes.d).setValue(0.37D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// this.goalSelector.a(0, new PathfinderGoalFollowParent(this, 1.1D));

	}

	public Location getLocation() {
		return new Location(this.world.getWorld(), this.locX, this.locY,
				this.locZ);
	}

	@Override
	public boolean h_() {
		return true;
	}

	@Override
	public void g(double d0, double d1, double d2) {

	}

	@Override
	public void move(double arg0, double arg1, double arg2) {
		if (getLocation().add(arg0, arg1, arg2).toVector().toBlockVector()
				.equals(this.loc.toVector().toBlockVector()))
			super.move(arg0, arg1, arg2);
	}

}
