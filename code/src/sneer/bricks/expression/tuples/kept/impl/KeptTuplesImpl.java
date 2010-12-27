package sneer.bricks.expression.tuples.kept.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.kept.KeptTuples;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.foundation.lang.Consumer;

public class KeptTuplesImpl implements KeptTuples {

	private ListRegister<Tuple> _delegate = my(CollectionSignals.class).newListRegister();

	
	@Override
	public Consumer<Tuple> adder() {
		return _delegate.adder();
	}


	@Override
	public 	ListSignal<Tuple> output() {
		return _delegate.output();
	}
	
}
