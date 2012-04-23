package sneer.bricks.network.computers.tcp;

import java.io.IOException;

import basis.brickness.Brick;


@Brick
public interface TcpNetwork {
	
	ByteArraySocket openSocket(String remoteAddress, int remotePort) throws IOException;
	
	ByteArrayServerSocket openServerSocket(int port) throws IOException;
	
	String remoteIpFor(ByteArraySocket socket);

}