package sneer.bricks.identity.seals.generator;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.brickness.Brick;

@Brick(Prevalent.class)
public interface OwnSealGenerator {
	
	boolean needsToGenerateOwnSeal();
	void generateOwnSeal(byte[] randomness);

	Seal generatedSeal();
}
