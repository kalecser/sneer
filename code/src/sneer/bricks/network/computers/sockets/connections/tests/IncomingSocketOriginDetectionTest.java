package sneer.bricks.network.computers.sockets.connections.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.junit.Test;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.connections.ContactSighting;
import sneer.bricks.network.computers.sockets.protocol.ProtocolTokens;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.lang.Consumer;

public class IncomingSocketOriginDetectionTest extends BrickTest {

	@Bind private final ContactSeals _seals = mock(ContactSeals.class);
	@Bind private final OwnSeal _ownSealBrick = mock(OwnSeal.class);
	@Bind private final Network _network = mock(Network.class);

	private ConnectionManager _subject = my(ConnectionManager.class);

	private final ByteArraySocket _socket = mock(ByteArraySocket.class);

	private final Seal _ownSeal   = newSeal(new byte[]{2, 2, 2});
	private final Seal _otherSeal = newSeal(new byte[]{1, 1, 1});

		
	@Test (timeout = 2000)
	public void incomingSocketOriginDetection() throws Exception {
		
		final Contact contact = mock(Contact.class);
		
		checking(new Expectations() {{

			allowing(_ownSealBrick).oldGet(); will(returnValue(_ownSeal));
			
			allowing(_seals).contactGiven(_otherSeal); will(returnValue(contact));
			allowing(_seals).sealGiven(contact); will(returnValue(constant(_otherSeal)));

			Sequence sequence = newSequence("seal");
			oneOf(_socket).read(); will(returnValue(ProtocolTokens.SNEER_WIRE_PROTOCOL_1)); inSequence(sequence);
			oneOf(_socket).read(); will(returnValue(new byte[]{1, 1, 1})); inSequence(sequence);
			oneOf(_socket).write(ProtocolTokens.CONFIRMED); inSequence(sequence);

			oneOf(_network).remoteIpFor(_socket); will(returnValue("10.42.10.42"));
			
			oneOf(_socket).close();
			
		}});

		final Latch ipDetected = my(Latches.class).produce();
		_subject.contactSightings().addReceiver(new Consumer<ContactSighting>() { @Override public void consume(ContactSighting sighting) {
			assertEquals(_otherSeal, sighting.seal());
			assertEquals("10.42.10.42", sighting.ip());
			ipDetected.open();
		}});

		_subject.manageIncomingSocket(_socket);
		
		ipDetected.waitTillOpen();
		
		my(Threads.class).crashAllThreads();
	}

	
	private static Signal<Seal> constant(Seal seal) {
		return my(Signals.class).constant(seal);
	}


	private Seal newSeal(byte[] bytes) {
		return new Seal(new ImmutableByteArray(bytes));
	}

}