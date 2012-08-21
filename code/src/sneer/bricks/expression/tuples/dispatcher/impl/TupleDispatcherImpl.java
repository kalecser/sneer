package sneer.bricks.expression.tuples.dispatcher.impl;
import static basis.environments.Environments.my;

import java.util.HashSet;
import java.util.Set;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.dispatcher.TupleDispatcher;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.exceptionhandling.ExceptionHandler;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;
import basis.lang.Consumer;

class TupleDispatcherImpl implements TupleDispatcher {

	private static final Threads Threads = my(Threads.class);
	private static final ExceptionHandler ExceptionHandler = my(ExceptionHandler.class);
	
	private final Object _dispatchCounterMonitor = new Object();
	private int _dispatchCounter = 0;
	private final Set<Thread> _dispatchingThreads = new HashSet<Thread>();


	@Override
	public void dispatchCounterIncrement() {
		synchronized (_dispatchCounterMonitor ) {
			_dispatchCounter++;
		}
	}
	
	
	@Override
	public void dispatchCounterDecrement() {
		synchronized (_dispatchCounterMonitor ) {
			_dispatchCounter--;
			if (_dispatchCounter == 0)
				_dispatchCounterMonitor.notifyAll();
		}
	}

	
	@Override	
	public boolean waitForAllDispatchingToFinish() {
		if (_dispatchingThreads.contains(Thread.currentThread())) throw new IllegalStateException("Dispatching thread cannot wait for dispatching to finish.");
		
		synchronized (_dispatchCounterMonitor ) {
			if (_dispatchCounter == 0) return false;
			while (_dispatchCounter != 0)
				Threads.waitWithoutInterruptions(_dispatchCounterMonitor);
		}
		return true;
	}


	@Override
	public void dispatch(Tuple tuple, Consumer<? super Tuple> subscriber, Environment environment) {
		_dispatchingThreads.add(Thread.currentThread());
		doDispatch(tuple, subscriber, environment);
		_dispatchingThreads.remove(Thread.currentThread());
	}


	private void doDispatch(final Tuple tuple, final Consumer<? super Tuple> subscriber, final Environment environment) {
		ExceptionHandler.shield(new Closure() { @Override public void run() {
			Environments.runWith(environment, new Closure() { @Override public void run() {
				subscriber.consume(tuple);
			}});
		}});
	}
	
}
