package sneer.bricks.expression.tuples.kept;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Brick(Prevalent.class)
public interface KeptTuples {
	
	Consumer<Tuple> adder();
	ListSignal<Tuple> output();
	
}
