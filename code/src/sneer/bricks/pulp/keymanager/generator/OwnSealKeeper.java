package sneer.bricks.pulp.keymanager.generator;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.foundation.brickness.Brick;

<<<<<<< Updated upstream:code/src/sneer/bricks/pulp/keymanager/generator/OwnSealKeeper.java
@Brick(Prevalent.class)
=======
@Brick //(Prevalent.class)
>>>>>>> Stashed changes:code/src/sneer/bricks/pulp/keymanager/generator/OwnSealKeeper.java
public interface OwnSealKeeper {
	boolean needsToProduceSeal();
	void produceOwnSeal(byte[] randomness);

	Seal seal();
}
