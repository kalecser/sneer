package sneer.bricks.expression.tuples.dispatcher;

import basis.brickness.Brick;
import basis.environments.Environment;
import basis.lang.Consumer;
import sneer.bricks.expression.tuples.Tuple;


@Brick
public interface TupleDispatcher {

	void dispatchCounterIncrement();
	void dispatchCounterDecrement();

	void waitForAllDispatchingToFinish();

	void dispatch(Tuple tuple, Consumer<? super Tuple> subscriber, Environment environment);

}
