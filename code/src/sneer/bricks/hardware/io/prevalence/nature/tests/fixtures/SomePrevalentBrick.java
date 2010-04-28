package sneer.bricks.hardware.io.prevalence.nature.tests.fixtures;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.hardware.io.prevalence.nature.Transaction;
import sneer.bricks.pulp.reactive.Register;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Brick(Prevalent.class)
public interface SomePrevalentBrick {

	void set(String string);
	String get();
	
	void addItem(String name);
	@Transaction
	Item addItemAndReturnIt(String name);
	void removeItem(Item item);
	int itemCount();
	Item getItem(String name);
	
	@Transaction
	Item addItem_AnnotatedAsTransaction(String name);
	
	Consumer<String> itemAdder_Idempotent();
	
	Register<String> itemAdder_Idempotent_Transitive();
}
