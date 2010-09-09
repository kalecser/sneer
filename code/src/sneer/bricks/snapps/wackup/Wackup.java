package sneer.bricks.snapps.wackup;

import java.io.File;

import sneer.bricks.pulp.events.pulsers.PulseSource;
import sneer.foundation.brickness.Brick;

@Brick
public interface Wackup {

	File folder();

	PulseSource newFileArrived();

}
