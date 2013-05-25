package sneer.snapps.games.drones;


public interface Attributable {

	UnitAttribute[] attributes();

	void define(UnitAttribute attribute, int value);

}
