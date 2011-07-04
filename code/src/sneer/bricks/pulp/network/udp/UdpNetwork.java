package sneer.bricks.pulp.network.udp;

import java.io.IOException;

import sneer.bricks.pulp.network.ByteArrayServerSocket;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network;

class UdpNetwork implements Network {

	@Override
	public ByteArrayServerSocket openServerSocket(int port) throws IOException {
		System.out.println("Open Server Socket: " + port);
		return new UdpByteArrayServerSocket(port);
	}

	
	@Override
	public ByteArraySocket openSocket(String host, int port) throws IOException {
		System.out.println("Open Socket: " + host + ":" + port);
		return new UdpByteArraySocket(host, port);
	}

	
	@Override
	public String remoteIpFor(ByteArraySocket socket) {
		return ((UdpByteArraySocket)socket).remoteIP();
	}
}
