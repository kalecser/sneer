package sneer.bricks.snapps.gis.location;

import basis.brickness.Brick;
import basis.lang.Consumer;

@Brick
public interface Locations {

	void find(String address, Consumer<Location> receiver);
	
}
