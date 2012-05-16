package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.lang.Consumer;
import basis.lang.exceptions.Refusal;
import basis.util.concurrent.Latch;

public class UdpConnectionManagerTest extends BrickTestBase {

	
	private UdpConnectionManager subject = my(UdpConnectionManager.class);

	
	@Test(timeout=2000)
	public void onFirstPacket_ShouldConnect() throws Exception {
		assertFalse(isConnected("Neide"));
		subject.handle(packetFrom("Neide"));
		my(SignalUtils.class).waitForValue(connectionFor("Neide").isConnected(), true);
		assertFalse(isConnected("Maicon"));
	}


	@Test (timeout=2000)
	public void receiveData() throws Exception {
		final Latch latch = new Latch();
		connectionFor("Neide").initCommunications(new PacketSchedulerMock(), new Consumer<byte[]>() { @Override public void consume(byte[] value) {
			assertEquals("Hello", new String(value));
			latch.open();
		}});
		
		subject.handle(packetFrom("Neide", "Hello".getBytes()));
		latch.waitTillOpen();
	}

	
	@Test (timeout=2000)
	public void sendData() throws Refusal {
		LoggingSender sender = new LoggingSender();
		subject.initSender(sender);
		
		my(InternetAddressKeeper.class).put(produceContact("Neide"), "200.201.202.203", 123);
		
		ByteConnection connection = connectionFor("Neide");
		my(SignalUtils.class).waitForValue(sender.history(), "| <empty>,to:200.201.202.203,port:123");
		
		PacketScheduler scheduler = new PacketSchedulerMock("foo", "bar");
		connection.initCommunications(scheduler, my(Signals.class).sink());
		my(SignalUtils.class).waitForValue(sender.history(), "| <empty>,to:200.201.202.203,port:123| foo,to:200.201.202.203,port:123| bar,to:200.201.202.203,port:123");
	}
	
	
	@Test
	public void invalidPacket() {
		subject.handle(new DatagramPacket(new byte[0], 0));
	}
	

	@Test(timeout=2000)
	public void onNotConnected_ShouldSendHailingPacketsEverySoOften() throws Refusal {
		LoggingSender sender = new LoggingSender();
		subject.initSender(sender);

		my(InternetAddressKeeper.class).put(produceContact("Neide"), "200.201.202.203", 123);
		
		connectionFor("Neide");
		my(SignalUtils.class).waitForValue(sender.history(), "| <empty>,to:200.201.202.203,port:123");
	}
	
	@Test(timeout=2000)
	public void onReceivePacket_ShouldUseInternetAddress() throws Exception {
		LoggingSender sender = new LoggingSender();
		subject.initSender(sender);
		
		subject.handle(packetFrom("Neide"));
		
		PacketScheduler scheduler = new PacketSchedulerMock("foo");
		connectionFor("Neide").initCommunications(scheduler, my(Signals.class).sink());
		
		my(SignalUtils.class).waitForElement(sender.historySet(), "| foo,to:200.201.202.203,port:234");
	}
	
	
	@Test
	@Ignore
	public void shouldNotHailUnnessecerily() {
		fail();
	}
	
	@Test(timeout = 2000)
	public void onIdleRecognizeNewSighting() throws Exception {
		LoggingSender sender = new LoggingSender();
		subject.initSender(sender);
		
		subject.handle(packetFrom("Neide"));
		
		ByteConnection connection = connectionFor("Neide");
		
		my(Clock.class).advanceTime(UdpConnectionManager.IDLE_PERIOD);
		my(SignalUtils.class).waitForValue(connection.isConnected(), false);
		
		subject.handle(packetFrom("Neide", new byte[0], "100.101.102.103", 456));
		my(SignalUtils.class).waitForValue(connection.isConnected(), true);
		
		PacketScheduler scheduler = new PacketSchedulerMock("foo");
		connection.initCommunications(scheduler, my(Signals.class).sink());
		
		my(SignalUtils.class).waitForElement(sender.historySet(), "| foo,to:100.101.102.103,port:456");
	}
	
	@Test(timeout = 2000)
	public void keepAlive() throws Refusal {
		LoggingSender sender = new LoggingSender();
		subject.initSender(sender);
		
		my(InternetAddressKeeper.class).put(produceContact("Neide"), "200.201.202.203", 123);
		connectionFor("Neide");
		
		my(SignalUtils.class).waitForValue(sender.history(), "| <empty>,to:200.201.202.203,port:123");
		
		my(Clock.class).advanceTime(UdpConnectionManager.KEEP_ALIVE_PERIOD - 1);
		my(SignalUtils.class).waitForValue(sender.history(), "| <empty>,to:200.201.202.203,port:123");
		
		my(Clock.class).advanceTime(1);
		
		my(SignalUtils.class).waitForValue(sender.history(), "| <empty>,to:200.201.202.203,port:123| <empty>,to:200.201.202.203,port:123");
	}
	
	private DatagramPacket packetFrom(String nick, byte[] data, String ip, int port) throws Exception {
		produceContact(nick);
		
		my(ContactSeals.class).put(nick, new Seal(fill(42)));
		byte[] bytes = my(Lang.class).arrays().concat(fill(42), data);
		
		DatagramPacket ret = new DatagramPacket(bytes, bytes.length);
		ret.setAddress(InetAddress.getByName(ip));
		ret.setPort(port);
		
		return ret;
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

	
	private DatagramPacket packetFrom(String nick) throws Exception {
		return packetFrom(nick, new byte[0]);
	}


	private DatagramPacket packetFrom(String nick, byte[] data) throws Exception {
		return packetFrom(nick, data, "200.201.202.203", 234);
	}

}

