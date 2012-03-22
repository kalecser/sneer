package sneer.bricks.expression.tuples;

import basis.brickness.Brick;
import basis.lang.Consumer;
import basis.lang.Functor;
import basis.lang.Predicate;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;

@Brick
public interface TupleSpace {

	void add(Tuple tuple);

	<T extends Tuple> WeakContract addSubscription(Class<T> tupleType, Consumer<? super T> subscriber);
	<T extends Tuple> WeakContract addSubscription(Class<T> tupleType, Consumer<? super T> subscriber, Predicate<? super T> filter);

	void keep(Class<? extends Tuple> tupleType);
	<T extends Tuple> void keepChosen(Class<T> tupleType, Predicate<? super T> filter);
	<T extends Tuple> void keepNewest(Class<T> tupleType, Functor<? super T, Object> grouping);
	
}
