package sneer.bricks.expression.tuples.kept;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import basis.brickness.Brick;

@Brick(Prevalent.class)
public interface KeptTuples {
	
	void add(Tuple tuple);
	void remove(Tuple tuple);
	
	boolean contains(Tuple tuple);
	Tuple[] all();

}
