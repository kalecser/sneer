package sneer.bricks.expression.tuples.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.kept.KeptTuples;
import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.lang.contracts.Contracts;
import sneer.bricks.hardware.cpu.lang.contracts.Disposable;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.exceptionhandling.ExceptionHandler;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Predicate;

class TupleSpaceImpl implements TupleSpace {

	//Refactor The synchronization will no longer be necessary when the container guarantees synchronization of model bricks.
	class Subscription<T extends Tuple> implements Disposable {

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
			
			_stepperContract = _threads.startStepping(notifier());
		}

		private Runnable notifier() {
			return new Closure() { @Override public void run() {
				Tuple nextTuple = waitToPopTuple();
				if (_isDisposed) return;
				
				notifySubscriber(nextTuple);
				dispatchCounterDecrement();
			}};
		}

		private void notifySubscriber(final Tuple tuple) {
			final Consumer<? super Tuple> subscriber = _subscriber;
			if (subscriber == null) return;
			
			_exceptionHandler.shield(new Closure() { @Override public void run() {
				Environments.runWith(_environment, new Closure() { @Override public void run() {
					subscriber.consume(tuple);
				}});
			}});
		}

		void filterAndNotify(final Tuple tuple) {
			if (!_tupleType.isInstance(tuple)) return;
			if (!_filter.evaluate((T)tuple)) return;
			
			dispatchCounterIncrement();
			pushTuple(tuple);
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

		@Override
		/** Removes this subscription as soon as possible. The subscription might still receive tuple notifications from other threads AFTER this method returns, though. It is impossible to guarantee synchronicity of this method without risking deadlocks, especially with the GUI thread. If you really need to know when the subscription was removed, get in touch with us. We can change the API to provide for a callback.*/
		public void dispose() {
			_stepperContract.dispose();
			_subscriptions.remove(this);
			synchronized(_tuplesToNotify) {
				_isDisposed = true;
				_tuplesToNotify.notify();
			}
		}
	
	}

	private static final int FLOODED_CACHE_SIZE = 1000;
	private static final Subscription<?>[] SUBSCRIPTION_ARRAY = new Subscription[0];

	private final Threads _threads = my(Threads.class);
	private final ExceptionHandler _exceptionHandler = my(ExceptionHandler.class);

	private final List<Subscription<?>> _subscriptions = Collections.synchronizedList(new ArrayList<Subscription<?>>());

	private final Object _dispatchCounterMonitor = new Object();
	private int _dispatchCounter = 0;

	private final Set<Tuple> _floodedTupleCache = new LinkedHashSet<Tuple>();
	private final Set<Class<? extends Tuple>> _typesToKeep = new HashSet<Class<? extends Tuple>>();
	private final ListRegister<Tuple> _keptTuples;

	TupleSpaceImpl() {
		_keptTuples = my(KeptTuples.class);
	}
	
	@Override
	public synchronized void acquire(Tuple tuple) {
		if (tuple.addressee == null) {
			if (dealWithFloodedTuple(tuple)) return;
		} else
			if (dealWithAddressedTuple(tuple)) return;
		
		if (isAlreadyKept(tuple)) return;
		keepIfNecessary(tuple);
				
		notifySubscriptions(tuple);
	}

	private boolean dealWithAddressedTuple(Tuple tuple) {
		Seal me = my(OwnSeal.class).get().currentValue();
		if (!tuple.addressee.equals(me) && !tuple.publisher.equals(me)) {
			my(Logger.class).log("Tuple received with incorrect addressee: {} type: ", tuple.addressee, tuple.getClass());
			return true;
		}
		return false;
	}


	private boolean dealWithFloodedTuple(Tuple tuple) {
		if (_floodedTupleCache.contains(tuple)) {
			logDuplicateTupleIgnored(tuple);
			return true;
		};
		_floodedTupleCache.add(tuple);
		capFloodedTuples();
		return false;
	}

	
	private void logDuplicateTupleIgnored(Tuple tuple) {
		my(Logger.class).log("Duplicate tuple ignored: ", tuple);
	}

	
	private void notifySubscriptions(Tuple tuple) {
		for (Subscription<?> subscription : _subscriptions.toArray(SUBSCRIPTION_ARRAY))
			subscription.filterAndNotify(tuple);
	}


	private void keepIfNecessary(Tuple tuple) {
		if (shouldKeep(tuple)) keep(tuple);
	}

	
	private boolean shouldKeep(Tuple tuple) {
		for (Class<? extends Tuple> typeToKeep : _typesToKeep) //Optimize
			if (typeToKeep.isInstance(tuple))
				return true;

		return false;
	}


	private boolean isAlreadyKept(Tuple tuple) {
		boolean result = _keptTuples.output().currentIndexOf(tuple) != -1;   //Optimize
		if (result) logDuplicateTupleIgnored(tuple);
		return result;
	}


	private void keep(Tuple tuple) {
		_keptTuples.adder().consume(tuple);
	}

	
	private void capFloodedTuples() {
		if (_floodedTupleCache.size() <= FLOODED_CACHE_SIZE) return;

		Iterator<Tuple> tuplesIterator = _floodedTupleCache.iterator();
		tuplesIterator.next();
		tuplesIterator.remove();
		
	}

	
	@Override
	public <T extends Tuple> WeakContract addSubscription(Class<T> tupleType, Consumer<? super T> subscriber) {
		return addSubscription(tupleType, subscriber, Predicate.TRUE);
	}

	
	@Override
	public <T extends Tuple> WeakContract addSubscription(Class<T> tupleType, Consumer<? super T> subscriber, Predicate<? super T> filter) {
		Subscription<?> subscription = new Subscription<T>(subscriber, tupleType, filter);

		for (Tuple kept : keptTuples())
			subscription.filterAndNotify(kept);

		_subscriptions.add(subscription);
		return my(Contracts.class).weakContractFor(subscription);
	}
	
	
	@Override
	public synchronized void keep(Class<? extends Tuple> tupleType) {
		_typesToKeep.add(tupleType);
	}

	@Override
	public synchronized List<Tuple> keptTuples() {
		return _keptTuples.output().currentElements();
	}


	@Override
	public int floodedCacheSize() {
		return FLOODED_CACHE_SIZE;
	}

	@Override	
	public void waitForAllDispatchingToFinish() {
		synchronized (_dispatchCounterMonitor ) {
			while (_dispatchCounter != 0)
				_threads.waitWithoutInterruptions(_dispatchCounterMonitor);
		}
		
	}

	private void dispatchCounterIncrement() {
		synchronized (_dispatchCounterMonitor ) {
			_dispatchCounter++;
		}
	}

	private void dispatchCounterDecrement() {
		synchronized (_dispatchCounterMonitor ) {
			_dispatchCounter--;
			if (_dispatchCounter == 0)
				_dispatchCounterMonitor.notifyAll();
		}
	}
}
