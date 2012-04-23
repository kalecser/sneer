package sneer.bricks.network.computers.tcp.connections.originator.tests;

import static basis.environments.Environments.my;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.tcp.ByteArraySocket;
import sneer.bricks.network.computers.tcp.TcpNetwork;
import sneer.bricks.network.computers.tcp.connections.TcpConnectionManager;
import sneer.bricks.network.computers.tcp.connections.originator.SocketOriginator;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.brickness.testsupport.Bind;
import basis.lang.arrays.ImmutableByteArray;
import basis.util.concurrent.Latch;

public class SocketOriginatorTest extends BrickTestBase {

	@SuppressWarnings("unused")
	private SocketOriginator _subject;

	@Bind private final TcpNetwork _networkMock = mock(TcpNetwork.class);
	@Bind private final TcpConnectionManager _socketConnectionManagerMock = mock(TcpConnectionManager.class);

	private final ByteConnection _byteConnection = mock(ByteConnection.class);
	private final Signal<Boolean> _isConnected = my(Signals.class).constant(false);
	private final ByteArraySocket _openedSocket = mock(ByteArraySocket.class);


	@Test (timeout = 2000)
	public void openConnection() throws Exception {
		final Latch latch = new Latch();
		final Contact neide = my(Contacts.class).addContact("Neide");
		my(ContactSeals.class).put("Neide", newSeal(new byte[]{42}));

		checking(new Expectations() {{
			oneOf(_socketConnectionManagerMock).connectionFor(neide);
				will(returnValue(_byteConnection));

			oneOf(_byteConnection).isConnected();
				will(returnValue(_isConnected));

			oneOf(_networkMock).openSocket("neide.selfip.net", 5000);
				will(returnValue(_openedSocket));

			oneOf(_socketConnectionManagerMock).manageOutgoingSocket(_openedSocket, neide);
				will(new CustomAction("manageIncomingSocket") { @Override public Object invoke(Invocation ignored) {
					latch.open(); return null;
				}});
		}});

		_subject = my(SocketOriginator.class);

		my(InternetAddressKeeper.class).put(neide, "neide.selfip.net", 5000);
		latch.waitTillOpen();
	}

	
	private Seal newSeal(byte[] bytes) {
		return new Seal(new ImmutableByteArray(bytes));
	}
}
