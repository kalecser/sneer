package sneer.bricks.network.computers.sockets.connections.originator.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.computers.sockets.connections.ByteConnection;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.connections.originator.SocketOriginator;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.lang.arrays.ImmutableByteArray;
import sneer.foundation.util.concurrent.Latch;

public class SocketOriginatorTest extends BrickTestBase {

	@SuppressWarnings("unused")
	private SocketOriginator _subject;

	@Bind private final Network _networkMock = mock(Network.class);
	@Bind private final ConnectionManager _connectionManagerMock = mock(ConnectionManager.class);

	private final ByteConnection _byteConnection = mock(ByteConnection.class);
	private final Signal<Boolean> _isConnected = my(Signals.class).constant(false);
	private final ByteArraySocket _openedSocket = mock(ByteArraySocket.class);


	@Test (timeout = 2000)
	public void openConnection() throws Exception {
		final Latch ready = new Latch();
		final Contact neide = my(Contacts.class).addContact("Neide");
		my(ContactSeals.class).put("Neide", newSeal(new byte[]{42}));

		checking(new Expectations() {{
			oneOf(_connectionManagerMock).connectionFor(neide);
				will(returnValue(_byteConnection));

			oneOf(_byteConnection).isConnected();
				will(returnValue(_isConnected));

			oneOf(_networkMock).openSocket("neide.selfip.net", 5000);
				will(returnValue(_openedSocket));

			oneOf(_connectionManagerMock).manageOutgoingSocket(_openedSocket, neide);
				will(new CustomAction("manageIncomingSocket") { @Override public Object invoke(Invocation ignored) {
					ready.open(); return null;
				}});
		}});

		_subject = my(SocketOriginator.class);

		my(InternetAddressKeeper.class).add(neide, "neide.selfip.net", 5000);
		ready.waitTillOpen();
	}

	
	private Seal newSeal(byte[] bytes) {
		return new Seal(new ImmutableByteArray(bytes));
	}
}
