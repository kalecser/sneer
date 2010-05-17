package sneer.bricks.expression.tuples.remote;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Brick
public interface RemoteTuples {

	/** Delegates to TupleSpace.addSubscribition() using a predicate to ignore tuples published by myself */
	<T extends Tuple> WeakContract addSubscription(Class<T> tupleType, Consumer<? super T> subscriber);

}
