package sneer.bricks.expression.tuples.impl;

import static basis.environments.Environments.my;

import java.util.LinkedList;
import java.util.List;

import basis.environments.Environment;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.Predicate;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.dispatcher.TupleDispatcher;
import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.Threads;

class Subscription<T extends Tuple> {

	private static final Threads Threads = my(Threads.class);
	private static final TupleDispatcher TupleDispatcher = my(TupleDispatcher.class);
	
	
	private final Consumer<? super Tuple> _subscriber;
	private final Class<? extends Tuple> _tupleType;
	private final Predicate<? super T> _filter;
	private final Environment _environment;

	private final List<Tuple> _tuplesToNotify = new LinkedList<Tuple>(); 

	private final Contract _stepperContract;
	private boolean _isDisposed = false;
	
	
	Subscription(Consumer<? super T> subscriber, Class<T> tupleType, Predicate<? super T> filter) {
		_subscriber = (Consumer<? super Tuple>)subscriber;
		_tupleType = tupleType;
		_filter = filter;
		_environment = my(Environment.class);
		
		_stepperContract = Threads.startStepping("Tuple Subscription: " + tupleType, notifier());
	}

	
	private Closure notifier() {
		return new Closure() { @Override public void run() {
			Tuple nextTuple = waitToPopTuple();
			if (_isDisposed) return;
			notifySubscriber(nextTuple);
			TupleDispatcher.dispatchCounterDecrement();
		}};
	}
	
	
	private void notifySubscriber(Tuple tuple) {
		Consumer<? super Tuple> subscriber = _subscriber;
		if (subscriber == null) return;
		
		TupleDispatcher.dispatch(tuple, subscriber, _environment);
	}

	
	void filterAndPushToNotify(final Tuple tuple) {
		if (!isRelevant(tuple)) return;
		
		TupleDispatcher.dispatchCounterIncrement();
		pushTuple(tuple);
	}


	void filterAndNotify(final Tuple tuple) {
		if (!isRelevant(tuple)) return;
		
		notifySubscriber(tuple);
	}
	
	
	private boolean isRelevant(final Tuple tuple) {
		if (!_tupleType.isInstance(tuple)) return false;
		if (!_filter.evaluate((T)tuple)) return false;
		return true;
	}
	
	
	private Tuple waitToPopTuple() {
		synchronized (_tuplesToNotify) {
			while (_tuplesToNotify.isEmpty()) { //This used to be an "if" instead of a "while" and we did get a rare IndexOutOfBoundsException when doing remove(0) below. I don't know how this can happen since only one thread removes elements and it is only notified when an element is added. Can someone explain that? Klaus
				my(Threads.class).waitWithoutInterruptions(_tuplesToNotify);
				if (_isDisposed) return null;
			}
			
			return _tuplesToNotify.remove(0);
		}
	}

	
	private void pushTuple(Tuple tuple) {
		synchronized (_tuplesToNotify) {
			_tuplesToNotify.add(tuple);
			_tuplesToNotify.notify();
		}
	}

	
	/** Removes this subscription as soon as possible. The subscription might still receive tuple notifications from other threads AFTER this method returns, though. It is impossible to guarantee synchronicity of this method without risking deadlocks, especially with the GUI thread. If you really need to know when the subscription was removed, get in touch with us. We can change the API to provide for a callback.*/
	void dispose() {
		_stepperContract.dispose();
		synchronized(_tuplesToNotify) {
			_isDisposed = true;
			decrementPendingTuplesFromDispatchCounter();
			_tuplesToNotify.notify();
		}
	}

	
	private void decrementPendingTuplesFromDispatchCounter() {
		for (int i = 0; i < _tuplesToNotify.size(); i++)
			TupleDispatcher.dispatchCounterDecrement();
	}

}