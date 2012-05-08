package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.util.Arrays;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.Signals;
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
		connectionFor("Neide").initCommunications(new PacketSchedulerMock(), new Consumer<byte[]>() { @Override public void consume(byte[] value) {
			assertEquals("Hello", new String(value));
			latch.open();
		}});
		
		subject.handle(packetFrom("Neide", "Hello".getBytes()));
		latch.waitTillOpen();
	}

	
	@Test (timeout=1000)
	public void sendData() throws Refusal {
		final Register<String> packets = my(Signals.class).newRegister("");
		subject.initSender(new Consumer<DatagramPacket>() {  @Override public void consume(DatagramPacket packetToSend) {
			byte[] bytes = packetToSend.getData();
			byte[] seal = Arrays.copyOf(bytes, Seal.SIZE_IN_BYTES);
			byte[] payload = payload(bytes, Seal.SIZE_IN_BYTES);
			assertArrayEquals(ownSealBytes(), seal);
			String current = packets.output().currentValue();
			packets.setter().consume(current + "+" + new String(payload));
			assertEquals("200.201.202.203", packetToSend.getAddress().getHostAddress());
			assertEquals(123, packetToSend.getPort());
		}});
		
		my(InternetAddressKeeper.class).put(produceContact("Neide"), "200.201.202.203", 123);
		
		PacketScheduler sender = new PacketSchedulerMock("foo", "bar");
		connectionFor("Neide").initCommunications(sender, my(Signals.class).sink());
		my(SignalUtils.class).waitForValue(packets.output(), "+foo+bar");
	}
	
	
	@Test
	public void invalidPacket() {
		subject.handle(new DatagramPacket(new byte[0], 0));
	}

	private byte[] payload(byte[] data, int offset) {
		return Arrays.copyOfRange(data, offset, data.length);
	}
	
	private DatagramPacket packetFrom(String nick, byte[] data) throws Refusal {
		produceContact(nick);
		my(ContactSeals.class).put(nick, new Seal(fill(42)));
		byte[] bytes = my(Lang.class).arrays().concat(fill(42), data);
		return new DatagramPacket(bytes, bytes.length);
	}

	
	private byte[] fill(int id) {
		byte[] ret = new byte[Seal.SIZE_IN_BYTES];
		Arrays.fill(ret, (byte)id);
		return ret;
	}

	private Boolean isConnected(String nick) {
		ByteConnection connection = connectionFor(nick);
		return connection.isConnected().currentValue();
	}

	private ByteConnection connectionFor(String nick) {
		return subject.connectionFor(produceContact(nick));
	}


	private Contact produceContact(String nick) {
		return my(Contacts.class).produceContact(nick);
	}

	private DatagramPacket packetFrom(String nick) throws Refusal {
		return packetFrom(nick, new byte[0]);
	}


	private byte[] ownSealBytes() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}
}
