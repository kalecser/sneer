package sneer.bricks.expression.tuples.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.software.folderconfig.testsupport.BrickTestWithFiles;
import sneer.foundation.lang.Consumer;

public class TupleSpaceResponsivenessTest extends BrickTestWithFiles {

	private final TupleSpace _subject = my(TupleSpace.class);
	
	@Test (timeout = 1000)
	public void test() {
		final Latch latch = my(Latches.class).produce();
		
		@SuppressWarnings("unused")	WeakContract contract = _subject.addSubscription(TestTuple.class, new Consumer<TestTuple>() { @Override public void consume(TestTuple value) {
			latch.open();
		}});

		_subject.acquire(new TestTuple(42));
		latch.waitTillOpen();
	}
	
}


