package sneer.bricks.network.computers.sockets.accepter;

import basis.brickness.Brick;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.notifiers.Source;

@Brick
public interface SocketAccepter {
    
	Source<ByteArraySocket> lastAcceptedSocket();

}
