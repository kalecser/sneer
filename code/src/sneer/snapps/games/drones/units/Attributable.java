package sneer.snapps.games.drones.units;

public interface Attributable {

	UnitAttribute[] attributes();

	void define(UnitAttribute attribute, int value);

	int getAttribute(UnitAttribute attribute);
}
