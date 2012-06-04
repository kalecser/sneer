package sneer.bricks.network.computers.udp.holepuncher.client.tests;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.network.computers.addresses.own.OwnIps;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.udp.holepuncher.client.StunClient;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunRequest;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.brickness.testsupport.Bind;
import basis.lang.Consumer;
import basis.util.concurrent.Latch;


public class StunClientTest extends BrickTestBase {

	private final StunClient subject = my(StunClient.class);
	
	@Bind private final OwnIps ownIps = mock(OwnIps.class);
	
	
	@Ignore @Test public void multipleOwnIps() {}
	@Ignore @Test public void noOwnIp() {}
	@Ignore @Test public void ownIpsChange() {}
	
	
	@Test(timeout = 2000)
	public void stunRequest() throws Exception {
		mockOwnIp("10.42.10.1");
		setOwnPort(1234);
		
		final Latch latch = new Latch();
		subject.initSender(new Consumer<DatagramPacket>() {  @Override public void consume(DatagramPacket packet) {
			assertEquals("dynamic.sneer.me", packet
					.getAddress()
					.getHostName());
			assertEquals(7777, packet.getPort());
			StunRequest request = my(StunProtocol.class).unmarshalRequest(packet.getData(), packet.getLength());
			assertArrayEquals(ownSeal(),request._ownSeal);
			assertEquals("10.42.10.1", request._localIp.getHostAddress());
			assertEquals(1234, request._localPort);
			assertNull(request._peerToFind);
			latch.open();
		}});
		latch.waitTillOpen();
	}

	
	private void mockOwnIp(String ip) throws UnknownHostException {
		final SetRegister<Object> ownIp = my(CollectionSignals.class).newSetRegister();
		ownIp.add(InetAddress.getByName(ip));
		checking(new Expectations() {{
			oneOf(ownIps).get(); will(returnValue(ownIp.output())); 
		}});
	}

	private void setOwnPort(int value) {
		my(Attributes.class).myAttributeSetter(OwnPort.class).consume(value);
	}
	
	private byte[] ownSeal() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}
}
