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
	
	private final Environment _env1;
	private final Environment _env2;

	private final WeakContract _toAvoidGC1;
	private final WeakContract _toAvoidGC2;

	
	public TuplePumpImpl(Environment env1, Environment env2) {
		_env1 = env1;
		_env2 = env2;

		_toAvoidGC1 = pump(env1, env2);
		_toAvoidGC2 = pump(env2, env1);
	}

	
	private WeakContract pump(Environment from, final Environment to) {
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
		waitForAllDispatchingToFinishIn(_env1);
		waitForAllDispatchingToFinishIn(_env2);
	}


	private void waitForAllDispatchingToFinishIn(Environment env) {
		Environments.runWith(env, new Closure() { @Override public void run() {
			my(TupleSpace.class).waitForAllDispatchingToFinish();
		}});
	}

	
	@Override
	public void dispose() {
		_toAvoidGC1.dispose();
		_toAvoidGC2.dispose();
	}

}
