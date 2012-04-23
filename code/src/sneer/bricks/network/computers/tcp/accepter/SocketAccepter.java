package sneer.bricks.network.computers.tcp.accepter;

import basis.brickness.Brick;
import sneer.bricks.network.computers.tcp.ByteArraySocket;
import sneer.bricks.pulp.notifiers.Source;

@Brick
public interface SocketAccepter {
    
	Source<ByteArraySocket> lastAcceptedSocket();

}
