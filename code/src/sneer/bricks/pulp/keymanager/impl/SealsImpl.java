package sneer.bricks.pulp.keymanager.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.keymanager.generator.OwnSealKeeper;

class SealsImpl implements Seals {

	private Seal _ownSeal;
	
	private final Map<Contact, Seal> _sealsByContact = new HashMap<Contact, Seal>();


	@Override
	synchronized
	public Seal ownSeal() {
		if (_ownSeal == null)
			_ownSeal = produceOwnSeal();
		return _ownSeal;
	}

	
	private Seal produceOwnSeal() {
		//All this complexity with a separate prevalent OwnSealKeeper is because the source of randomness cannot be inside a prevalent brick.
		if (my(OwnSealKeeper.class).needsToProduceSeal())
			my(OwnSealKeeper.class).produceOwnSeal(randomness());
		
		return my(OwnSealKeeper.class).seal();
	}


	private byte[] randomness() {
		my(Logger.class).log("This random source needs to be made cryptographically secure. " + getClass());

		byte[] result = new byte[128];
		new Random().nextBytes(result);
		return result;
	}


	@Override
	public Seal sealGiven(Contact contact) {
		return _sealsByContact.get(contact);
	}


	@Override
	public synchronized void put(String nick, Seal seal) {
		Contact contact = my(Contacts.class).contactGiven(nick);
		if(sealGiven(contact) != null) throw new IllegalArgumentException("There already was a seal registered for contact: " + contact.nickname().currentValue());
		if(contactGiven(seal) != null) throw new IllegalArgumentException("There already was a contact registered with seal: " + seal);
		_sealsByContact.put(contact, seal);
	}


	@Override
	public synchronized Contact contactGiven(Seal peersSeal) {
		for (Contact candidate : _sealsByContact.keySet())
			if(_sealsByContact.get(candidate).equals(peersSeal))
				return candidate;
		
		return null;
	}
}
