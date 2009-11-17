package sneer.bricks.network.social.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.ContactManager;
import sneer.bricks.software.bricks.statestore.BrickStateStore;

abstract class Store {

	static Collection<String> restore() {
		Collection<String> nicks = (Collection<String>) my(BrickStateStore.class).readObjectFor(ContactManager.class, ContactManagerImpl.class.getClassLoader());
		return nicks == null
			? new ArrayList<String>()
			: nicks;
	}
	
	static void save(Collection<Contact> currentNicks) {
		List<String> nicks = new ArrayList<String>();
		for (Contact contact : currentNicks) 
			nicks.add(contact.nickname().currentValue());

		my(BrickStateStore.class).writeObjectFor(ContactManager.class, nicks);
	 }
}