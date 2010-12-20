package sneer.bricks.network.computers.addresses.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.addresses.ContactInternetAddresses;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.sockets.connections.Sighting;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.exceptions.Refusal;

public class ContactInternetAddressesTest extends BrickTestWithTuples {

	@Bind private final Attributes _attributes = mock(Attributes.class);

	private final ContactInternetAddresses _subject = my(ContactInternetAddresses.class);


	@Test(timeout=2000)
	public void keptAddressesAreFound() throws Refusal {
		Contact neide = my(Contacts.class).addContact("Neide");
		my(InternetAddressKeeper.class).add(neide, "10.42.10.42", 42);

		InternetAddress kept = firstKeptAddress();
		assertEquals(neide, kept.contact());
		assertEquals("10.42.10.42", kept.host());
		assertEquals(42, (int)kept.port().currentValue());
	}

	
	@Ignore
	@Test(timeout=2000)
	public void dnsAddressesAreFound() {
		see("10.42.10.42", 8081);
		
		InternetAddress kept = firstKeptAddress();
		assertEquals(remoteContact(), kept.contact());
		assertEquals("10.42.10.42", kept.host());
		assertEquals(8081, (int)kept.port().currentValue());
	}

	
	private void see(final String ip, final int port) {
		System.out.println(my(TupleSpace.class).keptTuples().size());
		Sighting sighting =	EnvironmentUtils.produceIn(remote(), new Producer<Sighting>() { @Override public Sighting produce() {
			Sighting result = new Sighting(ownSeal(), ip);
			my(TupleSpace.class).add(result);
			return result;
		}});
		waitForAllDispatchingToFinish();
		System.out.println(my(TupleSpace.class).keptTuples().size());
		assertTrue(my(TupleSpace.class).keptTuples().contains(sighting));
		
		checking(new Expectations() {{
			oneOf(_attributes).attributeValueFor(remoteContact(), OwnPort.class, Integer.class); 
			will(returnValue(my(Signals.class).constant(port)));
		}});
	}
	
	
	private InternetAddress firstKeptAddress() {
		return _subject.addresses().currentElements().iterator().next();
	}

	
	private Seal ownSeal() {
		return my(OwnSeal.class).get().currentValue();
	}

}
