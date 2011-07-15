package sneer.bricks.pulp.network;

import java.io.IOException;

import sneer.foundation.brickness.Brick;

@Brick
public interface Network2010 {
	
	ByteArraySocket openSocket(String remoteAddress, int remotePort) throws IOException;
	
	ByteArrayServerSocket openServerSocket(int port) throws IOException;
	
	String remoteIpFor(ByteArraySocket socket);

}