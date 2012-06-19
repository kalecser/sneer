package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Data;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Hail;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Stun;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.computers.udp.connections.UdpPacketType;
import sneer.bricks.network.computers.udp.holepuncher.client.StunClient;
import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.brickness.testsupport.Bind;
import basis.lang.Consumer;
import basis.util.concurrent.Latch;

public class UdpConnectionManagerTest extends BrickTestBase {

	
	private UdpConnectionManager subject = my(UdpConnectionManager.class);
	@Bind private final LoggingSender sender = new LoggingSender();
	@Bind private final StunClient stunClient = mock(StunClient.class);

		//To do:
	
		//HAIL SIGHTINGS:
		//First Hand Sightings - done
		//Second Hand Sightings: Stun response, Remote sighting
		//Sneer Host & Port Own Attributes
		
		//Forget old sightings
	

	@Test (timeout=2000)
	public void onSighting_ShouldHail() throws Exception {
		subject.handle(packetFrom("Neide", Data, "Hello".getBytes()));
		
		my(SignalUtils.class).waitForValue(sender.history(), "| hail 0,to:200.201.202.203,port:123");
	}
	
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
	public void sendData_ShouldUseReceivedHailSighting() throws Exception {
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
	public void onStunPacketReceived_ShouldDelegateToStunClient() throws Exception {
		final Latch latch = new Latch();
		checking(new Expectations(){{
			exactly(1).of(stunClient).handle(with(any(ByteBuffer.class))); will(new CustomAction("") { @Override public Object invoke(Invocation invocation) throws Throwable {
				latch.open(); return null;
			}});
		}});
		subject.handle(packetFrom("Neide", Stun, "Whatever".getBytes()));
		
		latch.waitTillOpen();
	}
	
	
	@Test(timeout = 2000)
	public void onIdleRecognizeNewSighting() throws Exception {
	
		subject.handle(hailPacketFrom("Neide"));
		
		ByteConnection connection = connectionFor("Neide");
		
		my(Clock.class).advanceTime(UdpConnectionManager.IDLE_PERIOD);
		my(SignalUtils.class).waitForValue(connection.isConnected(), false);
		
		subject.handle(packetFrom("Neide", Hail, asBytes(50), "100.101.102.103", 456));
		my(SignalUtils.class).waitForValue(connection.isConnected(), true);
		
		PacketScheduler scheduler = new PacketSchedulerMock("foo");
		connection.initCommunications(scheduler, my(Signals.class).sink());
		
		my(SignalUtils.class).waitForElement(sender.historySet(), "| foo,to:100.101.102.103,port:456");
	}
	

	@Test(timeout = 2000)
	public void keepAlive() {
		my(SightingKeeper.class).keep(produceContact("Neide"), new InetSocketAddress("200.201.202.203", 123));
		connectionFor("Neide");
		
		my(SignalUtils.class).waitForValue(sender.history(), "| hail 0,to:200.201.202.203,port:123");
		
		my(Clock.class).advanceTime(UdpConnectionManager.KEEP_ALIVE_PERIOD - 1);
		my(SignalUtils.class).waitForValue(sender.history(), "| hail 0,to:200.201.202.203,port:123");
		
		my(Clock.class).advanceTime(1);
		
		my(SignalUtils.class).waitForElement(sender.historySet(), "| hail 10000,to:200.201.202.203,port:123");
	}
	
	
	@Test(timeout = 2000)
	public void onSighting_ShouldUseFastestAddress() throws Exception {
		subject.handle(packetFrom("Neide", Hail, asBytes(41), "200.201.202.203", 123));
		subject.handle(packetFrom("Neide", Hail, asBytes(42), "192.168.10.10", 7777));
		
		PacketScheduler scheduler = new PacketSchedulerMock("foo");
		connectionFor("Neide").initCommunications(scheduler, my(Signals.class).sink());
		
		my(SignalUtils.class).waitForElement(sender.historySet(), "| foo,to:192.168.10.10,port:7777");
	}


	private byte[] asBytes(int value) {
		return ByteBuffer.allocate(8).putLong(value).array();
	}
	
	private void seeNeideIn(InetSocketAddress sighting) {
		my(SightingKeeper.class).keep(produceContact("Neide"), sighting);
	}
	
	private DatagramPacket packetFrom(String nick, UdpPacketType type, byte[] data, String ip, int port) throws Exception {
		produceContact(nick);
		my(ContactSeals.class).put(nick, new Seal(fill(42)));
		
		byte[] bytes = new byte[] { (byte)type.ordinal() };
		bytes = my(Lang.class).arrays().concat(bytes, fill(42));
		bytes = my(Lang.class).arrays().concat(bytes, data);
		
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
		return packetFrom(nick, Hail, asBytes(41));
	}


	private DatagramPacket packetFrom(String nick, UdpPacketType type, byte[] payload) throws Exception {
		return packetFrom(nick, type, payload, "200.201.202.203", 123);
	}

}

