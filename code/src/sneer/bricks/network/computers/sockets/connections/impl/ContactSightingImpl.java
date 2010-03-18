/**
 * 
 */
package sneer.bricks.network.computers.sockets.connections.impl;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.sockets.connections.ContactSighting;

final class ContactSightingImpl implements ContactSighting {
	private final Seal _contactsSeal;
	private final String _ip;

	ContactSightingImpl(Seal contactsSeal, String ip) {
		_contactsSeal = contactsSeal;
		_ip = ip;
	}

	@Override
	public Seal seal() {
		return _contactsSeal;
	}

	@Override
	public String ip() {
		return _ip;
	}
}