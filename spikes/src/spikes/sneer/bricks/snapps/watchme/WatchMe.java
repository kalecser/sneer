package spikes.sneer.bricks.snapps.watchme;

import java.awt.image.BufferedImage;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.notifiers.Source;
import sneer.foundation.brickness.Brick;

@Brick
public interface WatchMe {

	void startShowingMyScreen();
	void stopShowingMyScreen();
	
	Source<BufferedImage> screenStreamFor(Seal publicKey);
	
}
