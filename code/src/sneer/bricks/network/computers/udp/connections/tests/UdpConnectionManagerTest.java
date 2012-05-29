package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.brickness.testsupport.Bind;
import basis.lang.Consumer;
import basis.util.concurrent.Latch;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager.PacketType;
import static sneer.bricks.network.computers.udp.connections.UdpConnectionManager.PacketType.*;

public class UdpConnectionManagerTest extends BrickTestBase {

	
	private UdpConnectionManager subject = my(UdpConnectionManager.class);
	@Bind private final LoggingSender sender = new LoggingSender();

	
		//DADOS:
		//Fastest received hail return address

		//HAIL SIGHTINGS:
		//First Hand Sightings
		//Second Hand Sightings: Stun response, Remote sighting
		//Sneer Ip & Port Own Attributes
	

	@Test(timeout=2000)
	public void onFirstPacket_ShouldConnect() throws Exception {
		assertFalse(isConnected("Neide"));
		subject.handle(hailPacketFrom("Neide"));
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
		
		subject.handle(packetFrom("Neide", Data, "Hello".getBytes()));
		latch.waitTillOpen();
	}

	
	@Test(timeout=2000)
	public void sendData() throws Exception {
		subject.handle(hailPacketFrom("Neide"));
		
		PacketScheduler scheduler = new PacketSchedulerMock("foo", "bar");
		connectionFor("Neide").initCommunications(scheduler, my(Signals.class).sink());

		my(SignalUtils.class).waitForElement(sender.historySet(), "| foo,to:200.201.202.203,port:123");
		my(SignalUtils.class).waitForElement(sender.historySet(), "| bar,to:200.201.202.203,port:123");
	}
	
	
	@Test
	public void invalidPacket() {
		subject.handle(new DatagramPacket(new byte[0], 0));
	}
	

	@Test(timeout=2000)
	public void onNotConnected_ShouldSendHailingPacketsEverySoOften() {
		seeNeideIn(new InetSocketAddress("200.201.202.203", 123));
		seeNeideIn(new InetSocketAddress("192.168.1.100", 7777));
		
		connectionFor("Neide");
		my(SignalUtils.class).waitForElement(sender.historySet(), "| hail 0,to:200.201.202.203,port:123");
		my(SignalUtils.class).waitForElement(sender.historySet(), "| hail 0,to:192.168.1.100,port:7777");
	}
	
	@Test (timeout=2000)
	public void onSighting_ShouldHail() throws Exception {
		subject.handle(packetFrom("Neide", Data, "Hello".getBytes()));
		
		my(SignalUtils.class).waitForValue(sender.history(), "| hail 1,to:200.201.202.203,port:123");
	}
	

	@Test(timeout=2000)
	public void onReceivePacket_ShouldUseInternetAddress() throws Exception {
		subject.handle(hailPacketFrom("Neide"));
		
		PacketScheduler scheduler = new PacketSchedulerMock("foo");
		connectionFor("Neide").initCommunications(scheduler, my(Signals.class).sink());
		
		my(SignalUtils.class).waitForElement(sender.historySet(), "| foo,to:200.201.202.203,port:123");
	}
	
	
	@Test(timeout = 2000)
	public void onIdleRecognizeNewSighting() throws Exception {
	
		subject.handle(hailPacketFrom("Neide"));
		
		ByteConnection connection = connectionFor("Neide");
		
		my(Clock.class).advanceTime(UdpConnectionManager.IDLE_PERIOD);
		my(SignalUtils.class).waitForValue(connection.isConnected(), false);
		
		subject.handle(packetFrom("Neide", Hail, new byte[] { 50 }, "100.101.102.103", 456));
		my(SignalUtils.class).waitForValue(connection.isConnected(), true);
		
		PacketScheduler scheduler = new PacketSchedulerMock("foo");
		connection.initCommunications(scheduler, my(Signals.class).sink());
		
		my(SignalUtils.class).waitForElement(sender.historySet(), "| foo,to:100.101.102.103,port:456");
	}
	
	@Ignore
	@Test(timeout = 2000)
	public void keepAlive() {
		my(SightingKeeper.class).keep(produceContact("Neide"), new InetSocketAddress("200.201.202.203", 123));
		connectionFor("Neide");
		
		my(SignalUtils.class).waitForValue(sender.history(), "| <empty>,to:200.201.202.203,port:123");
		
		my(Clock.class).advanceTime(UdpConnectionManager.KEEP_ALIVE_PERIOD - 1);
		my(SignalUtils.class).waitForValue(sender.history(), "| <empty>,to:200.201.202.203,port:123");
		
		my(Clock.class).advanceTime(1);
		
		my(SignalUtils.class).waitForValue(sender.history(), "| <empty>,to:200.201.202.203,port:123| <empty>,to:200.201.202.203,port:123");
	}
	
	@Ignore
	@Test(timeout = 2000)
	public void onSighting_ShouldUseFastestAddress() throws Exception {
		subject.handle(packetFrom("Neide", new byte[] { 0, 41 }, "200.201.202.203", 123));
		subject.handle(packetFrom("Neide", new byte[] { 0, 42 }, "192.168.10.10", 7777));
		
		PacketScheduler scheduler = new PacketSchedulerMock("foo");
		connectionFor("Neide").initCommunications(scheduler, my(Signals.class).sink());
		
		my(SignalUtils.class).waitForElement(sender.historySet(), "| foo,to:192.168.10.10,port:7777");
	}
	
	private void seeNeideIn(InetSocketAddress sighting) {
		my(SightingKeeper.class).keep(produceContact("Neide"), sighting);
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

	
	private DatagramPacket hailPacketFrom(String nick) throws Exception {
		return packetFrom(nick, Hail, new byte[] { 41 });
	}


	private DatagramPacket packetFrom(String nick, PacketType type, byte[] payload) throws Exception {
		return packetFrom(nick, type, payload, "200.201.202.203", 123);
	}


	private DatagramPacket packetFrom(String nick, PacketType type,
			byte[] payload, String ip, int port) throws Exception {
		byte[] typeByte = new byte[] { (byte)type.ordinal() };
		byte[] bytes = my(Lang.class).arrays().concat(typeByte, payload);
		return packetFrom(nick, bytes, ip, port);
	}

}

