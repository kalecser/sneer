package sneer.bricks.hardware.io.prevalence.nature.tests.fixtures;

import basis.brickness.Brick;
import basis.lang.Closure;
import basis.lang.Consumer;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.hardware.io.prevalence.nature.Transaction;
import sneer.bricks.pulp.reactive.Register;

@Brick(Prevalent.class)
public interface SomePrevalentBrick {

	void set(String string);
	String get();
	
	void addItem(String name);
	void removeItem(Item item);
	int itemCount();
	Item getItem(String name);
	
	@Transaction
	Item addItemAndReturnIt_AnnotatedAsTransaction(String name);
	
	Consumer<String> itemAdder_Idempotent();
	
	Register<String> itemAdder_Idempotent_Transitive();

	Closure removerFor(Item item);
}
