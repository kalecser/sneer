package sneer.bricks.network.computers.addresses.keeper.impl;

import static basis.environments.Environments.my;
import basis.lang.exceptions.Refusal;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

class InternetAddressImpl implements InternetAddress {

	private final Contact _contact;
	
	private final String _host;
	
	private final Signal<Integer> _port;
	
	InternetAddressImpl(Contact contact, String host, int port) throws Refusal {
		if (contact == null) throw new IllegalArgumentException();
		
		if (host == null || host.trim().isEmpty()) throw new Refusal("Host Address must be set.");
		if (port < 1 || port > 65535) throw new Refusal("Port must be between 1 and 65535. Was " + port);
		_contact = contact;
		_host = host;
		_port = my(Signals.class).constant(port);
	}

	@Override
	public Contact contact() {
		return _contact;
	}

	@Override
	public String host() {
		return _host;
	}

	@Override
	public Signal<Integer> port() {
		return _port;
	}

	@Override
	public String toString() {
		return _contact+" ("+_host+" : "+_port+")";
	}
}
