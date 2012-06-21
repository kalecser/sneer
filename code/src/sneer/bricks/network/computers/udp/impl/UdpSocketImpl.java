package sneer.bricks.network.computers.udp.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;


class UdpSocketImpl implements UdpSocket {

	private final DatagramSocket socket;


	UdpSocketImpl(int portNumber) throws SocketException {
		socket = new DatagramSocket(portNumber);
	}

	
	@Override
	public void send(DatagramPacket packet) throws IOException {
		socket.send(packet);
	}

	
	@Override
	public void receive(DatagramPacket packet) throws IOException {
		socket.receive(packet);
	}


	@Override
	public void crash() {
		socket.close();
	}

}
