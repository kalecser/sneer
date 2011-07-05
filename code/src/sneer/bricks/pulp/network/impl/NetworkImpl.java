package sneer.bricks.pulp.network.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import sneer.bricks.pulp.network.ByteArrayServerSocket;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network;
import sneer.foundation.lang.CacheMap;

class NetworkImpl implements Network {
	private static final boolean USE_UDP = false; 
	
	private final CacheMap<SocketAddress, DatagramPacket> _cache = CacheMap.newInstance();
	private DatagramSocket _serverSocket; 
	

	@Override
	public ByteArrayServerSocket openServerSocket(int port) throws IOException {
		if (USE_UDP) {
			_serverSocket = new DatagramSocket(port);
			return new UdpByteArrayServerSocketImpl(_serverSocket, _cache);
		}
		return new ByteArrayServerSocketImpl(port);
	}

	
	@Override
	public ByteArraySocket openSocket(String remoteHost, int remotePort) throws IOException {
		return USE_UDP ? newUdpSocketTo(remoteHost, remotePort) :  new ByteArraySocketImpl(remoteHost, remotePort);
	}

	
	@Override
	public String remoteIpFor(ByteArraySocket socket) {
		return USE_UDP ? ((UdpByteArraySocketImpl)socket).remoteIP() : ((ByteArraySocketImpl)socket).remoteIP();
	}
	
	
	private ByteArraySocket newUdpSocketTo(String remoteHost, int remotePort) throws IOException {
		InetSocketAddress to = new InetSocketAddress(remoteHost, remotePort);
		return new UdpByteArraySocketImpl(to, _serverSocket, _cache);
	}
	
}
