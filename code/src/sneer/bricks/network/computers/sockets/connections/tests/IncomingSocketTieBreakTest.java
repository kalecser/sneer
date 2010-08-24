package sneer.bricks.network.computers.sockets.connections.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.junit.Test;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.protocol.ProtocolTokens;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.testsupport.BrickTestWithFiles;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.lang.arrays.ImmutableByteArray;

public class IncomingSocketTieBreakTest extends BrickTestWithFiles {

	@Bind private final ContactSeals _seals = mock(ContactSeals.class);
	@Bind private final OwnSeal _ownSealBrick = mock(OwnSeal.class);
	@Bind private final Network _network = mock(Network.class);

	private ConnectionManager _subject = my(ConnectionManager.class);

	private final ByteArraySocket _socketA = mock("socketA", ByteArraySocket.class);
	private final ByteArraySocket _socketB = mock("socketB", ByteArraySocket.class);


	private final Seal _smallerSeal = newSeal(new byte[]{1, 1, 1});
	private final Seal _ownSeal     = newSeal(new byte[]{2, 2, 2});
	private final Seal _greaterSeal = newSeal(new byte[]{3, 3, 3});

		
	@Test (timeout = 2000)
	public void tieBreak() throws Exception {
		
		final Contact a = my(Contacts.class).addContact("Contact A");
		final Contact b = my(Contacts.class).addContact("Contact B");
		
		checking(new Expectations() {{
			Sequence sequence = newSequence("main");

			allowing(_ownSealBrick).get().currentValue(); will(returnValue(constant(_ownSeal)));
			
			allowing(_seals).contactGiven(_smallerSeal); will(returnValue(a));
			allowing(_seals).contactGiven(_greaterSeal); will(returnValue(b));
			allowing(_seals).sealGiven(a); will(returnValue(constant(_smallerSeal)));
			allowing(_seals).sealGiven(b); will(returnValue(constant(_greaterSeal)));
			
			allowing(_network).remoteIpFor(with(any(ByteArraySocket.class)));

			oneOf(_socketA).read(); will(returnValue(ProtocolTokens.SNEER_WIRE_PROTOCOL_1)); inSequence(sequence);
			oneOf(_socketA).read(); will(returnValue(new byte[]{1, 1, 1})); inSequence(sequence);
			oneOf(_socketA).read(); will(returnValue("Neide".getBytes("UTF-8"))); inSequence(sequence);
			oneOf(_socketA).write(ProtocolTokens.CONFIRMED); inSequence(sequence);

			oneOf(_socketB).read(); will(returnValue(ProtocolTokens.SNEER_WIRE_PROTOCOL_1)); inSequence(sequence);
			oneOf(_socketB).read(); will(returnValue(new byte[]{3, 3, 3})); inSequence(sequence);
			oneOf(_socketB).read(); will(returnValue("Maicon".getBytes("UTF-8"))); inSequence(sequence);
			oneOf(_socketB).read(); will(returnValue(ProtocolTokens.CONFIRMED)); inSequence(sequence);

			oneOf(_socketA).close();
			oneOf(_socketB).close();
			
		}});

		_subject.manageIncomingSocket(_socketA);
		_subject.manageIncomingSocket(_socketB);
		
		my(Threads.class).crashAllThreads();
	}

	
	private static Signal<Seal> constant(Seal seal) {
		return my(Signals.class).constant(seal);
	}


	private Seal newSeal(byte[] bytes) {
		return new Seal(new ImmutableByteArray(bytes));
	}

}