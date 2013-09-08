package sneer.bricks.snapps.web.impl;

import static basis.environments.Environments.my;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;

public class ContactProviderImpl implements ContactProvider {

	@Override
	public Seal getSealForNickOrNull(String nickName) {
		if (nickName.isEmpty())
			return my(OwnSeal.class).get().currentValue();
		
		Contact contact = my(Contacts.class).contactGiven(nickName);
		if (contact == null) return null;
		return my(ContactSeals.class).sealGiven(contact).currentValue();
	}

}
