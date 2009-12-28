package sneer.bricks.pulp.tuples.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import sneer.bricks.pulp.serialization.Serializer;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;


public class TupleClassNotFoundSkippingTest extends BrickTest {

	@Bind private final Serializer _serializer = mock(Serializer.class);
	
	
	@Test
	public void tupleClassesNotFoundAreSkipped() throws Exception {
		checking(new Expectations() {{
			Sequence seq = newSequence("whatever");
			
			oneOf(_serializer).serialize(with(any(OutputStream.class)), with(anything())); inSequence(seq);
				will(new CustomAction("serializing") { @Override public Object invoke(Invocation invocation) {
					writeAnything((OutputStream)invocation.getParameter(0));
					return null;
				}});				

			//Once for Prevayler fail-fast baptism deep cloning and once for the actual transaction replay.	
			exactly(2).of(_serializer).deserialize(with(any(InputStream.class)), with(any(ClassLoader.class))); inSequence(seq);
				will(throwException(new ClassNotFoundException()));

		}});
		
		runInNewEnvironment(new Runnable() { @Override public void run() {
			TupleSpace subject1 = createSubject();
			subject1.keep(TestTuple.class);
			subject1.acquire(new TestTuple(0));
		}});
		
		runInNewEnvironment(new Runnable() { @Override public void run() {
			TupleSpace subject2 = createSubject();
			assertTrue(subject2.keptTuples().isEmpty());
		}});
	}

	
	private void runInNewEnvironment(Runnable runnable) {
		Environment newEnvironment = newTestEnvironment(my(FolderConfig.class));
		Environments.runWith(newEnvironment, runnable);
	}
	
	
	private TupleSpace createSubject() {
		return my(TupleSpace.class);
	}


	private void writeAnything(OutputStream outputStream) {
		try {
			outputStream.write("anything".getBytes());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	
}


