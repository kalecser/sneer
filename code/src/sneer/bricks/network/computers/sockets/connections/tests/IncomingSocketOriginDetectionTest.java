package sneer.bricks.network.computers.sockets.connections.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.junit.Test;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.protocol.ProtocolTokens;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;

public class IncomingSocketOriginDetectionTest extends BrickTest {

	@Bind private final ContactSeals _seals = mock(ContactSeals.class);
	@Bind private final OwnSeal _ownSealBrick = mock(OwnSeal.class);
//	@Bind private final SocketAccepter _socketAccepter = mock(SocketAccepter.class);

	private ConnectionManager _subject = my(ConnectionManager.class);

	private final ByteArraySocket _socket = mock(ByteArraySocket.class);

	private final Seal _ownSeal   = newSeal(new byte[]{2, 2, 2});
	private final Seal _otherSeal = newSeal(new byte[]{1, 1, 1});

		
	@Test (timeout = 2000)
	public void tieBreak() throws Exception {
		
		final Contact contact = mock(Contact.class);
		
		checking(new Expectations() {{
			Sequence sequence = newSequence("main");

			allowing(_ownSealBrick).get(); will(returnValue(_ownSeal));
			
			allowing(_seals).contactGiven(_otherSeal); will(returnValue(contact));
			allowing(_seals).sealGiven(contact); will(returnValue(constant(_otherSeal)));

			oneOf(_socket).read(); will(returnValue(ProtocolTokens.SNEER_WIRE_PROTOCOL_1)); inSequence(sequence);
			oneOf(_socket).read(); will(returnValue(new byte[]{1, 1, 1})); inSequence(sequence);
			oneOf(_socket).write(ProtocolTokens.CONFIRMED); inSequence(sequence);

			oneOf(_socket).close();
			
		}});

		_subject.manageIncomingSocket(_socket);
		
		my(Threads.class).crashAllThreads();
	}

	
	private static Signal<Seal> constant(Seal seal) {
		return my(Signals.class).constant(seal);
	}


	private Seal newSeal(byte[] bytes) {
		return new Seal(new ImmutableByteArray(bytes));
	}

}