package sneer.bricks.expression.tuples.remote.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Predicate;

class RemoteTuplesImpl implements RemoteTuples {

	private TupleSpace _delegate = my(TupleSpace.class); 

	@Override
	public <T extends Tuple> WeakContract addSubscription(Class<T> tupleType, Consumer<? super T> subscriber) {
		return _delegate.addSubscription(tupleType, subscriber, new Predicate<Tuple>() { @Override public boolean evaluate(Tuple tuple) {
			return !my(OwnSeal.class).get().currentValue().equals(tuple.publisher);
		}});
	}

}
