package sneer.snapps.games.drones.units.impl;

import sneer.snapps.games.drones.units.Unit;

class UnitImpl implements Unit {
	
	static final int SIZE = 100;

	private int x;
	private Direction direction;
	
	public UnitImpl(int i, Direction direction) {
		x = i;
		this.direction = direction;
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

}
