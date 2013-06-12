package sneer.snapps.games.drones.units.impl;

import static sneer.snapps.games.drones.units.UnitAttribute.ARMOR;
import static sneer.snapps.games.drones.units.UnitAttribute.LIFE;
import static sneer.snapps.games.drones.units.UnitAttribute.STRENGTH;

import java.util.HashMap;
import java.util.Map;

import sneer.snapps.games.drones.units.Unit;
import sneer.snapps.games.drones.units.UnitAttribute;

class UnitImpl implements Unit {

	static final int SIZE = 100;

	private int x;
	private Direction direction;
	private String name;
	private Map<UnitAttribute, Integer> attributes;

	public UnitImpl(int i, Direction direction, String name) {
		x = i;
		this.direction = direction;
		this.name = name;

		attributes = new HashMap<UnitAttribute, Integer>();
		attributes.put(LIFE, 0);
		attributes.put(STRENGTH, 0);
		attributes.put(ARMOR, 0);
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
		return attributes.keySet().toArray(new UnitAttribute[attributes.size()]);
	}

	@Override
	public void define(UnitAttribute attribute, int value) {
		attributes.put(attribute, value);
	}

	@Override
	public int getAttribute(UnitAttribute attribute) {
		return attributes.get(attribute);
	}

	@Override
	public String toString() {
		return name;
	}
}
