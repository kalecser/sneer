package sneer.bricks.network.computers.tcp.impl;

import java.io.IOException;

import sneer.bricks.network.computers.tcp.ByteArrayServerSocket;
import sneer.bricks.network.computers.tcp.ByteArraySocket;
import sneer.bricks.network.computers.tcp.TcpNetwork;

class TcpNetworkImpl implements TcpNetwork {
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
