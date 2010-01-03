package sneer.bricks.pulp.tuples.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.Consumer;

public class TupleKeepingTest extends BrickTest {

	private int _notificationCounter;
	
	
	@Test //(timeout = 5000)
	public void tuplesLimitAmount() {

		@SuppressWarnings("unused")
		WeakContract contract = subject().addSubscription(KeptTuple.class, new Consumer<KeptTuple>() { @Override public void consume(KeptTuple ignored) {
			_notificationCounter++;
		}});
		
		subject().keep(KeptTuple.class);
		KeptTuple tuple = new KeptTuple(1);
		subject().acquire(tuple);
		flushCache();
		subject().acquire(tuple);
		
		subject().waitForAllDispatchingToFinish();

		assertEquals(1, subject().keptTuples().size());
		assertEquals(1, _notificationCounter);
	}


	private TupleSpace subject() {
		return my(TupleSpace.class);
	}


	private void flushCache() {
		int cacheSize = subject().floodedCacheSize();
		publishTestTuples(cacheSize);
	}


	private void publishTestTuples(int amount) {
		for (int i = 0; i < amount; i++)
			subject().acquire(new TestTuple(i));
	}
	
}


