package sneer.bricks.network.computers.sockets.connections;

import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.Call;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.notifiers.Source;
import basis.brickness.Brick;

@Brick
public interface SocketConnectionManager {

	void manageIncomingSocket(ByteArraySocket socket);
	void manageOutgoingSocket(ByteArraySocket socket, Contact contact);

	ByteConnection socketConnectionFor(Contact contact);
	void closeSocketConnectionFor(Contact contact);
	Source<Call> unknownSocketCallers();

}
