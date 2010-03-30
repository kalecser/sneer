package sneer.bricks.network.computers.ports.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.ports.PortTuple;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class OwnPortTest extends BrickTest {

	private final OwnPort _subject = my(OwnPort.class);
	
	@Test(timeout = 2000)
	public void ownPortTupleIsPublished() throws Exception {
		_subject.portSetter().consume(42);
		
		assertEquals(42, (int)_subject.port().currentValue());
		assertElementsInAnyOrder(my(TupleSpace.class).keptTuples(), new PortTuple(42));
	}
	
}
