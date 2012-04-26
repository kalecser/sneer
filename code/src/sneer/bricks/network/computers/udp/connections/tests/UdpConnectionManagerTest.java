package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;

import org.junit.Test;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.lang.exceptions.Refusal;

public class UdpConnectionManagerTest extends BrickTestBase {

	
	private UdpConnectionManager subject = my(UdpConnectionManager.class);

	@Test(timeout=1000)
	public void onFirstPacket_ShouldCreateConnection() throws Refusal{
		assertFalse(isConnected("Neide"));
		subject.handle(packetFrom("Neide"));
		my(SignalUtils.class).waitForValue(connectionFor("Neide").isConnected(), true);
		assertFalse(isConnected("Maicon"));
	}

	private Boolean isConnected(String nick) {
		ByteConnection connection = connectionFor(nick);
		Boolean isConnected = connection.isConnected().currentValue();
		return isConnected;
	}

	private ByteConnection connectionFor(String nick) {
		ByteConnection connection = subject.connectionFor(my(Contacts.class).produceContact(nick));
		return connection;
	}

	private DatagramPacket packetFrom(String nick) throws Refusal {
		my(Contacts.class).produceContact(nick);
		my(ContactSeals.class).put(nick, new Seal(new byte[]{42}));
		byte[] bytes = new byte[]{42};
		return new DatagramPacket(bytes, bytes.length);
	}
}
