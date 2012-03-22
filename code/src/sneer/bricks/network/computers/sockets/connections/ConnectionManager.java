package sneer.bricks.network.computers.sockets.connections;

import basis.brickness.Brick;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.notifiers.Source;

@Brick
public interface ConnectionManager {

	void manageIncomingSocket(ByteArraySocket socket);
	void manageOutgoingSocket(ByteArraySocket socket, Contact contact);

	ByteConnection connectionFor(Contact contact);

	void closeConnectionFor(Contact contact);
	
	Source<Call> unknownCallers();

}
