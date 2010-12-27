package sneer.bricks.expression.tuples.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.kept.KeptTuples;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class TuplePersistenceTest extends BrickTestBase {

	@Test (timeout = 2000)
	public void tuplePersistence() {
		runInNewEnvironment(new Closure() { @Override public void run() {
			TupleSpace subject1 = createSubject();
	
			assertEquals((Integer)0, my(KeptTuples.class).output().size().currentValue());
	
			subject1.keep(TestTuple.class);
			subject1.add(tuple(0));
			subject1.add(tuple(1));
			subject1.add(tuple(2));
		}});

		runInNewEnvironment(new Closure() { @Override public void run() {
			List<Tuple> kept = my(KeptTuples.class).output().currentElements();
			assertEquals(3, kept.size());
			assertEquals(0, ((TestTuple)kept.get(0)).intValue);
			assertEquals(1, ((TestTuple)kept.get(1)).intValue);
			assertEquals(2, ((TestTuple)kept.get(2)).intValue);
		}});
	}

	@Test (timeout = 2000)
	public void filesAreClosedUponCrash() throws IOException {
		
		my(TupleSpace.class).keep(TestTuple.class);
		my(TupleSpace.class).add(tuple(42));
		
		my(Threads.class).crashAllThreads();
		
		my(IO.class).files().forceDelete(tmpFolder());
	}

	private TestTuple tuple(int i) {
		return new TestTuple(i);
	}

	private void runInNewEnvironment(Closure closure) {
		Environment newEnvironment = newTestEnvironment(my(FolderConfig.class));
		Environments.runWith(newEnvironment, closure);
		crash(newEnvironment);
	}

	private TupleSpace createSubject() {
		return my(TupleSpace.class);
	}

}
