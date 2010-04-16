package sneer.bricks.pulp.probe.tests;

import sneer.bricks.expression.tuples.Tuple;

public abstract class TupleWithId extends Tuple {

	public final int id;

	public TupleWithId(int id_) {
		id = id_;
	}

}