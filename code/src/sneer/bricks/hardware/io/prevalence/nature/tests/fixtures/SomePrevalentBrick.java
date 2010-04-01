package sneer.bricks.hardware.io.prevalence.nature.tests.fixtures;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.foundation.brickness.Brick;

@Brick(Prevalent.class)
public interface SomePrevalentBrick {

	void set(String string);
	String get();
<<<<<<< Updated upstream:code/src/sneer/bricks/hardware/io/prevalence/nature/tests/fixtures/SomePrevalentBrick.java
=======
	
	void addItem(String name);
	void removeItem(Item item);
	int itemCount();
	Item getItem(String name);
>>>>>>> Stashed changes:code/src/sneer/bricks/hardware/io/prevalence/nature/tests/fixtures/SomePrevalentBrick.java

}
