package sneer.bricks.expression.tuples.impl;

import static basis.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basis.lang.Consumer;
import basis.lang.Functor;
import basis.lang.Predicate;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.floodcache.FloodedTupleCache;
import sneer.bricks.expression.tuples.keeper.TupleKeeper;
import sneer.bricks.hardware.cpu.lang.contracts.Contracts;
import sneer.bricks.hardware.cpu.lang.contracts.Disposable;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;

class TupleSpaceImpl implements TupleSpace {

	private static final FloodedTupleCache FloodedTupleCache = my(FloodedTupleCache.class);
	private static final TupleKeeper TupleKeeper = my(TupleKeeper.class);
	
	private static final Subscription<?>[] SUBSCRIPTION_ARRAY = new Subscription[0];

	private final List<Subscription<?>> _subscriptions = Collections.synchronizedList(new ArrayList<Subscription<?>>());

	
	@Override
	public synchronized void add(Tuple tuple) {
		if (tuple.addressee == null) {
			if (dealWithFloodedTuple(tuple)) return;
		} else
			if (dealWithAddressedTuple(tuple)) return;
		
		if (isAlreadyKept(tuple)) return;
		TupleKeeper.keepIfNecessary(tuple);
				
		notifySubscriptions(tuple);
	}


	@Override
	public <T extends Tuple> WeakContract addSubscription(Class<T> tupleType, Consumer<? super T> subscriber) {
		return addSubscription(tupleType, subscriber, Predicate.TRUE);
	}

	
	@Override
	public <T extends Tuple> WeakContract addSubscription(Class<T> tupleType, Consumer<? super T> subscriber, Predicate<? super T> filter) {
		final Subscription<?> subscription = new Subscription<T>(subscriber, tupleType, filter);

		for (Tuple kept : TupleKeeper.keptTuples())
			subscription.filterAndNotify(kept);

		_subscriptions.add(subscription);
		return my(Contracts.class).weakContractFor(new Disposable() {  @Override public void dispose() {
			_subscriptions.remove(subscription);
			subscription.dispose();
		}});
	}
	
	
	@Override
	public synchronized void keep(Class<? extends Tuple> tupleType) {
		TupleKeeper.keepType(tupleType);
	}
	
	
	@Override
	public <T extends Tuple> void keepChosen(Class<T> tupleType, Predicate<? super T> filter) {
		TupleKeeper.keepChosen(tupleType, filter);
	}


	@Override
	public <T extends Tuple> void keepNewest(Class<T> tupleType, Functor<? super T, Object> grouping) {
		TupleKeeper.keepNewest(tupleType, grouping);
	}

	
	private boolean isAlreadyKept(Tuple tuple) {
		boolean result = TupleKeeper.isAlreadyKept(tuple);
		if (result) logDuplicateTupleIgnored(tuple);
		return result;
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

	
	private void notifySubscriptions(Tuple tuple) {
		for (Subscription<?> subscription : _subscriptions.toArray(SUBSCRIPTION_ARRAY))
			subscription.filterAndPushToNotify(tuple);
	}

	
	private void logDuplicateTupleIgnored(Tuple tuple) {
		my(Logger.class).log("Duplicate tuple ignored: ", tuple);
	}

}
