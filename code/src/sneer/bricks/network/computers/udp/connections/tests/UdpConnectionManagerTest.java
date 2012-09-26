package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;
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
import basis.lang.exceptions.Refusal;
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
		subject.handle(dataFrom("Neide", bytes("Hello")));
		
		my(SignalUtils.class).waitForValue(sender.history(), "| Hail 0 PK:4889 Wesley,to:200.201.202.203,port:123");
	}
	
	
	@Test(timeout=2000)
	public void onFirstPacket_ShouldConnect() throws Exception {
		assertFalse(isConnected("Neide"));
		subject.handle(hailFrom("Neide"));
		my(SignalUtils.class).waitForValue(connectionFor("Neide").isConnected(), true);
		assertFalse(isConnected("Maicon"));
	}
	
	
	@Test(timeout=2000)
	public void onUnknownCaller_ShouldNotify() throws Exception {
		RefLatch<Call> latch = new RefLatch<Call>();
		@SuppressWarnings("unused") WeakContract ref = subject.unknownCallers().addReceiver(latch);
		byte[] seal = seal(123);
		subject.handle(unknownHailFrom("Neide", seal));
		Call call = latch.waitAndGet();
		assertEquals(new Seal(seal), call.callerSeal());
		assertEquals("Neide", call.callerName());
	}

	
	@Test (timeout=2000)
	public void receiveData() throws Exception {
		subject.handle(hailFrom("Neide"));
		
		final Latch latch = new Latch();
		connectionFor("Neide").initCommunications(new PacketProducerMock(), new Consumer<ByteBuffer>() { @Override public void consume(ByteBuffer packet) {
			byte[] bytes = new byte[packet.remaining()];
			packet.get(bytes);
			assertEquals("Hello", new String(bytes));
			latch.open();
		}});
		
		subject.handle(dataFrom("Neide", bytes("Hello")));
		latch.waitTillOpen();
	}

	
	@Test(timeout=2000)
	public void sendData_ShouldUseReceivedHailSighting() throws Exception {
		subject.handle(hailFrom("Neide"));
		
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
		subject.handle(stunFrom("Neide"));
		
		latch.waitTillOpen();
	}
	
	
	@Test(timeout = 2000)
	public void onIdleRecognizeNewSighting() throws Exception {
		subject.handle(hailFrom("Neide"));
		
		ByteConnection connection = connectionFor("Neide");
		
		my(Clock.class).advanceTime(UdpConnectionManager.IDLE_PERIOD);
		my(SignalUtils.class).waitForValue(connection.isConnected(), false);
		
		subject.handle(hailFrom("Neide", 50, "100.101.102.103", 456));
		my(SignalUtils.class).waitForValue(connection.isConnected(), true);
		
		Producer<ByteBuffer> producer = new PacketProducerMock("foo");
		connection.initCommunications(producer, my(Signals.class).sink());
		
		my(SignalUtils.class).waitForElement(sender.historySet(), "| Data foo,to:100.101.102.103,port:456");
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
		subject.handle(hailFrom("Neide", 41, "200.201.202.203", 123));
		subject.handle(hailFrom("Neide", 42, "192.168.10.10", 7777));
		
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
	

	private void seeNeideIn(InetSocketAddress sighting) {
		my(SightingKeeper.class).keep(produceContact("Neide"), sighting);
	}
	
	
	private DatagramPacket hailFrom(String nick) throws Exception {
		return hailFrom(nick, produceSeal(nick, 42), 0, "200.201.202.203", 123);
	}


	private DatagramPacket unknownHailFrom(String nick, byte[] seal) throws Exception {
		return hailFrom(nick, seal, 0, "200.42.0.35", 4557);
	}
	
	
	private DatagramPacket hailFrom(String nick, int timestamp, String ip, int port) throws Exception {
		return hailFrom(nick, produceSeal(nick, 42), timestamp, ip, port);
	}
	
	
	private DatagramPacket hailFrom(String nick, byte[] seal, int timestamp, String ip, int port) throws UnknownHostException {
		byte[] publicKey = my(OwnKeys.class).ownPublicKey().currentValue().getEncoded();
		
		ByteBuffer buf = preparePacket(Hail, seal);
		buf.putLong(timestamp);
		buf.put(publicKey);
		buf.put(bytes(nick));
		buf.flip();
		
		return datagramPacketFor(buf, ip, port);
	}
	
	
	private DatagramPacket dataFrom(String nick, byte[] payload) throws Exception {
		ByteBuffer buf = preparePacket(Data, produceSeal(nick, 42));
		buf.put(payload);
		buf.flip();
		
		return datagramPacketFor(buf, "200.201.202.203", 123);
	}


	private DatagramPacket stunFrom(String nick) throws UnknownHostException, Refusal {
		byte[] seal = produceSeal(nick, 42);
		
		ByteBuffer buf = ByteBuffer.allocate(UdpNetwork.MAX_PACKET_PAYLOAD_SIZE);
		buf.put((byte)Stun.ordinal());
		buf.put(seal);
		buf.flip();
		
		return datagramPacketFor(buf, "200.201.202.203", 123);
	}


	private ByteBuffer preparePacket(UdpPacketType type, byte[] seal) {
		ByteBuffer buf = ByteBuffer.allocate(UdpNetwork.MAX_PACKET_PAYLOAD_SIZE);
		buf.put((byte)type.ordinal());
		buf.put(seal);
		
		return buf;
	}

	
	private DatagramPacket datagramPacketFor(ByteBuffer buf, String ip, int port) throws UnknownHostException {
		DatagramPacket ret = new DatagramPacket(buf.array(), buf.limit());
		ret.setAddress(InetAddress.getByName(ip));
		ret.setPort(port);
		
		return ret;
	}

	
	private byte[] seal(int id) {
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
	
	
	private byte[] produceSeal(String nick, int seal) throws Refusal {
		produceContact(nick);
		byte[] ret = seal(seal);
		my(ContactSeals.class).put(nick, new Seal(ret));
		
		return ret;
	}

	
	private void setOwnName(String name) {
		my(Attributes.class).myAttributeSetter(OwnName.class).consume(name);
	}
	
}

