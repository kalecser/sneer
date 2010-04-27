package sneer.bricks.expression.tuples.testsupport.pump.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.testsupport.pump.TuplePump;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;

class TuplePumpImpl implements TuplePump {
	
	private final Environment _env1;
	private final Environment _env2;

	private final WeakContract _toAvoidGC1;
	private final WeakContract _toAvoidGC2;

	
	public TuplePumpImpl(Environment env1, Environment env2) {
		_env1 = env1;
		_env2 = env2;

		_toAvoidGC1 = pumpFor(env1, env2);
		_toAvoidGC2 = pumpFor(env2, env1);
	}

	
	private WeakContract pumpFor(Environment env1, final Environment env2) {
		final ByRef<WeakContract> result = ByRef.newInstance();

		Environments.runWith(env1, new Closure() { @Override public void run() {
			result.value = my(TupleSpace.class).addSubscription(Tuple.class, new Consumer<Tuple>() { @Override public void consume(final Tuple tuple) {
				Environments.runWith(env2, new Closure() { @Override public void run() {
					my(TupleSpace.class).acquire(tuple);
				}});
			}});
		}});

		return result.value;
	}

	
	@Override
	public void waitForAllDispatchingToFinish() {
		Environments.runWith(_env1, new Closure() { @Override public void run() {
			my(TupleSpace.class).waitForAllDispatchingToFinish();
		}});
		
		Environments.runWith(_env2, new Closure() { @Override public void run() {
			my(TupleSpace.class).waitForAllDispatchingToFinish();
		}});
	}

	
	@Override
	public void dispose() {
		_toAvoidGC1.dispose();
		_toAvoidGC2.dispose();
	}

}
