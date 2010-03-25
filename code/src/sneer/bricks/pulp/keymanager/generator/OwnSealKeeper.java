package sneer.bricks.pulp.keymanager.generator;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.foundation.brickness.Brick;

@Brick(Prevalent.class)
public interface OwnSealKeeper {
	boolean needsToProduceSeal();
	void produceOwnSeal(byte[] randomness);

	Seal seal();
}
