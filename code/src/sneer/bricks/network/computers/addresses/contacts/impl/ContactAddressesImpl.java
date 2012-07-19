package sneer.bricks.network.computers.addresses.contacts.impl;

import java.net.InetSocketAddress;

import sneer.bricks.network.computers.addresses.contacts.ContactAddresses;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;

class ContactAddressesImpl implements ContactAddresses {

	@Override
	public Signal<InetSocketAddress> given(Contact contact) {
		return new ContactAddress(contact).output();
	}

}
