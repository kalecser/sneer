package spikes.sneer.bricks.snapps.watchme;

import java.awt.image.BufferedImage;

import basis.brickness.Brick;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.notifiers.Source;

@Brick
public interface WatchMe {

	void startShowingMyScreen();
	void stopShowingMyScreen();
	
	Source<BufferedImage> screenStreamFor(Seal publicKey);
	
}
