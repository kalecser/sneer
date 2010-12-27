package sneer.bricks.expression.tuples.kept;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Brick(Prevalent.class)
public interface KeptTuples {
	
	void add(Tuple tuple);
	
	boolean contains(Tuple tuple);
	Tuple[] all();

	@Deprecated //Use add(Tuple) instead. Deprecated in Dec 2010. It is safe to delete this method after everyone has taken snapshots of their kept tuples.
	Consumer<Tuple> adder();
	
}
