package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.lang.Consumer;
import basis.lang.exceptions.Refusal;
import basis.util.concurrent.Latch;

public class UdpConnectionManagerTest extends BrickTestBase {

	
	private static final class PacketSchedulerMock implements PacketScheduler {
		final String[] messages;
		int next = 0;

		public PacketSchedulerMock(String... messages){
			this.messages = messages;
		}
		
		@Override
		public void previousPacketWasSent() {
			next++;
		}

		@Override
		public synchronized byte[] highestPriorityPacketToSend() {
			blockIfFinished();
			return messages[next].getBytes();
		}

		private void blockIfFinished() {
			if (next == messages.length) my(Threads.class).waitWithoutInterruptions(this);
		}
	}


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

	
	@Ignore
	@Test (timeout=1000)
	public void sendData() {
		final Register<String> packets = my(Signals.class).newRegister("");
		subject.initSender(new Consumer<DatagramPacket>() {  @Override public void consume(DatagramPacket value) {
			byte[] bytes = value.getData();
			byte[] seal = Arrays.copyOf(bytes, Seal.SIZE_IN_BYTES);
			byte[] payload = payload(bytes, Seal.SIZE_IN_BYTES);
			assertArrayEquals(ownSealBytes(), seal);
			String current = packets.output().currentValue();
			packets.setter().consume(current + new String(payload));
			fail("test ip/port");
		}});
		
		PacketScheduler sender = new PacketSchedulerMock("foo", "bar");
		connectionFor("Neide").initCommunications(sender, my(Signals.class).sink());
		my(SignalUtils.class).waitForValue(packets.output(), "foobar");
	}
	
	
	@Test
	public void invalidPacket() {
		subject.handle(new DatagramPacket(new byte[0], 0));
	}

	private byte[] payload(byte[] data, int offset) {
		return Arrays.copyOfRange(data, offset, data.length);
	}
	
	private DatagramPacket packetFrom(String nick, byte[] data) throws Refusal {
		my(Contacts.class).produceContact(nick);
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


	private byte[] ownSealBytes() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}
}
