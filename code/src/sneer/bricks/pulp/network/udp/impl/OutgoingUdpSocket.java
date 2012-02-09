package sneer.bricks.pulp.network.udp.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import sneer.bricks.pulp.network.ByteArraySocket;



final class OutgoingUdpSocket implements ByteArraySocket {

	private final DatagramSocket delegate;
	private final SocketAddress destination;
	
	private final DatagramPacket packet = new DatagramPacket(new byte[MAX_ARRAY_SIZE], MAX_ARRAY_SIZE);

	
	OutgoingUdpSocket(String remoteAddress, int remotePort) throws SocketException {
		delegate = new DatagramSocket();
		destination = new InetSocketAddress(remoteAddress, remotePort);
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		delegate.send(new DatagramPacket(bytes, bytes.length, destination));
	}

	@Override
	public byte[] read() throws IOException {
		delegate.receive(packet);
		return Arrays.copyOf(packet.getData(), packet.getLength());
	}

	@Override
	public void close() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

}