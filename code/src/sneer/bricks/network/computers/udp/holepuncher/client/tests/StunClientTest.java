package sneer.bricks.network.computers.udp.holepuncher.client.tests;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.network.computers.addresses.own.OwnIps;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.udp.holepuncher.client.StunClient;
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
	
	@Ignore @Test public void noOwnIp() {}
	@Ignore @Test public void ownIpsChange() {}
	
	
	@Test//(timeout = 2000)
	public void stunRequest() throws Exception {
		mockOwnIps("10.42.10.1", "10.42.10.27");
		setOwnPort(1234);
		
		final Latch latch = new Latch();
		subject.initSender(new Consumer<DatagramPacket>() {  @Override public void consume(DatagramPacket packet) {
			assertEquals("dynamic.sneer.me", packet.getAddress().getHostName());
			assertEquals(7777, packet.getPort());
		//	DatagramPacket[] replies = my(StunServer.class).repliesFor(packet);
		//	subject.handle(asBuffer(replies));
			latch.open();
		}});
		
		latch.waitTillOpen();
	}

	
//	private ByteBuffer asBuffer(DatagramPacket[] replies) {
//		return ByteBuffer.wrap(replies[0].getData());
//	}
	
	private void mockOwnIps(String... ips) throws UnknownHostException {
		final SetRegister<InetAddress> ownIp = my(CollectionSignals.class).newSetRegister();
		
		for (int i = 0; i < ips.length; i++)
			ownIp.add(InetAddress.getByName(ips[i]));
		
		checking(new Expectations() {{
			oneOf(ownIps).get(); will(returnValue(ownIp.output())); 
		}});
	}

	private void setOwnPort(int value) {
		my(Attributes.class).myAttributeSetter(OwnPort.class).consume(value);
	}
	
	
}
