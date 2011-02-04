package sneer.bricks.identity.seals.contacts.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.lang.exceptions.Refusal;

public class ContactSealsTest extends BrickTestBase {

	ContactSeals _subject = my(ContactSeals.class);
	
	@Test
	public void contactDeletionCascadesToItsSeals() throws Refusal {
		Contact neide = my(Contacts.class).produceContact("Neide");
		Seal seal = new Seal(new byte[] {1, 2, 3});
		_subject.put("Neide", seal);
		
		my(Contacts.class).produceContact("Pedro");
		try {
			_subject.put("Pedro", seal);
			fail("Seal already belongs to Neide");
		} catch (Refusal expected) {}

		my(Contacts.class).removeContact(neide);
		_subject.put("Pedro", seal);
	}
	
}
