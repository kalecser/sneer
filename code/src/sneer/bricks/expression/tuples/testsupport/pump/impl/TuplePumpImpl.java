package sneer.bricks.expression.tuples.testsupport.pump.impl;

import static basis.environments.Environments.my;
import static basis.environments.Environments.runWith;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.dispatcher.TupleDispatcher;
import sneer.bricks.expression.tuples.testsupport.pump.TuplePump;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import basis.environments.Environment;
import basis.environments.EnvironmentUtils;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.Producer;

class TuplePumpImpl implements TuplePump {

	private final Environment _well1;
	private final Environment _well2;

	private final WeakContract pumpingContract1;
	private final WeakContract pumpingContract2;

	private final Set<Tuple> _tuplesThatWillEcho = Collections.synchronizedSet(new HashSet<Tuple>());

	TuplePumpImpl(Environment aTupleWell, Environment anotherTupleWell) {
		_well1 = aTupleWell;
		_well2 = anotherTupleWell;

		pumpingContract1 = startPumping(_well1, _well2);
		pumpingContract2 = startPumping(_well2, _well1);
	}


	private WeakContract startPumping(Environment from, final Environment to) {
		return EnvironmentUtils.produceIn(from, new Producer<WeakContract>() { @Override public WeakContract produce() {
			return my(TupleSpace.class).addSubscription(Tuple.class, new Consumer<Tuple>() { @Override public void consume(final Tuple tuple) {
				if (_tuplesThatWillEcho.remove(tuple)) return; //Tuple that was sent and is returning.
				_tuplesThatWillEcho.add(tuple);
				runWith(to, new Closure() { @Override public void run() {
					my(TupleSpace.class).add(tuple);
				}});
			}});
		}});
	}


	@Override
	public void waitForAllDispatchingToFinish() {
		boolean hasDispatchingToDo;
		do {
			hasDispatchingToDo =
					waitForAllDispatchingToFinishIn(_well1) || waitForAllDispatchingToFinishIn(_well2);
		} while (hasDispatchingToDo);
	}


	private boolean waitForAllDispatchingToFinishIn(Environment env) {
		return EnvironmentUtils.produceIn(env, new Producer<Boolean>() { @Override public Boolean produce() {
			return my(TupleDispatcher.class).waitForAllDispatchingToFinish();
		}});
	}


	@Override
	public void dispose() {
		pumpingContract1.dispose();
		pumpingContract2.dispose();
	}

}
