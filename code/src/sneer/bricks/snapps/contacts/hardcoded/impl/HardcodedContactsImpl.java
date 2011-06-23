package sneer.bricks.snapps.contacts.hardcoded.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.codec.Codec;
import sneer.bricks.hardware.cpu.codec.DecodeException;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.snapps.contacts.hardcoded.HardcodedContacts;
import sneer.foundation.lang.arrays.ImmutableByteArray;
import sneer.foundation.lang.exceptions.NotImplementedYet;
import sneer.foundation.lang.exceptions.Refusal;

public class HardcodedContactsImpl implements HardcodedContacts {

	
	private final Contacts _contactManager = my(Contacts.class);

	
	HardcodedContactsImpl() {
		for (ContactInfo contact : contacts())
			refresh(contact);
	}
	
	
	private void refresh(ContactInfo contact) {
		if (my(OwnSeal.class).get().currentValue().equals(contact._seal))
			return;
		
		refreshAddresses(contact);
		refreshSeal(contact);
	}

	
	private void refreshSeal(ContactInfo contact) {
		if (contact._seal == null) return;
		try {
			my(ContactSeals.class).put(contact._nick, contact._seal);
		} catch (Refusal e) {
			throw new NotImplementedYet(e);
		}
	}

	
	private void refreshAddresses(ContactInfo contactInfo) {
		String nick = contactInfo._nick;
		Contact contact = _contactManager.produceContact(nick);
		my(InternetAddressKeeper.class).add(contact, contactInfo._host, contactInfo._port);
	}

	
	private ContactInfo[] contacts() {
		return new ContactInfo[] {
//			new ContactInfo("Bamboo","rbo.selfip.net", 5923),
//			new ContactInfo("Bihaiko", "bihaiko.dyndns.org", 6789),
//			new ContactInfo("Daniel Santos", "dfcsantos.homeip.net", 7777),
			new ContactInfo("Dummy", "localhost", 7777, newSeal("c0e0ae71b239640fded22b880f7cb63772f04a6f9c7685689c2610f395dbff1d3cfc11f36f20d54305ff51b26cd171e4882d628ea4a1ac201641cf17fea6912c")),
			new ContactInfo("Edmundo", "edmundo.selfip.net", 8888, newSeal("cd27ee9965cc808ffb2f5379d8c246dd26e050541927ef886541ef0c7e7af527ae98c87075418806748f72f7ef60496d49d6ab317f9c08f75f542253b3487014")),
			new ContactInfo("Igor Arouca", "igorarouca.selfip.net", 6789, newSeal("937c611591d4bca0b56e1a661e4fa7e0552a7c85692f3352a9d7ac5019769cd4c1be6300b3bab890c77a29ca7506baadc8d329c7eac16254e9afec43d92e5da3")),
			new ContactInfo("Julien Roubieu", "jroubieu.dyndns-home.com", 8181, newSeal("7871320fd3404ab0d1f9c5ab2b3febfeb6122d8daa080e1508c7ec56a76aae60656e498ce9975fe2b26a4219e4f20b12c880808716e043b7b3ab215dab16965e")),
//			new ContactInfo("Kalecser", "kalecser.dyndns.org", 7770),
			new ContactInfo("Klaus", "klausw.dyndns.org", 5923, newSeal("9fa8ae50bde46dc175527015afc3d9005cfccb2dfaaac7c51d8c854419bb5381efb34a15876cef1e25d170babd451d25e3d5e20a96404094a0e62c94524755b8")),
			new ContactInfo("Patrick Roemer", "judgefang.dontexist.net", 4711, newSeal("7e90b955120b16a98a63371dd0f4eae54218d4245f6afa83d5301f80bc05eadfe89d26dd19620e3c7b4cb29e59d7845c156ab0eb1d0394a14a50416799d4062f")),
			new ContactInfo("Adenauer", "adenauer.dyndns.info", 9090, newSeal("e89c068a50c77cb0611bcb4e1ccaa4175bc7e6520776bd56a59ddc28d5af399e3f47b60e018433f68fd67c6cbd1826bc4f23af57d85a0310f87594c18c3489c4")),
		};
	}


	private Seal newSeal(String sealString) {
		try {
			return new Seal(new ImmutableByteArray(my(Codec.class).hex().decode(sealString)));
		} catch (DecodeException e) {
			throw new IllegalStateException(e);
		}
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