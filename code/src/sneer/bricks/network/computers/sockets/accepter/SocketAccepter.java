package sneer.bricks.network.computers.sockets.accepter;

import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.notifiers.Source;
import sneer.foundation.brickness.Brick;

@Brick
public interface SocketAccepter {
    
	Source<ByteArraySocket> lastAcceptedSocket();

}
