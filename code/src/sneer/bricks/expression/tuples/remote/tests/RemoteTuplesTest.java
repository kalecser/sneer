package sneer.bricks.expression.tuples.remote.tests;

import static sneer.foundation.environments.Environments.my;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.expression.tuples.tests.TestTuple;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.software.folderconfig.testsupport.BrickTestWithFiles;
import sneer.foundation.lang.Consumer;

public class RemoteTuplesTest extends BrickTestWithFiles {

	private RemoteTuples _subject = my(RemoteTuples.class);

	@Test
	public void publishTuplesAndCheckForLoopback() {
		final AtomicInteger counter = new AtomicInteger(0);

		@SuppressWarnings("unused") WeakContract tuplesConsumer = 
			my(TupleSpace.class).addSubscription(TestTuple.class, new Consumer<TestTuple>() { @Override public void consume(TestTuple tuple) {
				counter.getAndIncrement();
			}});

		@SuppressWarnings("unused") WeakContract remoteTuplesConsumer =
			_subject.addSubscription(TestTuple.class, new Consumer<TestTuple>() { @Override public void consume(TestTuple ignored) {
				fail();
			}});

		my(TupleSpace.class).acquire(new TestTuple(0));
		my(TupleSpace.class).acquire(new TestTuple(1));
		my(TupleSpace.class).acquire(new TestTuple(2));

		my(TupleSpace.class).waitForAllDispatchingToFinish();

		assertEquals(3, counter.get());
	}

}
