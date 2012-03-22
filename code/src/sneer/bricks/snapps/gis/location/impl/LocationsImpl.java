package sneer.bricks.snapps.gis.location.impl;

import basis.lang.Consumer;
import sneer.bricks.snapps.gis.location.Location;
import sneer.bricks.snapps.gis.location.Locations;

class LocationsImpl implements Locations {

	@Override
	public void find(final String address, Consumer<Location> receiver) {
		new LocationImpl(address, receiver);
	}
}
