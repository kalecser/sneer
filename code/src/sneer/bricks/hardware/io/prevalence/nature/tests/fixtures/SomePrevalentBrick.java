package sneer.bricks.hardware.io.prevalence.nature.tests.fixtures;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.foundation.brickness.Brick;

@Brick(Prevalent.class)
public interface SomePrevalentBrick {

	void set(String string);
	String get();

}
