package sneer.snapps.games.drones.units;

public interface Unit extends Attributable {

	enum Direction { RIGHT, LEFT }

	boolean collidesWith(Unit other);

	void move();

	int x();

	int size();

	void attack(Unit otherUnit);

	void receiveHit(int strength);
	
	float hitpoints();

	boolean isAlive();
}
