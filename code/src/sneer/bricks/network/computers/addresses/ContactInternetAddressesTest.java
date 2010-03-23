package sneer.bricks.network.computers.addresses;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.exceptions.Refusal;

public class ContactInternetAddressesTest extends BrickTest {
	
	private final ContactInternetAddresses _subject = my(ContactInternetAddresses.class);
	
	@Test
	public void keptAddressesAreFound() throws Refusal {
		Contact contact = my(Contacts.class).addContact("Neide");
		my(InternetAddressKeeper.class).add(contact, "10.42.10.42", 42);
		InternetAddress kept = _subject.addresses().currentElements().iterator().next();
		assertEquals(contact, kept.contact());
		assertEquals("10.42.10.42", kept.host());
		assertEquals(42, kept.port());
	}

}
