package sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.brick2;

import basis.brickness.Brick;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;

@Brick(Prevalent.class)
public interface PrevalentBrick2 {

	void rememberItemCount();

	int recallItemCount();
	
	void addItemToSomePrevalentBrick(String name);

}
