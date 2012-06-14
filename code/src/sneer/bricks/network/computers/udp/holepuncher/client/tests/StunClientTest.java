package sneer.bricks.network.computers.udp.holepuncher.client.tests;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.own.OwnIps;
import sneer.bricks.network.computers.ports.OwnPort;
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
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.brickness.testsupport.Bind;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.ByRef;
import basis.lang.ClosureX;
import basis.lang.Consumer;
import basis.util.concurrent.Latch;


public class StunClientTest extends BrickTestBase {

	private final SetRegister<InetAddress> myIps = my(CollectionSignals.class).newSetRegister();
	@Bind private final OwnIps ownIps = mock(OwnIps.class);
	
	{
		checking(new Expectations() {{
			allowing(ownIps).get(); will(returnValue(myIps.output())); 
		}});
	}
	
	@Ignore @Test public void noOwnIp() {}
	
	@Test(timeout = 2000)
	public void ownIpsChange() throws Exception {
		mockOwnIps("10.42.10.1");
		setOwnPort(1234);
		
		communicate(my(StunClient.class), my(StunServer.class));
		
		Environment remote = newTestEnvironment(ownIps, my(StunServer.class));
		
		final Seal seal = my(OwnSeal.class).get().currentValue();
		
		Environments.runWith(remote, new ClosureX<Exception>() { @Override public void run() throws Exception {
			my(FolderConfig.class).storageFolder().set(newTmpFile("environment2"));
			
			final Contact neide = my(Contacts.class).produceContact("Neide");
			my(ContactSeals.class).put("Neide", seal);
			
			communicate(my(StunClient.class), my(StunServer.class));
			
			SetSignal<InetSocketAddress> sightings = my(SightingKeeper.class).sightingsOf(neide);
			my(SignalUtils.class).waitForElement(sightings, new InetSocketAddress("10.42.10.1", 1234));
		}});
		
		mockOwnIps("10.42.10.50");
		
		Environments.runWith(remote, new ClosureX<Exception>() { @Override public void run() throws Exception {
			communicate(my(StunClient.class), my(StunServer.class));
			
			InetSocketAddress sighting1 = new InetSocketAddress("10.42.10.1", 1234);
			InetSocketAddress sighting2 = new InetSocketAddress("10.42.10.50", 1234);
			
			final Contact neide = my(Contacts.class).contactGiven("Neide");
			
			SetSignal<InetSocketAddress> sightings = my(SightingKeeper.class).sightingsOf(neide);
			my(SignalUtils.class).waitForElement(sightings, sighting1);
			my(SignalUtils.class).waitForElement(sightings, sighting2);
			
			my(Threads.class).crashAllThreads();
		}});
	}
	
	@Test(timeout = 2000)
	public void stunRequest() throws Exception {
		mockOwnIps("10.42.10.1", "10.42.10.27");
		setOwnPort(1234);
		
		communicate(my(StunClient.class), my(StunServer.class));
		
		final Seal seal = my(OwnSeal.class).get().currentValue();
		
		Environments.runWith(newTestEnvironment(ownIps, my(StunServer.class)), new ClosureX<Exception>() { @Override public void run() throws Exception {
			my(FolderConfig.class).storageFolder().set(newTmpFile("environment2"));
			
			final Contact neide = my(Contacts.class).produceContact("Neide");
			my(ContactSeals.class).put("Neide", seal);
			
			mockOwnIps("192.168.10.1", "192.168.10.27");
			setOwnPort(3412);
			
			communicate(my(StunClient.class), my(StunServer.class));
			
			InetSocketAddress sighting1 = new InetSocketAddress("10.42.10.1", 1234);
			InetSocketAddress sighting2 = new InetSocketAddress("10.42.10.27", 1234);
			
			SetSignal<InetSocketAddress> sightings = my(SightingKeeper.class).sightingsOf(neide);
			my(SignalUtils.class).waitForElement(sightings, sighting1);
			my(SignalUtils.class).waitForElement(sightings, sighting2);
			
			my(Threads.class).crashAllThreads();
		}});
	}
	
	private void communicate(final StunClient client, final StunServer server) {
		final ByRef<DatagramPacket[]> replies = ByRef.newInstance();
		final Latch latch = new Latch();
		client.initSender(new Consumer<DatagramPacket>() {  @Override public void consume(DatagramPacket packet) {
			assertEquals("dynamic.sneer.me", packet.getAddress().getHostName());
			assertEquals(7777, packet.getPort());
			replies.value = server.repliesFor(packet);
			latch.open();
		}});
		latch.waitTillOpen();

		if (replies.value.length == 0) return;
		
		assertEquals(2, replies.value.length);
		my(StunClient.class).handle(asBuffer(replies.value[0]));
	}

	
	private ByteBuffer asBuffer(DatagramPacket packet) {
		return ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
	}
	
	private void mockOwnIps(String... ips) throws UnknownHostException {
		myIps.clear();
		for (int i = 0; i < ips.length; i++)
			myIps.add(InetAddress.getByName(ips[i]));
	}

	private void setOwnPort(int value) {
		my(Attributes.class).myAttributeSetter(OwnPort.class).consume(value);
	}
	
	
}
