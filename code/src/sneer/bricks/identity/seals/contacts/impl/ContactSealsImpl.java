package sneer.bricks.identity.seals.contacts.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.contacts.Contact;
import sneer.bricks.network.social.contacts.Contacts;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.exceptions.Refusal;

class ContactSealsImpl implements ContactSeals {


	private final CacheMap<Contact, Register<Seal>> _sealsByContact = CacheMap.newInstance();


	@Override
	public Signal<Seal> sealGiven(Contact contact) {
		return sealRegisterGiven(contact).output();
	}

	
	@Override
	public void put(String nick, Seal seal) throws Refusal {
		final Contact contact = my(Contacts.class).contactGiven(nick);
		if (contact == null) throw new Refusal("No contact found with nickname: " + nick);

		Contact oldContact = contactGiven(seal);
		if (contact.equals(oldContact)) return;
		if (oldContact != null) throw new Refusal("Trying to set a Seal for '" + contact + "' that already belonged to '" + oldContact + "'.");
		
		sealRegisterGiven(contact).setter().consume(seal);
	}


	private Register<Seal> sealRegisterGiven(final Contact contact) {
		return _sealsByContact.get(contact, new Producer<Register<Seal>>() { @Override public Register<Seal> produce() {
			return my(Signals.class).newRegister(null);
		}});
	}


	@Override
	public Contact contactGiven(Seal peersSeal) {
		for (Contact candidate : _sealsByContact.keySet()) {
			Seal candidatesSeal = sealGiven(candidate).currentValue();
			if(candidatesSeal != null && candidatesSeal.equals(peersSeal))
				return candidate;
		}

		return null;
	}

}
