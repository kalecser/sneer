package sneer.bricks.network.computers.addresses.sighting.tests;


import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.sighting.Sighting;
import sneer.bricks.network.computers.addresses.sighting.SightingPublisher;
import sneer.bricks.network.computers.addresses.sighting.tests.mock.MockContactSighting;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.connections.ContactSighting;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.software.folderconfig.testsupport.BrickTestWithFiles;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.lang.arrays.ImmutableByteArray;

public class SightingPublisherTest extends BrickTestWithFiles {

	@Bind private final ConnectionManager _connectionManager = mock(ConnectionManager.class);
	@Bind private final ContactSeals _seals = mock(ContactSeals.class);
	private final EventNotifier<ContactSighting> _sightingsSource = my(EventNotifiers.class).newInstance();

	{
		checking(new Expectations() {{
			allowing(_connectionManager).contactSightings(); will(returnValue(_sightingsSource.output()));
			allowingContactGivenSeal("foo");
		}

		private void allowingContactGivenSeal(String nick) {
			allowing(_seals).contactGiven(sealForContact(nick)); will(returnValue(my(Contacts.class).contactGiven(nick)));
		}});	
	}
	
	
	@Test (timeout = 2000)
	public void testSightingsAreConvertedToTuples(){
		my(SightingPublisher.class);
		see("foo", "10.42.10.42");
		assertDnsEntryKept("foo", "10.42.10.42");
	}

	private void assertDnsEntryKept(final String seal, final String ip) {
		assertTupleKept(new Sighting(sealForContact(seal), ip));
	}

	private void assertTupleKept(final Tuple expected) {
		assertTrue(my(TupleSpace.class).keptTuples().contains(expected));
	}

	
	private void see(String contact, String ip) {
		Seal seal = sealForContact(contact);
		_sightingsSource.notifyReceivers(new MockContactSighting(ip, seal));
	}

	
	private Seal sealForContact(String contact) {
		return new Seal(new ImmutableByteArray(contact.getBytes()));
	}
	
	

	
}
