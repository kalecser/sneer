package sneer.snapps.games.drones.units;

public interface Attributable {

	UnitAttribute[] attributes();

	void set(UnitAttribute attribute, int value);
}
