package sneer.bricks.expression.tuples;

import java.util.List;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Predicate;

@Brick
public interface TupleSpace {

	void add(Tuple tuple);

	<T extends Tuple> WeakContract addSubscription(Class<T> tupleType, Consumer<? super T> subscriber);
	<T extends Tuple> WeakContract addSubscription(Class<T> tupleType, Consumer<? super T> subscriber, Predicate<? super T> filter);

	void keep(Class<? extends Tuple> tupleType);
	List<Tuple> keptTuples();

	void waitForAllDispatchingToFinish();
	
}
