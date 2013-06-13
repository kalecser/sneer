package sneer.snapps.games.drones.units.impl;

import static sneer.snapps.games.drones.units.UnitAttribute.ARMOR;
import static sneer.snapps.games.drones.units.UnitAttribute.HITPOINTS;
import static sneer.snapps.games.drones.units.UnitAttribute.STRENGTH;
import sneer.snapps.games.drones.units.Unit;
import sneer.snapps.games.drones.units.UnitAttribute;

class UnitImpl implements Unit {

	static final int SIZE = 100;

	private int x;
	private Direction direction;
	private String name;

	private float hitpoints;
	private int strength;
	private int armor;

	public UnitImpl(int i, Direction direction, String name) {
		x = i;
		this.direction = direction;
		this.name = name;
	}

	@Override
	public int x() {
		return x;
	}

	@Override
	public void move() {
		x += direction() * 10;
	}

	private int direction() {
		return direction == Direction.RIGHT ? 1 : -1;

	}

	@Override
	public boolean collidesWith(Unit other) {
		return x + size() > other.x() && x <= other.x();
	}

	@Override
	public int size() {
		return SIZE;
	}

	@Override
	public UnitAttribute[] attributes() {
		return new UnitAttribute[]{HITPOINTS, STRENGTH, ARMOR};
	}

	@Override
	public void set(UnitAttribute attribute, int value) {
		if (attribute == HITPOINTS) hitpoints = value;
		if (attribute == STRENGTH)  strength = value;
		if (attribute == ARMOR)     armor = value;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void attack(Unit otherUnit) {
		otherUnit.receiveHit(strength);
	}

	@Override
	public void receiveHit(int strength) {
		float damageReduction = strength * (armor / 1000.0f);
		float damage = strength - damageReduction;
		hitpoints -= damage;
	}

	@Override
	public float hitpoints() {
		return hitpoints;
	}
}
