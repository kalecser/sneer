package sneer.bricks.network.computers.addresses.dns.tests.mock;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.sockets.connections.ContactSighting;

public class MockContactSighting implements ContactSighting {

	private final String _ip;
	private final Seal _seal;

	public MockContactSighting(String ip, Seal seal) {
		_ip = ip;
		_seal = seal;
	}

	@Override
	public String ip() {
		return _ip;
	}

	@Override
	public Seal seal() {
		return _seal;
	}

}
