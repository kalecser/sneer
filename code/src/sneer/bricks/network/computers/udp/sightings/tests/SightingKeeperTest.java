package sneer.bricks.network.computers.udp.sightings.tests;

import static basis.environments.Environments.my;
import static basis.environments.Environments.runWith;

import java.net.InetSocketAddress;

import org.junit.Test;

import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.lang.Closure;
import basis.lang.exceptions.Refusal;

public class SightingKeeperTest extends BrickTestWithTuples {
	
	private final SightingKeeper subject = my(SightingKeeper.class);

	@Test(timeout=2000)
	public void onSighting_ShouldPublishTuple() throws Refusal {
		Contact neide = my(Contacts.class).produceContact("Neide");
		my(ContactSeals.class).put("Neide", new Seal(new byte[] { 42 }));
		
		runWith(remote(), new Closure() { @Override public void run() {
			Contact neide = my(Contacts.class).contactGiven("Neide");
			InetSocketAddress sighting = new InetSocketAddress("201.202.203.231", 2021);
			subject.keep(neide, sighting);
		}});
		
		waitForAllDispatchingToFinish();
		
		SetSignal<InetSocketAddress> sightings = subject.sightingsOf(neide);
		assertSame(1, sightings.size().currentValue());
		
		InetSocketAddress addr = sightings.currentElements().iterator().next();
		assertEquals("201.202.203.231", addr.getHostString());
		assertEquals(2021, addr.getPort());
	}

}
