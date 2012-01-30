package sneer.bricks.identity.seals.contacts.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.exceptions.Refusal;
import sneer.foundation.testsupport.PrettyPrinter;

class ContactSealsImpl implements ContactSeals {

	@SuppressWarnings("unused")
	private WeakContract _refToAvoidGc;


	{
		_refToAvoidGc = my(Contacts.class).contacts().addReceiver(new Consumer<CollectionChange<Contact>>() { @Override public void consume(CollectionChange<Contact> change) {
			for (Contact removedContact : change.elementsRemoved())
				_sealsByContact.remove(removedContact);
		}});
		
		PrettyPrinter.registerFor(Seal.class, new Functor<Seal, String>() { @Override public String evaluate(Seal seal) {
			Contact contact = contactGiven(seal);
			if (contact == null) return seal.toString();

			String nickname = contact.nickname().currentValue();
			if (nickname.isEmpty()) return seal.toString();

			return nickname;
		}});
	}


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
		if (contact.equals(oldContact)) throw new Refusal("Trying to set a Seal for '" + contact + "' that was already set.");
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


	@Override
	public Signal<String> nicknameGiven(Seal peersSeal) {
		Contact contact = contactGiven(peersSeal);
		return contact == null
			? null
			: contact.nickname();
	}

}
