package sneer.bricks.hardware.io.prevalence.nature.tests.fixtures;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.hardware.io.prevalence.nature.Transaction;
import sneer.foundation.brickness.Brick;

@Brick(Prevalent.class)
public interface SomePrevalentBrick {

	void set(String string);
	String get();
	
	void addItem(String name);
	void removeItem(Item item);
	int itemCount();
	Item getItem(String name);
	
	@Transaction
	Item addItem_AnnotatedAsTransaction(String name);
}
