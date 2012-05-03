package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.util.Arrays;

import org.junit.Test;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.lang.Consumer;
import basis.lang.exceptions.Refusal;
import basis.util.concurrent.Latch;

public class UdpConnectionManagerTest extends BrickTestBase {

	
	private UdpConnectionManager subject = my(UdpConnectionManager.class);

	
	@Test(timeout=1000)
	public void onFirstPacket_ShouldConnect() throws Refusal{
		assertFalse(isConnected("Neide"));
		subject.handle(packetFrom("Neide"));
		my(SignalUtils.class).waitForValue(connectionFor("Neide").isConnected(), true);
		assertFalse(isConnected("Maicon"));
	}


	@Test (timeout=1000)
	public void receiveData() throws Refusal{
		final Latch latch = new Latch();
		connectionFor("Neide").initCommunications(null, new Consumer<byte[]>() { @Override public void consume(byte[] value) {
			assertEquals("Hello", new String(value));
			latch.open();
		}});
		
		subject.handle(packetFrom("Neide", "Hello".getBytes()));
		latch.waitTillOpen();
	}

	
	@Test
	public void invalidPacket() {
		subject.handle(new DatagramPacket(new byte[0], 0));
	}

	
	private DatagramPacket packetFrom(String nick, byte[] data) throws Refusal {
		my(Contacts.class).produceContact(nick);
		my(ContactSeals.class).put(nick, new Seal(fill(42)));
		byte[] bytes = concat(fill(42), data);
		return new DatagramPacket(bytes, bytes.length);
	}

	
	private byte[] fill(int id) {
		byte[] ret = new byte[Seal.SIZE_IN_BYTES];
		Arrays.fill(ret, (byte)id);
		return ret;
	}

	private byte[] concat(byte[] a, byte[] b) {
		byte[] ret = new byte[a.length + b.length];
		System.arraycopy(a, 0, ret, 0, a.length);
		System.arraycopy(b, 0, ret, a.length, b.length);
		return ret ;
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
		return packetFrom(nick, new byte[0]);
	}
}
