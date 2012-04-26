package sneer.bricks.network.computers.tcp.connections;

import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.Call;
import sneer.bricks.network.computers.connections.ConnectionManager;
import sneer.bricks.network.computers.tcp.ByteArraySocket;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.notifiers.Source;
import basis.brickness.Brick;

@Brick
public interface TcpConnectionManager extends ConnectionManager.Delegate {

	void manageIncomingSocket(ByteArraySocket socket);
	void manageOutgoingSocket(ByteArraySocket socket, Contact contact);

	@Override
	ByteConnection connectionFor(Contact contact);
	@Override
	void closeConnectionFor(Contact contact);
	@Override
	Source<Call> unknownCallers();

}
