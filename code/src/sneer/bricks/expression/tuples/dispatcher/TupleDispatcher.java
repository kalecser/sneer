package sneer.bricks.expression.tuples.dispatcher;

import sneer.bricks.expression.tuples.Tuple;
import sneer.foundation.brickness.Brick;
import sneer.foundation.environments.Environment;
import sneer.foundation.lang.Consumer;


@Brick
public interface TupleDispatcher {

	void dispatchCounterDecrement();

	void dispatchCounterIncrement();

	void waitForAllDispatchingToFinish();

	void dispatch(Tuple tuple, Consumer<? super Tuple> subscriber, Environment environment);

}
