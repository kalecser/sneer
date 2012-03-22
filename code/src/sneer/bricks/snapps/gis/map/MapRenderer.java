package sneer.bricks.snapps.gis.map;

import java.awt.Image;

import basis.brickness.Brick;
import basis.lang.Consumer;

import sneer.bricks.snapps.gis.location.Location;

@Brick
public interface MapRenderer {

	void render(Consumer<Image> receiver, Location location);
	void render(Consumer<Image> receiver, Location location, int zoom);
}
