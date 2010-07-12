/**
 * 
 */
package sneer.bricks.network.computers.addresses.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.addresses.sighting.Sighting;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Signal;

final class InternetAddressImpl implements InternetAddress {

	public final Contact _contact;
	public final Sighting _sighting;

	
	InternetAddressImpl(Contact contact, Sighting sighting) {
		_contact = contact;
		_sighting = sighting;
	}

	
	@Override
	public Signal<Integer> port() {
		return my(Attributes.class).attributeValueFor(_contact, OwnPort.class, Integer.class);
	}

	@Override
	public String host() {
		return _sighting.ip;
	}

	@Override
	public Contact contact() {
		return _contact;
	}
}