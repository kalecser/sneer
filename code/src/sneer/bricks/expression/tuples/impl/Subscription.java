package sneer.bricks.expression.tuples.impl;

import static basis.environments.Environments.my;

import java.util.LinkedList;
import java.util.List;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.dispatcher.TupleDispatcher;
import sneer.bricks.hardware.cpu.threads.Threads;
import basis.environments.Environment;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.Predicate;

class Subscription<T extends Tuple> {
	
	private static final TupleDispatcher TupleDispatcher = my(TupleDispatcher.class);
	
	private final Consumer<? super Tuple> _subscriber;
	private final Class<? extends Tuple> _tupleType;
	private final Predicate<? super T> _filter;
	private final Environment _environment;

	private final List<Tuple> _tuplesToNotify = new LinkedList<Tuple>();

	private boolean isDisposed; 

	Subscription(Consumer<? super T> subscriber, Class<T> tupleType, Predicate<? super T> filter) {
		_subscriber = (Consumer<? super Tuple>)subscriber;
		_tupleType = tupleType;
		_filter = filter;
		_environment = my(Environment.class);
	}

	
	private void notifySubscriber(Tuple tuple) {
		if (_subscriber == null) return;
		TupleDispatcher.dispatch(tuple, _subscriber, _environment);
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
	
	
	synchronized
	private void pushTuple(Tuple tuple) {
		if (isDisposed) return;
		
		boolean wasWaiting = _tuplesToNotify.isEmpty();  
		_tuplesToNotify.add(tuple);
		
		if (wasWaiting) 
			startNotifyingSubscriber();
	}


	private void startNotifyingSubscriber() {
		my(Threads.class).startDaemon("Notifier for " + _tupleType, new Closure() { @Override public void run() {
			while (notifySubscriberOfNextTuple());
		}});
	}


	private boolean notifySubscriberOfNextTuple() {
		boolean wasDisposed;
		Tuple tuple;
		synchronized (this) {
			wasDisposed = isDisposed;
			tuple = _tuplesToNotify.get(0);
		}
		
		if (!wasDisposed)
			notifySubscriber(tuple);
		
		synchronized (this) {
			_tuplesToNotify.remove(0);
			TupleDispatcher.dispatchCounterDecrement();
			return !_tuplesToNotify.isEmpty(); 
		}
	}
	
	
	/** Removes this subscription as soon as possible. The subscription might still receive one tuple notification from another thread AFTER this method returns, though. It is impossible to guarantee synchronization of this method without risking deadlocks, especially with the GUI thread. If you really need to know when the subscription was removed, get in touch with us. We can change the API to provide for a callback.*/
	synchronized
	void dispose() {
		if (isDisposed) throw new IllegalStateException("Tuples subscription was already disposed");
		isDisposed = true;
	}

}