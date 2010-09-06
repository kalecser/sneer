package sneer.bricks.hardware.ram.reflection.visitation;

public class Tour {

	public final Tour _tillHere;
	public final String _direction;
	public final Object _attraction;

	Tour(Object start) {
		this(null, "", start);
	}

	private Tour(Tour tillHere, String direction, Object attraction) {
		_tillHere = tillHere;
		_direction = direction;
		_attraction = attraction;
	}

	Tour fork(String direction, Object attraction) {
		return new Tour(this, direction, attraction);
	}

	@Override
	public String toString() {
		String prefix = _tillHere == null ? "" : _tillHere.toString();
		return prefix + "." + _direction + " > "	+ _attraction.getClass().getSimpleName();
	}

}