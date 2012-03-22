package sneer.bricks.pulp.distribution.filtering;

import basis.brickness.Brick;
import basis.lang.Predicate;
import sneer.bricks.expression.tuples.Tuple;

@Brick
public interface TupleFilterManager {

	void block(Class<? extends Tuple> tupleType);
	
	/** @param censor evaluates to true if the given tuple can be published to contacts, false otherwise. */
	<T extends Tuple> void setCensor(Class<T> tupleType, Predicate<? super T> censor);

	boolean canBePublished(Tuple tuple);

}
