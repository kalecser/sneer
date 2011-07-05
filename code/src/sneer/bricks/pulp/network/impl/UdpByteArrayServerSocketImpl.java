//Copyright (C) 2008 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Adenauer Gabriel.

package sneer.bricks.pulp.network.impl;


import static sneer.bricks.pulp.network.ByteArraySocket.MAX_ARRAY_SIZE;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import sneer.bricks.pulp.network.ByteArrayServerSocket;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.foundation.lang.CacheMap;

class UdpByteArrayServerSocketImpl implements ByteArrayServerSocket {
	
	private CacheMap<SocketAddress, DatagramPacket> _cache;
	private DatagramSocket _serverSocket; 
	
	
	public UdpByteArrayServerSocketImpl(DatagramSocket serverSocket, CacheMap<SocketAddress, DatagramPacket> cache) {
		_serverSocket = serverSocket;
		_cache = cache;
	}
	
	
	@Override
	public ByteArraySocket accept() throws IOException {
		DatagramPacket packet = newPacket();
		_serverSocket.receive(packet);
		SocketAddress to = packet.getSocketAddress();
		
		display("Remote Address: " + packet.getAddress().getHostAddress() + " Remote Port: " + packet.getPort());
		display("SocketAddress: " + to.toString());
		display("Data received: " + new String(packet.getData()));
		
		_cache.put(to, packet);
		return new UdpByteArraySocketImpl(to, _serverSocket, _cache);
	}

	
	@Override
	public void crash() {
		_serverSocket.close();
	}


	private DatagramPacket newPacket() {
		byte[] data = new byte[MAX_ARRAY_SIZE];
		return new DatagramPacket(data, data.length);
	}
	
	
	private void display(String msg) {
		System.out.println(msg);
	}
	
}
