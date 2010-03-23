package sneer.bricks.network.computers.addresses;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.computers.addresses.sighting.Sighting;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.lang.exceptions.Refusal;

public class ContactInternetAddressesTest extends BrickTest {

	@Bind private final ContactSeals _contactSeals = mock(ContactSeals.class);
	private final Contact contact = produceContact("Neide");
	{
		checking(new Expectations(){{
			allowing(_contactSeals).contactGiven(seal()); will(returnValue(contact));
		}});
	}
	
	
	private final ContactInternetAddresses _subject = my(ContactInternetAddresses.class);

		
	@Test
	public void keptAddressesAreFound(){
		my(InternetAddressKeeper.class).add(contact, "10.42.10.42", 42);
		
		InternetAddress kept = keptAddress();
		assertEquals(contact, kept.contact());
		assertEquals("10.42.10.42", kept.host());
		assertEquals(42, kept.port());
	}

	@Ignore
	@Test
	public void dnsAddressesAreFound() {
		see("10.42.10.42");
		InternetAddress kept = keptAddress();
		assertEquals(contact, kept.contact());
		assertEquals("10.42.10.42", kept.host());
		assertEquals(42, kept.port());
	}

	private void see(String ip) {
		my(TupleSpace.class).acquire(new Sighting(seal(), ip));
		my(TupleSpace.class).waitForAllDispatchingToFinish();
	}
	
	private Seal seal() {
		return new Seal(new ImmutableByteArray(new byte[]{42}));
	}
	
	private InternetAddress keptAddress() {
		
		InternetAddress kept = _subject.addresses().currentElements().iterator().next();
		return kept;
	}
	
	private Contact produceContact(String nickname) {
		try {
			return my(Contacts.class).addContact(nickname);
		} catch (Refusal e) {
			throw new IllegalStateException(e);
		}
	}
}
