package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;
import static java.util.Arrays.copyOf;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Data;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Hail;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Stun;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Before;
import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.crypto.ecb.ECBCiphers;
import sneer.bricks.hardware.cpu.crypto.ecb.tests.NullECBCiphers;
import sneer.bricks.hardware.cpu.crypto.ecdh.ECDHKeyAgreement;
import sneer.bricks.hardware.cpu.crypto.ecdh.test.NullECDHKeyAgreemnent;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.keys.own.OwnKeys;
import sneer.bricks.identity.name.OwnName;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.contacts.ContactAddresses;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.Call;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.computers.udp.connections.UdpPacketType;
import sneer.bricks.network.computers.udp.holepuncher.client.StunClient;
import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.brickness.testsupport.Bind;
import basis.lang.Consumer;
import basis.lang.Producer;
import basis.util.concurrent.Latch;
import basis.util.concurrent.RefLatch;

public class UdpConnectionManagerTest extends BrickTestBase {

	private static final Charset UTF8 = Charset.forName("UTF-8");
	
	private final UdpConnectionManager subject = my(UdpConnectionManager.class);
	@Bind private final LoggingSender sender = new LoggingSender();
	@Bind private final StunClient stunClient = mock(StunClient.class);
	@Bind private final ECBCiphers ciphers = new NullECBCiphers();
	@Bind private final ECDHKeyAgreement keyAgreement = new NullECDHKeyAgreemnent();
			
	@Bind private final ContactAddresses contactAddresses = mock(ContactAddresses.class);
	private InetSocketAddress contactAddress = null;

	{
		checking(new Expectations(){{
			allowing(contactAddresses).given(with(any(Contact.class)));
			will(new CustomAction("") { @Override public Object invoke(Invocation invocation) throws Throwable {
				return my(Signals.class).constant(contactAddress);
			}});
		}});
	}
	
	
	@Before
	public void beforeUdpConnectionManagerTest() {
		my(OwnKeys.class).generateKeyPair(bytes("Seed"));
	}

	
	@Test (timeout=2000)
	public void onSighting_ShouldHail() throws Exception {
		setOwnName("Wesley");
		subject.handle(packetFrom("Neide", Data, bytes("Hello")));
		
		my(SignalUtils.class).waitForValue(sender.history(), "| Hail 0 PK:4889 Wesley,to:200.201.202.203,port:123");
	}
	
	
	@Test(timeout=2000)
	public void onFirstPacket_ShouldConnect() throws Exception {
		assertFalse(isConnected("Neide"));
		subject.handle(hailPacketFrom("Neide"));
		my(SignalUtils.class).waitForValue(connectionFor("Neide").isConnected(), true);
		assertFalse(isConnected("Maicon"));
	}
	
	
	@Test(timeout=2000)
	public void onUnknownCaller_ShouldNotify() throws Exception {
		RefLatch<Call> latch = new RefLatch<Call>();
		@SuppressWarnings("unused") WeakContract ref = subject.unknownCallers().addReceiver(latch);
		byte[] seal = fill(123);
		subject.handle(hailPacketFrom(seal, "Neide"));
		Call call = latch.waitAndGet();
		assertEquals(new Seal(seal), call.callerSeal());
		assertEquals("Neide", call.callerName());
	}

	
	@Test (timeout=2000)
	public void receiveData() throws Exception {
		subject.handle(hailPacketFrom("Neide"));
		
		final Latch latch = new Latch();
		connectionFor("Neide").initCommunications(new PacketProducerMock(), new Consumer<ByteBuffer>() { @Override public void consume(ByteBuffer packet) {
			byte[] bytes = new byte[packet.remaining()];
			packet.get(bytes);
			assertEquals("Hello", new String(bytes));
			latch.open();
		}});
		
		subject.handle(packetFrom("Neide", Data, bytes("Hello")));
		latch.waitTillOpen();
	}

	
	@Test(timeout=2000)
	public void sendData_ShouldUseReceivedHailSighting() throws Exception {
		subject.handle(hailPacketFrom("Neide"));
		
		Producer<ByteBuffer> producer = new PacketProducerMock("foo", "bar");
		connectionFor("Neide").initCommunications(producer, my(Signals.class).sink());

		my(SignalUtils.class).waitForElement(sender.historySet(), "| Data foo,to:200.201.202.203,port:123");
		my(SignalUtils.class).waitForElement(sender.historySet(), "| Data bar,to:200.201.202.203,port:123");
	}
	
	
	@Test
	public void invalidPacket() {
		subject.handle(new DatagramPacket(new byte[0], 0));
	}
	

	@Test(timeout=2000)
	public void onNotConnected_ShouldSendHailPacketsToSightings() {
		seeNeideIn(new InetSocketAddress("200.201.202.203", 1234));
		seeNeideIn(new InetSocketAddress("192.168.1.100", 7777));
		
		connectionFor("Neide");
		my(SignalUtils.class).waitForElement(sender.historySet(), "| Hail 0 PK:4889 ,to:200.201.202.203,port:1234");
		my(SignalUtils.class).waitForElement(sender.historySet(), "| Hail 0 PK:4889 ,to:192.168.1.100,port:7777");
	}
	

	@Test(timeout=2000)
	public void onNotConnected_ShouldSendHailPacketsToAddressOfContact() throws Exception {
		mockContactAddressAttributes("200.211.222.233", 1234);
		
		connectionFor("Neide");
		my(SignalUtils.class).waitForValue(sender.history(), "| Hail 0 PK:4889 ,to:200.211.222.233,port:1234");
	}

	
	@Test (timeout=2000)
	public void onStunPacketReceived_ShouldDelegateToStunClient() throws Exception {
		final Latch latch = new Latch();
		checking(new Expectations(){{
			exactly(1).of(stunClient).handle(with(any(ByteBuffer.class))); will(new CustomAction("") { @Override public Object invoke(Invocation invocation) throws Throwable {
				latch.open(); return null;
			}});
		}});
		subject.handle(packetFrom("Neide", Stun, bytes("Whatever")));
		
		latch.waitTillOpen();
	}
	
	
	@Test(timeout = 2000)
	public void onIdleRecognizeNewSighting() throws Exception {
		subject.handle(hailPacketFrom("Neide"));
		
		ByteConnection connection = connectionFor("Neide");
		
		my(Clock.class).advanceTime(UdpConnectionManager.IDLE_PERIOD);
		my(SignalUtils.class).waitForValue(connection.isConnected(), false);
		
		subject.handle(hailPacketFrom("Neide", asBytes(50), "100.101.102.103", 456));
		my(SignalUtils.class).waitForValue(connection.isConnected(), true);
		
		Producer<ByteBuffer> producer = new PacketProducerMock("foo");
		connection.initCommunications(producer, my(Signals.class).sink());
		
		my(SignalUtils.class).waitForElement(sender.historySet(), "| Data foo,to:100.101.102.103,port:456");
	}
	

	private DatagramPacket hailPacketFrom(String nick, byte[] payload, String ip, int port) throws Exception {
		ByteBuffer buf = ByteBuffer.allocate(UdpNetwork.MAX_PACKET_PAYLOAD_SIZE);
		buf.put(payload);
		buf.put(new byte[OwnKeys.PUBLIC_KEY_SIZE_IN_BYTES]);
		buf.flip();
		
		return packetFrom(nick, Hail, copyOf(buf.array(), buf.limit()), ip, port);
	}


	@Test(timeout = 2000)
	public void keepAlive() {
		my(SightingKeeper.class).keep(produceContact("Neide"), new InetSocketAddress("200.201.202.203", 123));
		connectionFor("Neide");
		
		my(SignalUtils.class).waitForValue(sender.history(), "| Hail 0 PK:4889 ,to:200.201.202.203,port:123");
		
		my(Clock.class).advanceTime(UdpConnectionManager.KEEP_ALIVE_PERIOD - 1);
		my(SignalUtils.class).waitForValue(sender.history(), "| Hail 0 PK:4889 ,to:200.201.202.203,port:123");
		
		my(Clock.class).advanceTime(1);
		
		my(SignalUtils.class).waitForElement(sender.historySet(), "| Hail 10000 PK:4889 ,to:200.201.202.203,port:123");
	}
	
	
	@Test(timeout = 2000)
	public void onSighting_ShouldUseFastestAddress() throws Exception {
		subject.handle(hailPacketFrom("Neide", asBytes(41), "200.201.202.203", 123));
		subject.handle(hailPacketFrom("Neide", asBytes(42), "192.168.10.10", 7777));
		
		Producer<ByteBuffer> producer = new PacketProducerMock("foo");
		connectionFor("Neide").initCommunications(producer, my(Signals.class).sink());
		
		my(SignalUtils.class).waitForElement(sender.historySet(), "| Data foo,to:192.168.10.10,port:7777");
	}
	
	
	private void mockContactAddressAttributes(final String host, final int port) {
		contactAddress = new InetSocketAddress(host, port);
	}
	
	
	private byte[] bytes(String message) {
		return message.getBytes(UTF8);
	}
	

	private byte[] asBytes(int value) {
		return ByteBuffer.allocate(8).putLong(value).array();
	}
	
	
	private void seeNeideIn(InetSocketAddress sighting) {
		my(SightingKeeper.class).keep(produceContact("Neide"), sighting);
	}
	
	
	private DatagramPacket hailPacketFrom(String nick) throws Exception {
		return hailPacketFrom(nick, asBytes(41), "200.201.202.203", 123);
	}

	
	private DatagramPacket hailPacketFrom(byte[] seal, String senderName) throws Exception {
		ByteBuffer data = ByteBuffer.allocate(UdpNetwork.MAX_PACKET_PAYLOAD_SIZE);
		data.putLong(41);
		
		byte[] key = new byte[OwnKeys.PUBLIC_KEY_SIZE_IN_BYTES];
		data.put(key);
		data.put(bytes(senderName));
		data.flip();
		
		return packetFrom(Hail, seal, copyOf(data.array(), data.limit()), "200.42.0.35", 4567);
	}
	
	
	private DatagramPacket packetFrom(String nick, UdpPacketType type, byte[] payload) throws Exception {
		return packetFrom(nick, type, payload, "200.201.202.203", 123);
	}

	
	private DatagramPacket packetFrom(String nick, UdpPacketType type, byte[] data, String ip, int port) throws Exception {
		produceContact(nick);
		byte[] sealBytes = fill(42);
		my(ContactSeals.class).put(nick, new Seal(sealBytes));
		
		return packetFrom(type, sealBytes, data, ip, port);
	}

	
	private DatagramPacket packetFrom(UdpPacketType type, byte[] seal, byte[] data, String ip, int port) throws UnknownHostException {
		byte[] bytes = new byte[] { (byte)type.ordinal() };
		bytes = my(Lang.class).arrays().concat(bytes, seal);
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

	
	private void setOwnName(String name) {
		my(Attributes.class).myAttributeSetter(OwnName.class).consume(name);
	}

}

