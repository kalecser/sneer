package sneer.bricks.expression.tuples.keeper;

import sneer.bricks.expression.tuples.Tuple;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.Predicate;


@Brick
public interface TupleKeeper {

	void keepType(Class<? extends Tuple> tupleType);
	<T extends Tuple> void keepChosen(Class<T> tupleType, Predicate<? super T> filter);
	<T extends Tuple> void keepNewest(Class<T> tupleType, Functor<? super T, Object> grouping);
	
	void keepIfNecessary(Tuple tuple);
	boolean isAlreadyKept(Tuple tuple);
	Tuple[] keptTuples();

	void garbageCollect();

}
