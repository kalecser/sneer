//Copyright (C) 2008 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Adenauer Gabriel.

package sneer.bricks.pulp.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import sneer.bricks.pulp.network.ByteArrayServerSocket;
import sneer.bricks.pulp.network.ByteArraySocket;

class UdpByteArrayServerSocket implements ByteArrayServerSocket {

	private final DatagramSocket _serverSocket;

	
	UdpByteArrayServerSocket(int port) throws IOException {
		_serverSocket = new DatagramSocket(port);
	}

	
	@Override
	public ByteArraySocket accept() throws IOException {
		return connect( whenAsked() );
	}

	
	@Override
	public void crash() {
		_serverSocket.close();
	}

	
	private UdpByteArraySocket connect(DatagramPacket receivedPacket) throws IOException {
		System.out.println("Accept from (SocketAddress: " + receivedPacket.getSocketAddress().toString()+ ")" + " (Address: " + receivedPacket.getAddress() + ":" + receivedPacket.getPort());
		System.out.println("Content received: " + new String(receivedPacket.getData(), 0, receivedPacket.getLength()));
		UdpByteArraySocket udpSocket = new UdpByteArraySocket(receivedPacket);
		return udpSocket;
	}

	public DatagramPacket whenAsked() throws IOException {
		byte[] data = new byte[_serverSocket.getReceiveBufferSize()];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		_serverSocket.receive(packet);
		return packet;
	}

}
