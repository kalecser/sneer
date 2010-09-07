package sneer.bricks.hardware.ram.reflection.visitation.impl;

import sneer.bricks.hardware.ram.reflection.visitation.Tour;

public class TourImpl implements Tour {

	private final Tour _tillHere;
	private final String _direction;
	private final Object _attraction;

	TourImpl(Object start) {
		this(null, "", start);
	}

	private TourImpl(Tour tillHere, String direction, Object attraction) {
		_tillHere = tillHere;
		_direction = direction;
		_attraction = attraction;
	}

	TourImpl fork(String direction, Object attraction) {
		return new TourImpl(this, direction, attraction);
	}

	@Override
	public String toString() {
		String prefix = _tillHere == null ? "" : _tillHere.toString();
		return prefix + "." + _direction + " > "	+ _attraction.getClass().getSimpleName();
	}

	@Override
	public Tour tillHere() {
		return _tillHere;
	}

	@Override
	public String direction() {
		return _direction;
	}

	@Override
	public Object target() {
		return _attraction;
	}

}