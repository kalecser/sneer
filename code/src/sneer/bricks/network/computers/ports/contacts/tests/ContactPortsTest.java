package sneer.bricks.network.computers.ports.contacts.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.ports.PortTuple;
import sneer.bricks.network.computers.ports.contacts.ContactPorts;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;

public class ContactPortsTest extends BrickTest {

	@Bind private final OwnSeal _ownSeal = mock(OwnSeal.class);
	
	private final ContactPorts _subject = my(ContactPorts.class);
	
	@Test(timeout = 2000)
	public void contactPorts() {
		checking(new Expectations(){{
			oneOf(_ownSeal).oldGet(); will(returnValue(seal(42)));
		}});
		my(TupleSpace.class).acquire(new PortTuple(8081));
		
		my(SignalUtils.class).waitForValue(_subject.portGiven(seal(42)), 8081);
	}
	
	
	private Seal seal(int seal) {
		return new Seal(my(ImmutableArrays.class).newImmutableByteArray(new byte[]{(byte)seal}));
	}
}
