package sneer.bricks.pulp.network.udp.impl;

import static sneer.bricks.pulp.network.ByteArraySocket.MAX_ARRAY_SIZE;
import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.pulp.network.ByteArrayServerSocket;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Functor;


class UdpServerSocket implements ByteArrayServerSocket {

	private final DatagramSocket delegate;
	private final DatagramPacket packet = new DatagramPacket(new byte[MAX_ARRAY_SIZE], MAX_ARRAY_SIZE);
	private final BlockingQueue<IncomingUdpSocket> incomingConnections = new ArrayBlockingQueue<IncomingUdpSocket>(100);
	
	CacheMap<SocketAddress, IncomingUdpSocket> socketsByEndpoint = CacheMap.newInstance();
	private final Functor<SocketAddress, IncomingUdpSocket> socketGivenEndpoint = new Functor<SocketAddress, IncomingUdpSocket>() { @Override public IncomingUdpSocket evaluate(SocketAddress endpoint) {
		IncomingUdpSocket socket = new IncomingUdpSocket(delegate, endpoint);
		incomingConnections.add(socket);
		return socket;
	}};


	UdpServerSocket(int port) throws SocketException {
		delegate = new DatagramSocket(port);
		my(Threads.class).startStepping("UDP Server", new Closure() { @Override public void run() {
			try {
				receiveDatagram();
			} catch (IOException e) {
				my(ExceptionLogger.class).log(e);
			}
		}});
	}

	
	private void receiveDatagram() throws IOException {
		delegate.receive(packet);
		
		SocketAddress endpoint = packet.getSocketAddress();
		IncomingUdpSocket socket = socketsByEndpoint.get(endpoint, socketGivenEndpoint);
		
		socket.receive(Arrays.copyOf(packet.getData(), packet.getLength()));
	}

	
	@Override
	public ByteArraySocket accept() {
		try {
			return incomingConnections.take();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void crash() {
		delegate.close();
	}

}
