package sneer.bricks.snapps.dns.tests;


import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.connections.ContactSighting;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.snapps.dns.Dns;
import sneer.bricks.snapps.dns.tests.mock.MockContactSighting;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;

public class DnsTests  extends BrickTest {

	@Bind private final ConnectionManager connectionManager = mock(ConnectionManager.class);
	private Dns subject;
	private EventNotifier<ContactSighting> _sightingsSource;
	
	
	@Test
	public void testDnsOneContactSighted(){
		sighted("foo", "10.42.10.42");
		Assert.assertArrayEquals(new String[]{"10.42.10.42"}, knownIpsForContact("foo"));
	}
	
	@Test
	public void testDnsOneContactSightedTwice(){
		sighted("foo", "10.42.10.42");
		sighted("foo", "10.42.10.43");
		Assert.assertArrayEquals(new String[]{"10.42.10.42", "10.42.10.43"}, knownIpsForContact("foo"));
	}
	
	@Test
	public void testDnsTwoContactsSighted(){
		sighted("foo", "10.42.10.42");
		sighted("bar", "10.42.10.43");
		Assert.assertArrayEquals(new String[]{"10.42.10.42"}, knownIpsForContact("foo"));
		Assert.assertArrayEquals(new String[]{"10.42.10.43"}, knownIpsForContact("bar"));
	}

	private Object[] knownIpsForContact(String contact) {
		return  subject.knownIpsForContact(sealForContact(contact)).currentElements().toArray();
	}

	private void sighted(String contact, String ip) {
		Seal seal = sealForContact(contact);
		_sightingsSource.notifyReceivers(new MockContactSighting(ip, seal));
	}

	private Seal sealForContact(String contact) {
		return new Seal(new ImmutableByteArray(contact.getBytes()));
	}

	@Before
	public void setup(){
		
		_sightingsSource = my(EventNotifiers.class).newInstance();
		final EventSource<ContactSighting> sightings = _sightingsSource.output();
		
		checking(new Expectations() {{
			allowing(connectionManager).contactSightings(); will(returnValue(sightings));	
		}});	
		
		subject  = my(Dns.class);
	}
}
