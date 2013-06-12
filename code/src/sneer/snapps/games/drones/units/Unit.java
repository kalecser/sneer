package sneer.snapps.games.drones.units;

public interface Unit extends Attributable {

	enum Direction { RIGHT, LEFT }

	boolean collidesWith(Unit other);

	void move();

	int x();

	int size();
	
}
