package sneer.bricks.pulp.keymanager.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Random;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.keymanager.ContactSeals;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.keymanager.generator.OwnSealKeeper;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.exceptions.Refusal;

class ContactSealsImpl implements ContactSeals {

	private Seal _ownSeal;
	
	private final CacheMap<Contact, Register<Seal>> _sealsByContact = CacheMap.newInstance();


	@Override
	synchronized
	public Seal ownSeal() {
		if (_ownSeal == null)
			_ownSeal = produceOwnSeal();
		return _ownSeal;
	}

	
	private Seal produceOwnSeal() {
		if ("true".equals(System.getProperty("sneer.dummy")))
			return dummySeal();

		//All this complexity with a separate prevalent OwnSealKeeper is because the source of randomness cannot be inside a prevalent brick.
		if (my(OwnSealKeeper.class).needsToProduceSeal())
			my(OwnSealKeeper.class).produceOwnSeal(randomness());
		
		return my(OwnSealKeeper.class).seal();
	}


	private Seal dummySeal() {
		return new Seal(new ImmutableByteArray(new byte[128]));
	}


	private byte[] randomness() {
		my(Logger.class).log("This random source needs to be made cryptographically secure. " + getClass());

		byte[] result = new byte[128];
		new Random(System.nanoTime() + System.currentTimeMillis()).nextBytes(result);
		return result;
	}


	@Override
	public Signal<Seal> sealGiven(Contact contact) {
		return _sealsByContact.get(contact).output();
	}

	
	@Override
	public void put(String nick, Seal seal) throws Refusal {
		final Contact contact = my(Contacts.class).contactGiven(nick);
		if (contact == null) throw new Refusal("No contact found with nickname: " + nick);

		Contact oldContact = contactGiven(seal);
		if (contact.equals(oldContact)) return;
		if (oldContact != null) throw new Refusal("Trying to set a Seal for '" + contact + "' that already belonged to '" + oldContact + "'.");
		
		_sealsByContact.get(contact, new Producer<Register<Seal>>() { @Override public Register<Seal> produce() {
			return my(Signals.class).newRegister(null);
		}}).setter().consume(seal);
	}


	@Override
	public Contact contactGiven(Seal peersSeal) {
		for (Contact candidate : _sealsByContact.keySet())
			if(sealGiven(candidate).currentValue().equals(peersSeal))
				return candidate;
		
		return null;
	}

}
