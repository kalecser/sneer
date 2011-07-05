//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Adenauer Gabriel.

package sneer.bricks.pulp.network.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.foundation.lang.CacheMap;

class UdpByteArraySocketImpl implements ByteArraySocket {

	private final CacheMap<SocketAddress, DatagramPacket> _cache;
	private final DatagramPacket _packetToSend = newPacket();
	private final SocketAddress _to;
	private DatagramSocket _socket;

	
	public UdpByteArraySocketImpl(SocketAddress to, DatagramSocket serverSocket, CacheMap<SocketAddress, DatagramPacket> cache ) throws IOException {
		_to = to;
		_socket = createSocketIfnecessary(serverSocket);
		_cache = cache;
	}

	
	@Override
	public void write(byte[] array) throws IOException {
		if (bigger(array)) return;
		_packetToSend.setData(array);
		_packetToSend.setSocketAddress(_to);
		_socket.send(_packetToSend);
	}

	
	@Override
	public byte[] read() { //TODO: Implement locked.
		DatagramPacket packet = _cache.get(_to);
		_cache.remove(_to);
		return packet == null ? null : packet.getData();
	}

	
	@Override
	public void close() {
		_socket.close();
	}

	
	String remoteIP() {
		return _socket.getInetAddress().getHostAddress();
	}
	
	
	private DatagramPacket newPacket() {
		byte[] array = new  byte[MAX_ARRAY_SIZE];
		return new DatagramPacket(array, array.length);
	}
	
	
	private DatagramSocket createSocketIfnecessary(DatagramSocket socket) throws IOException {
		if (socket == null)
			socket = new DatagramSocket();
		return socket;
	}
	
	
	private boolean bigger(byte[] array) {
		boolean result = false;
		int length = array.length;
		if (length > MAX_ARRAY_SIZE) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Packet greater than " + MAX_ARRAY_SIZE + " bytes cannot be sent.", "Size: " + length);
			result = true;
		}
		return result;
	}
	
}