package sneer.bricks.network.computers.connections.impl;

import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.Call;
import sneer.bricks.network.computers.connections.ConnectionManager;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.notifiers.Source;


class ConnectionManagerImpl implements ConnectionManager {

	@Override
	public ByteConnection connectionFor(Contact contact) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void closeConnectionFor(Contact contact) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public Source<Call> unknownCallers() {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

}
