package sneer.bricks.network.computers.udp.holepuncher.client.tests;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.own.OwnIps;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.udp.connections.UdpPacketType;
import sneer.bricks.network.computers.udp.holepuncher.client.StunClient;
import sneer.bricks.network.computers.udp.holepuncher.server.StunServer;
import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.brickness.testsupport.Bind;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.environments.ProxyInEnvironment;
import basis.lang.ByRef;
import basis.lang.ClosureX;
import basis.lang.Consumer;


public class StunClientTest extends BrickTestBase {

	@Bind private final OwnIps ownIpsMock = mock(OwnIps.class);
	private final SetRegister<InetAddress> ownIps = my(CollectionSignals.class).newSetRegister();
	private StunClient client1;
	private StunClient client2;
	

	{
		checking(new Expectations() {{
			allowing(ownIpsMock).get(); will(returnValue(ownIps.output())); 
		}});
	}
	

	@Test(timeout = 2000)
	public void ownIpsChange() throws Exception {
		mockOwnIps("10.42.10.1", "10.42.10.2");
		setOwnPort(1234);
		
		connect(my(StunClient.class), my(StunServer.class));
		
		final Seal seal = my(OwnSeal.class).get().currentValue();
		
		Environment remote = newTestEnvironment(my(StunServer.class));
		Environments.runWith(remote, new ClosureX<Exception>() { @Override public void run() throws Exception {
			Contact neide = my(Contacts.class).produceContact("Neide");
			my(ContactSeals.class).put("Neide", seal);
			
			connect(my(StunClient.class), my(StunServer.class));
			
			waitForSighting(neide, "10.42.10.1", 1234);
			waitForSighting(neide, "10.42.10.2", 1234);
		}});
		
		//Test that requests are sent periodically:
		
		mockOwnIps("10.42.10.50");

		final ByRef<Seal> remoteSeal = ByRef.newInstance();
		Environments.runWith(remote, new ClosureX<Exception>() { @Override public void run() throws Exception {
			my(Clock.class).advanceTime(StunClient.REQUEST_PERIOD);
			
			Contact neide = my(Contacts.class).contactGiven("Neide");
			waitForSighting(neide, "10.42.10.50", 1234);
			
			remoteSeal.value = my(OwnSeal.class).get().currentValue();
		}});
		
		//Test that known peers are notified:

		my(Contacts.class).produceContact("Remote");
		my(ContactSeals.class).put("Remote", remoteSeal.value);
		mockOwnIps("10.42.10.100");
		Environments.runWith(remote, new ClosureX<Exception>() { @Override public void run() throws Exception {
			Contact neide = my(Contacts.class).contactGiven("Neide");
			waitForSighting(neide, "10.42.10.100", 1234);
		}});
	}
	

	private void connect(StunClient client, final StunServer server) {
		final StunClient clientInEnvironment = ProxyInEnvironment.newInstance(client);
		if (client1 == null) {
			client1 = clientInEnvironment;
		} else {
			client2 = clientInEnvironment;
		}
		
		client.initSender(new Consumer<DatagramPacket>() { @Override public void consume(DatagramPacket request) {
			assertEquals("dynamic.sneer.me", request.getAddress().getHostName());
			assertEquals(7777, request.getPort());
			
			DatagramPacket[] replies = server.repliesFor(request);
			if (replies.length == 0) return;
			
			assertEquals(2, replies.length);
			clientInEnvironment.handle(asBuffer(replies[0]));
			if (other(clientInEnvironment) != null)
				other(clientInEnvironment).handle(asBuffer(replies[1]));
		}});
	}
	
	
	private StunClient other(StunClient client) {
		return client == client1 ? client2 : client1;
	}


	private void waitForSighting(Contact contact, String ip, int port) {
		SetSignal<InetSocketAddress> sightings = my(SightingKeeper.class).sightingsOf(contact);
		my(SignalUtils.class).waitForElement(sightings, new InetSocketAddress(ip, port));
	}

	
	private ByteBuffer asBuffer(DatagramPacket reply) {
		ByteBuffer ret = ByteBuffer.wrap(reply.getData(), 0, reply.getLength());
		assertEquals(UdpPacketType.Stun.ordinal(), ret.get());
		return ret;
	}
	
	
	private void mockOwnIps(String... ips) throws UnknownHostException {
		ownIps.clear();
		for (int i = 0; i < ips.length; i++)
			ownIps.add(InetAddress.getByName(ips[i]));
	}

	
	private void setOwnPort(int value) {
		my(Attributes.class).myAttributeSetter(OwnPort.class).consume(value);
	}
	
	
}
