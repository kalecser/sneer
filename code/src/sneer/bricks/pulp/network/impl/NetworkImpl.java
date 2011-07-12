package sneer.bricks.pulp.network.impl;

import java.io.IOException;

import sneer.bricks.pulp.network.ByteArrayServerSocket;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network;

class NetworkImpl implements Network {
	@Override
	public ByteArrayServerSocket openServerSocket(int port) throws IOException {
		return new ByteArrayServerSocketImpl(port);
	}

	
	@Override
	public ByteArraySocket openSocket(String remoteHost, int remotePort) throws IOException {
		return new ByteArraySocketImpl(remoteHost, remotePort);
	}

	
	@Override
	public String remoteIpFor(ByteArraySocket socket) {
		return ((ByteArraySocketImpl)socket).remoteIP();
	}
	
}
