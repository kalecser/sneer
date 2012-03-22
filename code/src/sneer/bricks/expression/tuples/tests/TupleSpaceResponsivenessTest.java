package sneer.bricks.expression.tuples.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import basis.lang.Consumer;
import basis.util.concurrent.Latch;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class TupleSpaceResponsivenessTest extends BrickTestBase {

	private final TupleSpace _subject = my(TupleSpace.class);
	
	@Test (timeout = 1000)
	public void test() {
		final Latch latch = new Latch();
		
		@SuppressWarnings("unused")	WeakContract contract = _subject.addSubscription(TestTuple.class, new Consumer<TestTuple>() { @Override public void consume(TestTuple value) {
			latch.open();
		}});

		_subject.add(new TestTuple(42));
		latch.waitTillOpen();
	}
	
}


