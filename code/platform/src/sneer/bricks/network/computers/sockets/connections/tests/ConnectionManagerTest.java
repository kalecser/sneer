package sneer.bricks.network.computers.sockets.connections.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.io.log.tests.TestThatUsesLogger;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.ContactManager;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.SignalUtils;

public class ConnectionManagerTest extends TestThatUsesLogger {

	private final ConnectionManager _subject = my(ConnectionManager.class);

	private final ContactManager _contactManager = my(ContactManager.class);

	@Ignore
	@Test (timeout = 2000)
	public void connection() {
		final Contact neide = _contactManager.produceContact("Neide");

		Signal<Boolean> isConnected = _subject.connectionFor(neide).isConnected();
		assertFalse(isConnected.currentValue());
		
		//Implement: MOCK THE INCOMING OR OUTGOING CONNECTIONS TO NEIDE. 

		my(SignalUtils.class).waitForValue(isConnected, true);
	}
}
