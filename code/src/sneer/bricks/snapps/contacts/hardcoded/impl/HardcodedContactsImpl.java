package sneer.bricks.snapps.contacts.hardcoded.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.snapps.contacts.hardcoded.HardcodedContacts;
import sneer.foundation.lang.exceptions.Refusal;

public class HardcodedContactsImpl implements HardcodedContacts {

	
	private final Contacts _contactManager = my(Contacts.class);

	
	HardcodedContactsImpl() {
		if(!_contactManager.contacts().currentElements().isEmpty()) 
			return;
				
		for (ContactInfo contact : contacts())
			add(contact);
	}
	
	
	private void add(ContactInfo contact) {
		if (my(OwnSeal.class).oldGet().equals(contact._seal)) return;
		addAddresses(contact);
		addSeal(contact);
	}

	
	private void addSeal(ContactInfo contact) {
		if (contact._seal == null) return;
		try {
			my(ContactSeals.class).put(contact._nick, contact._seal);
		} catch (Refusal e) {
			throw new IllegalStateException(e); // Fix Handle this exception.
		}
	}

	
	private void addAddresses(ContactInfo contact) {
		String nick = contact._nick;
		addAddress(nick, contact._host, contact._port);
		
		for (String host : alternativeHostsFor(nick))
			addAddress(nick, host, contact._port);
	}

	
	private String[] alternativeHostsFor(String nick) {
		if (nick.equals("Kalecser")) return new String[]{"10.42.11.165"};
		if (nick.equals("Klaus")) return new String[]{"200.169.90.89", "10.42.11.19"};
		return new String[]{};
	}

	
	private void addAddress(String nick, String host, int port) {
		Contact contact = _contactManager.produceContact(nick);
		my(InternetAddressKeeper.class).add(contact, host, port);
	}

	
	private ContactInfo[] contacts() {
		return new ContactInfo[] {
			new ContactInfo("Agnaldo4j", "agnaldo4j.selfip.com", 5923),
			new ContactInfo("Bamboo","rbo.selfip.net",5923),
			new ContactInfo("Bihaiko", "bihaiko.dyndns.org", 6789),
			new ContactInfo("Célio", "ccidral.dyndns.org", 9789),
			new ContactInfo("Daniel Santos", "dfcsantos.homeip.net", 7777),
			new ContactInfo("Douglas Giacomini", "dtgiacomini.dyndns.org", 5923),
			new ContactInfo("Dummy", "localhost", 7777, new Seal(new ImmutableByteArray(new byte[128]))),
			new ContactInfo("Igor Arouca", "igorarouca.dyndns.org", 6789),
			new ContactInfo("Kalecser", "kalecser.dyndns.org", 7770),
			new ContactInfo("Klaus", "klausw.selfip.net", 5923),
			new ContactInfo("Nell", "anelisedaux.dyndns.org", 5924),
			new ContactInfo("Priscila Vriesman", "priscilavriesman.dyndns.org", 7770),
			new ContactInfo("Ramon Tramontini", "ramontramontini.dyndns.org", 7770),
			new ContactInfo("Vitor Pamplona", "vfpamp.dyndns.org", 5923),
		};
	}


	static class ContactInfo {
		final String _nick;
		final String _host;
		final int _port;
		final Seal _seal;

		ContactInfo(String nick, String host, int port) {
			this(nick, host, port, null);
		}

		ContactInfo(String nick, String host, int port, Seal seal) {
			_nick = nick;
			_host = host;
			_port = port;
			_seal = seal;
		}
	}

}