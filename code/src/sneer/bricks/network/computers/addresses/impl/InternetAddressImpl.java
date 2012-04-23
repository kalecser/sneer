/**
 * 
 */
package sneer.bricks.network.computers.addresses.impl;

import static basis.environments.Environments.my;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.tcp.connections.Sighting;
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
	
	@Override
	public String toString() {
		return "Sighting: " + _contact+" (" + host() + ")";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ contact().hashCode();
		result = prime * result
				+ host().hashCode();
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof InternetAddress)) return false;
		InternetAddress other = (InternetAddress) obj;
		if (!other.contact().equals(contact()))	return false;
		if (!other.host().equals(host())) return false;
		return true;
	}
	
	

}