package sneer.bricks.expression.tuples.tests;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;

import org.junit.Test;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.dispatcher.TupleDispatcher;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Consumer;

public class TupleSpaceTest extends BrickTestBase {

	private final TupleSpace _subject = my(TupleSpace.class);
	
	@Test (timeout = 2000)
	public void subscriptionRemoval() {
		final ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		WeakContract contract = _subject.addSubscription(TestTuple.class, new Consumer<TestTuple>() { @Override public void consume(TestTuple value) {
			tuples.add(value);
		}});
		
		final TestTuple tuple = new TestTuple(42);
		_subject.add(tuple);
		my(TupleDispatcher.class).waitForAllDispatchingToFinish();

		contract.dispose();
		
		_subject.add(new TestTuple(-1));
		my(TupleDispatcher.class).waitForAllDispatchingToFinish();
		assertArrayEquals(new Object[] { tuple }, tuples.toArray());
	}

	
	@Test (timeout = 4000)
	public void testContractWeakness() throws Exception {
		final ByRef<Boolean> finalized = ByRef.newInstance(false);
		
		_subject.addSubscription(TestTuple.class, new Consumer<TestTuple>() {
			
			@Override
			public void consume(TestTuple value) {}

			@Override
			protected void finalize() throws Throwable {
				finalized.value = true; 
			}

		});

		while (!finalized.value) {
			System.gc();
			my(Threads.class).sleepWithoutInterruptions(100);
		}
	}


	@Test (timeout = 2000)
	public void subscriptionIsNotifiedSynchronously() {
		_subject.keep(TestTuple.class);
		_subject.add(new TestTuple(1));
		_subject.add(new TestTuple(2));
		_subject.add(new TestTuple(3));
		
		final ByRef<Integer> counter = ByRef.newInstance(0);
		_subject.addSubscription(TestTuple.class, new Consumer<TestTuple>() { @Override public void consume(TestTuple tuple) {
			counter.value++;
		}});
		
		assertEquals((Object)3, counter.value);
	}

}


