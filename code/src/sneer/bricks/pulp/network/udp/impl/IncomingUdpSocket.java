package sneer.bricks.pulp.network.udp.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.network.ByteArraySocket;



final class IncomingUdpSocket implements ByteArraySocket {

	private final DatagramSocket delegate;
	private final SocketAddress destination;
	
	private final BlockingQueue<byte[]> incomingPackets = new ArrayBlockingQueue<byte[]>(100);

	public IncomingUdpSocket(DatagramSocket delegate, SocketAddress destination) {
		this.delegate = delegate;
		this.destination = destination;
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		synchronized (delegate) {
			delegate.send(new DatagramPacket(bytes, bytes.length, destination));
		}
	}

	@Override
	public byte[] read() {
		try {
			return incomingPackets.take();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void close() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	
	void receive(byte[] packet) {
		boolean kept = incomingPackets.offer(packet);
		if (!kept) my(Logger.class).log("Incoming UDP packet (" + packet.length + " bytes) ignored because queue is full.");
	}
}