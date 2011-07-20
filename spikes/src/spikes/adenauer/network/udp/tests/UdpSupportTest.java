package spikes.adenauer.network.udp.tests;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.lang.Consumer;
import sneer.foundation.util.concurrent.Latch;
import spikes.adenauer.network.Network.Packet;
import spikes.adenauer.network.udp.UdpAddressResolver;
import spikes.adenauer.network.udp.impl.UdpNetworkImpl;


public class UdpSupportTest extends BrickTestBase {

	private final SocketAddress address1 = new InetSocketAddress("127.0.0.1", 10001);
	private final SocketAddress address2 = new InetSocketAddress("127.0.0.1", 10002);
	private final Seal seal1 = new Seal(new byte[] {1, 1, 1});
	private final Seal seal2 = new Seal(new byte[] {2, 2, 2});
	private final UdpNetworkImpl subject1 = createUdpNetwork(10001);
	private final UdpNetworkImpl subject2 = createUdpNetwork(10002);

	@Bind private final UdpAddressResolver resolver = mock(UdpAddressResolver.class);

	
	@Test
	public void packetsToUnknownDestinationsAreIgnored() {
		final Seal unknownSeal = new Seal(new byte[] {-1, -1, -1});
		expectToResolve(unknownSeal, null);
		subject1.send("anything".getBytes(), unknownSeal);
	}


	@Test(timeout = 2000)
	public void packetSending() {
		final Latch latch = new Latch();
		@SuppressWarnings("unused") WeakContract packetsRefToAvoidGc = 
			subject2.packetsReceived().addReceiver(new Consumer<Packet>() { @Override public void consume(Packet packet) {
				String received = new String(packet.data());
				assertEquals("hello", received);
				assertEquals(seal1, packet.sender());
				latch.open();
			}});

		sendData("hello".getBytes());
		latch.waitTillOpen();
	}
	
	
	@Test(timeout = 2000)
	public void receivingPacketMakesSenderBecomeOnline() {
		final Latch latch = new Latch();
		@SuppressWarnings("unused") WeakContract peersOnlineRefToAvoidGc = 
			subject2.peersOnline().addReceiver(new Consumer<CollectionChange<Seal>>() { @Override public void consume(CollectionChange<Seal> change) {
				if (change.elementsAdded().contains(seal1))  
					latch.open(); 
			}});
		
		sendData("hello".getBytes());
		latch.waitTillOpen();
	}
	
	
	private void sendData(byte[] data) {
		expectToResolve(seal2, address2);
		expectToResolve(address1, seal1);
		subject1.send(data, seal2);
	}

	
	private void expectToResolve(final Seal seal, final SocketAddress address) {
		checking(new Expectations(){{
			exactly(1).of(resolver).addressFor(seal);will(returnValue(address));
		}});
	}


	private void expectToResolve(final SocketAddress address, final Seal seal) {
		checking(new Expectations(){{
			exactly(1).of(resolver).sealFor(address);will(returnValue(seal));
		}});
	}

	private UdpNetworkImpl createUdpNetwork(int port) {
		try {
			return new UdpNetworkImpl(port);
		} catch (SocketException e) {
			throw new IllegalStateException(e);
		}
	}

	
	@After
	public void afterUdpTest() {
		subject1.close();
		subject2.close();
	}
	
}
