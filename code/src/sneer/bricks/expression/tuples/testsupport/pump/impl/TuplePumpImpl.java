package sneer.bricks.expression.tuples.testsupport.pump.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.testsupport.pump.TuplePump;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Producer;

class TuplePumpImpl implements TuplePump {

	private final Environment _well1;
	private final Environment _well2;

	private final WeakContract _pipe1;
	private final WeakContract _pipe2;


	public TuplePumpImpl(Environment aTupleWell, Environment anotherTupleWell) {
		_well1 = aTupleWell;
		_well2 = anotherTupleWell;

		_pipe1 = pipe(_well1, _well2); // Connects well1 to well2 so that tuples received by the first are pumped to the second
		_pipe2 = pipe(_well2, _well1); // Connects well2 to well1 to form a two-way connection
	}


	private WeakContract pipe(Environment from, final Environment to) {
		return EnvironmentUtils.produceIn(from, new Producer<WeakContract>() { @Override public WeakContract produce() {
			return my(TupleSpace.class).addSubscription(Tuple.class, new Consumer<Tuple>() { @Override public void consume(final Tuple tuple) {
				Environments.runWith(to, new Closure() { @Override public void run() {
					my(TupleSpace.class).acquire(tuple);
				}});
			}});
		}});
	}


	@Override
	public void waitForAllDispatchingToFinish() {
		waitForAllDispatchingToFinishIn(_well1);
		waitForAllDispatchingToFinishIn(_well2);
	}


	private void waitForAllDispatchingToFinishIn(Environment env) {
		Environments.runWith(env, new Closure() { @Override public void run() {
			my(TupleSpace.class).waitForAllDispatchingToFinish();
		}});
	}


	@Override
	public void dispose() {
		_pipe1.dispose();
		_pipe2.dispose();
	}

}
