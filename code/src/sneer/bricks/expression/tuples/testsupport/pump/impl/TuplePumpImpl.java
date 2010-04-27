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

	private final Environment _tupleWell1;
	private final Environment _tupleWell2;

	private final WeakContract _pipe1;
	private final WeakContract _pipe2;


	public TuplePumpImpl(Environment well1, Environment well2) {
		_tupleWell1 = well1;
		_tupleWell2 = well2;

		_pipe1 = pipe(well1, well2);
		_pipe2 = pipe(well2, well1);
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
		waitForAllDispatchingToFinishIn(_tupleWell1);
		waitForAllDispatchingToFinishIn(_tupleWell2);
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
