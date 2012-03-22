package sneer.bricks.expression.tuples.kept;

import basis.brickness.Brick;
import basis.lang.Consumer;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;

@Brick(Prevalent.class)
public interface KeptTuples {
	
	void add(Tuple tuple);
	void remove(Tuple tuple);
	
	boolean contains(Tuple tuple);
	Tuple[] all();

	@Deprecated //Use add(Tuple) instead. Deprecated in Dec 2010. It is safe to delete this method after everyone has taken snapshots of their kept tuples.
	Consumer<Tuple> adder();
	
}
