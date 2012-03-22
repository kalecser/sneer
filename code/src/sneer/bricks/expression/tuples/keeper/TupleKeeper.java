package sneer.bricks.expression.tuples.keeper;

import basis.brickness.Brick;
import basis.lang.Functor;
import basis.lang.Predicate;
import sneer.bricks.expression.tuples.Tuple;


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
