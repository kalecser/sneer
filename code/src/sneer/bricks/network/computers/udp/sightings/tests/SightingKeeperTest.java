package sneer.bricks.network.computers.udp.sightings.tests;

import static basis.environments.Environments.my;

import java.net.InetSocketAddress;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.computers.udp.sightings.UdpSighting;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.lang.exceptions.Refusal;
import basis.util.concurrent.RefLatch;

public class SightingKeeperTest extends BrickTestBase {
	
	private final SightingKeeper subject = my(SightingKeeper.class);

	@Ignore
	@Test(timeout=2000)
	public void onSighting_ShouldPublishTuple() throws Refusal {
		Contact neide = my(Contacts.class).produceContact("Neide");
		my(ContactSeals.class).put("Neide", new Seal(new byte[] { 42 }));
		
		InetSocketAddress sighting = new InetSocketAddress("201.202.203.231", 2021);
		subject.keep(neide, sighting);
		
		RefLatch<UdpSighting> latch = new RefLatch<>();
		@SuppressWarnings("unused") WeakContract ref = my(TupleSpace.class).addSubscription(UdpSighting.class, latch);
		UdpSighting tuple = latch.waitAndGet();
		
		assertEquals(42, tuple.peerSeal.bytes.get(0));
		assertEquals("201.202.203.231", tuple.host);
		assertEquals(2021, tuple.port);
	}

}
