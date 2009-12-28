package spikes.sneer.bricks.snapps.watchme;

import java.awt.image.BufferedImage;

import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.foundation.brickness.Brick;

@Brick
public interface WatchMe {

	void startShowingMyScreen();
	void stopShowingMyScreen();
	
	EventSource<BufferedImage> screenStreamFor(Seal publicKey);
	
}
