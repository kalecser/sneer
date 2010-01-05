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
		Object result = my(BrickStateStore.class).readObjectFor(ContactManager.class, ContactManagerImpl.class.getClassLoader());
		return result == null
			? new ArrayList<String>()
			: (Collection<String>) result;
	}
	
	static void save(Collection<Contact> currentNicks) {
		List<String> nicks = new ArrayList<String>();
		for (Contact contact : currentNicks) 
			nicks.add(contact.nickname().currentValue());

		my(BrickStateStore.class).writeObjectFor(ContactManager.class, nicks);
	 }
}