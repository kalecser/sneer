package sneer.bricks.expression.tuples.tests;

import static sneer.foundation.environments.Environments.my;

import java.util.Arrays;

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
	public void duplicateKeptTuplesAreIgnored() {

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


	@Test (timeout = 2000)
	public void garbageCollectingOldTuplesByGroup() {
		subject().keepNewest(KeptTuple.class, new Functor<KeptTuple, Object>() {  @Override public Object evaluate(KeptTuple keptTuple) {
			return keptTuple.number % 2; //Group into odds and evens.
		}});
		
		subject().add(new KeptTuple(1));
		my(Clock.class).advanceTime(42);
		subject().add(new KeptTuple(2));
		my(Clock.class).advanceTime(42);
		KeptTuple tuple3 = new KeptTuple(3);
		subject().add(tuple3);
		my(Clock.class).advanceTime(42);
		KeptTuple tuple4 = new KeptTuple(4);
		subject().add(tuple4);

		my(TupleKeeper.class).garbageCollect();
		assertContentsInAnyOrder(Arrays.asList(my(KeptTuples.class).all()), tuple3, tuple4);
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


