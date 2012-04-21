package sneer.bricks.network.computers.connections.impl;

import static basis.environments.Environments.my;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.Call;
import sneer.bricks.network.computers.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.connections.SocketConnectionManager;
import sneer.bricks.network.computers.sockets.connections.originator.SocketOriginator;
import sneer.bricks.network.computers.sockets.connections.receiver.SocketReceiver;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.notifiers.Source;


class ConnectionManagerImpl implements ConnectionManager {

	private static final boolean USE_UDP = false;
	private final SocketConnectionManager delegate = my(SocketConnectionManager.class);
	
	{
		if (!USE_UDP) {
			my(SocketOriginator.class);
			my(SocketReceiver.class);
		}
	}
	
	
	@Override
	public ByteConnection connectionFor(Contact contact) {
		return delegate.socketConnectionFor(contact);
	}

	
	@Override
	public void closeConnectionFor(Contact contact) {
		delegate.closeSocketConnectionFor(contact);
	}

	
	@Override
	public Source<Call> unknownCallers() {
		return delegate.unknownSocketCallers();
	}

}
