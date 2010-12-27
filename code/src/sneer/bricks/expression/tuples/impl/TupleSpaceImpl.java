package sneer.bricks.expression.tuples.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.floodcache.FloodedTupleCache;
import sneer.bricks.expression.tuples.kept.KeptTuples;
import sneer.bricks.hardware.cpu.lang.contracts.Contracts;
import sneer.bricks.hardware.cpu.lang.contracts.Disposable;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.Predicate;

class TupleSpaceImpl implements TupleSpace {

	private static final FloodedTupleCache FloodedTupleCache = my(FloodedTupleCache.class);
	
	private static final Subscription<?>[] SUBSCRIPTION_ARRAY = new Subscription[0];

	private final List<Subscription<?>> _subscriptions = Collections.synchronizedList(new ArrayList<Subscription<?>>());

	private final Set<Class<? extends Tuple>> _typesToKeep = new HashSet<Class<? extends Tuple>>();
	private final KeptTuples _keptTuples = my(KeptTuples.class);
	
	
	@Override
	public synchronized void add(Tuple tuple) {
		if (tuple.addressee == null) {
			if (dealWithFloodedTuple(tuple)) return;
		} else
			if (dealWithAddressedTuple(tuple)) return;
		
		if (isAlreadyKept(tuple)) return;
		keepIfNecessary(tuple);
				
		notifySubscriptions(tuple);
	}

	
	@Override
	public <T extends Tuple> WeakContract addSubscription(Class<T> tupleType, Consumer<? super T> subscriber) {
		return addSubscription(tupleType, subscriber, Predicate.TRUE);
	}

	
	@Override
	public <T extends Tuple> WeakContract addSubscription(Class<T> tupleType, Consumer<? super T> subscriber, Predicate<? super T> filter) {
		final Subscription<?> subscription = new Subscription<T>(subscriber, tupleType, filter);

		for (Tuple kept : _keptTuples.all())
			subscription.filterAndNotify(kept);

		_subscriptions.add(subscription);
		return my(Contracts.class).weakContractFor(new Disposable() {  @Override public void dispose() {
			_subscriptions.remove(subscription);
			subscription.dispose();
		}});
	}
	
	
	@Override
	public synchronized void keep(Class<? extends Tuple> tupleType) {
		_typesToKeep.add(tupleType);
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
		boolean isDuplicated = !FloodedTupleCache.add(tuple);
		
		if (isDuplicated) logDuplicateTupleIgnored(tuple);
		return isDuplicated;
	}

	
	private void logDuplicateTupleIgnored(Tuple tuple) {
		my(Logger.class).log("Duplicate tuple ignored: ", tuple);
	}

	
	private void notifySubscriptions(Tuple tuple) {
		for (Subscription<?> subscription : _subscriptions.toArray(SUBSCRIPTION_ARRAY))
			subscription.filterAndPushToNotify(tuple);
	}


	private void keepIfNecessary(Tuple tuple) {
		if (shouldKeep(tuple)) _keptTuples.add(tuple);
	}

	
	private boolean shouldKeep(Tuple tuple) {
		for (Class<? extends Tuple> typeToKeep : _typesToKeep) //Optimize
			if (typeToKeep.isInstance(tuple))
				return true;

		return false;
	}


	private boolean isAlreadyKept(Tuple tuple) {
		boolean result = _keptTuples.contains(tuple);
		if (result) logDuplicateTupleIgnored(tuple);
		return result;
	}


	@Override
	public <T extends Tuple> void keepNewest(Class<T> tupleType, Functor<? super T, Object> grouping) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

}
