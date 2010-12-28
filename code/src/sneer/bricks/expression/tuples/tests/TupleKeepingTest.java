package sneer.bricks.expression.tuples.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.dispatcher.TupleDispatcher;
import sneer.bricks.expression.tuples.floodcache.FloodedTupleCache;
import sneer.bricks.expression.tuples.keeper.TupleKeeper;
import sneer.bricks.expression.tuples.kept.KeptTuples;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;

public class TupleKeepingTest extends BrickTestBase {

	private int _notificationCounter;
	
	
	@Test (timeout = 5000)
	public void keptTuplesAreIgnoredWhenAdded() {

		@SuppressWarnings("unused")
		WeakContract contract = subject().addSubscription(KeptTuple.class, new Consumer<KeptTuple>() { @Override public void consume(KeptTuple ignored) {
			_notificationCounter++;
		}});
		
		subject().keep(KeptTuple.class);
		KeptTuple tuple = new KeptTuple(1);
		subject().add(tuple);
		flushCache();
		subject().add(tuple);
		
		my(TupleDispatcher.class).waitForAllDispatchingToFinish();

		assertEquals(1, my(KeptTuples.class).all().length);
		assertEquals(1, _notificationCounter);
	}


	@Ignore
	@Test (timeout = 2000)
	public void garbageCollectingOldTuples() {
		final Object singleGroup = new Object();
		subject().keepNewest(KeptTuple.class, new Functor<KeptTuple, Object>() {  @Override public Object evaluate(KeptTuple keptTuple) {
			return singleGroup;
		}});
		subject().keep(KeptTuple.class);
		
		subject().add(new KeptTuple(1));
		my(Clock.class).advanceTime(42);
		subject().add(new KeptTuple(2));

		my(TupleKeeper.class).garbageCollect();
		assertEquals(1, my(KeptTuples.class).all().length);
	}


	private TupleSpace subject() {
		return my(TupleSpace.class);
	}


	private void flushCache() {
		int cacheSize = my(FloodedTupleCache.class).maxSize();
		publishTestTuples(cacheSize);
	}


	private void publishTestTuples(int amount) {
		for (int i = 0; i < amount; i++)
			subject().add(new TestTuple(i));
	}
	
}


