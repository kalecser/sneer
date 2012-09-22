package sneer.bricks.snapps.web.impl;

import static basis.environments.Environments.my;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.attributes.Attributes;

public class ContactProviderImpl implements ContactProvider {

	@Override
	public Seal getSealForNickOrNull(String nickName) {
		Seal mySeal = my(OwnSeal.class).get().currentValue();
		if (nickName.isEmpty()) return mySeal;
		
		Contact contact = my(Contacts.class).contactGiven(nickName);
		if(contact == null) return null;
		return my(ContactSeals.class).sealGiven(contact).currentValue();
	}

}
