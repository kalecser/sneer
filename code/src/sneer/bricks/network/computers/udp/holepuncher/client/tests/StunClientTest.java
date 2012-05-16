package sneer.bricks.network.computers.udp.holepuncher.client.tests;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.network.computers.udp.holepuncher.client.StunClient;
import sneer.bricks.network.computers.udp.holepuncher.impl.StunRequest;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.lang.Consumer;
import basis.util.concurrent.Latch;


public class StunClientTest extends BrickTestBase {

	private final StunClient subject = my(StunClient.class);
	
	@Ignore
	@Test(timeout = 2000)
	public void stunRequest() {
		final Latch latch = new Latch();
		
		subject.initSender(new Consumer<DatagramPacket>() {  @Override public void consume(DatagramPacket packet) {
			assertEquals("dynamic.sneer.me", packet.getAddress().getHostName());
			assertEquals(7777, packet.getPort());
			StunRequest request = StunRequest.umarshalFrom(packet.getData(), packet.getLength());
			assertArrayEquals(ownSeal(),request._ownSeal);
			assertEquals("10.42.10.1", request._localIp.getHostAddress());
			assertEquals(1234, request._localPort);
			assertNull(request._peerToFind);
			latch.open();
		}});
		latch.waitTillOpen();
		
	}
	
	private byte[] ownSeal() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}
}
