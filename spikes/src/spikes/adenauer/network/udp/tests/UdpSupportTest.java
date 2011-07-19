package spikes.adenauer.network.udp.tests;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.util.concurrent.Latch;
import spikes.adenauer.network.udp.UdpAddressResolver;
import spikes.adenauer.network.udp.impl.UdpNetworkImpl;


public class UdpSupportTest extends BrickTestBase {

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
		Latch latch = new Latch();
		@SuppressWarnings("unused") WeakContract refToAvoidGc =
			subject2.packetsReceived().addPulseReceiver(latch);
		
		expectToResolve(seal2, new InetSocketAddress("127.0.0.1", 10002));
		subject1.send("anything".getBytes(), seal2);
		latch.waitTillOpen();
	}

	
	private void expectToResolve(final Seal seal, final SocketAddress address) {
		checking(new Expectations(){{
			exactly(1).of(resolver).addressFor(seal);will(returnValue(address));
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
