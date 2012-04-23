package sneer.bricks.network.computers.connections.impl;

import static basis.environments.Environments.my;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.Call;
import sneer.bricks.network.computers.connections.ConnectionManager;
import sneer.bricks.network.computers.tcp.connections.TcpConnectionManager;
import sneer.bricks.network.computers.tcp.connections.originator.SocketOriginator;
import sneer.bricks.network.computers.tcp.connections.receiver.SocketReceiver;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.notifiers.Source;


class ConnectionManagerImpl implements ConnectionManager {

	private static final boolean USE_UDP = false;
	private final Worker delegate = my(TcpConnectionManager.class);
	
	{
		if (!USE_UDP) {
			my(SocketOriginator.class);
			my(SocketReceiver.class);
		}
	}
	
	
	@Override
	public ByteConnection connectionFor(Contact contact) {
		return delegate.connectionFor(contact);
	}

	
	@Override
	public void closeConnectionFor(Contact contact) {
		delegate.closeConnectionFor(contact);
	}

	
	@Override
	public Source<Call> unknownCallers() {
		return delegate.unknownCallers();
	}

}
