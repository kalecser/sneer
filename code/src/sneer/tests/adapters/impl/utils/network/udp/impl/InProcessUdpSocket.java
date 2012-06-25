package sneer.tests.adapters.impl.utils.network.udp.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import basis.lang.Functor;
import basis.lang.exceptions.Crashed;


class InProcessUdpSocket implements UdpSocket {

	private static final InetAddress LOCALHOST = localhost();
	
	private final int portNumber;
	private final Functor<Integer, InProcessUdpSocket> socketsByNumberFinder;

	private final BlockingQueue<DatagramPacket> incomingPackets = new LinkedBlockingQueue<DatagramPacket>();

	private volatile boolean isCrashed = false;
	


	InProcessUdpSocket(int portNumber, Functor<Integer, InProcessUdpSocket> socketsByNumberFinder) {
		this.portNumber = portNumber;
		this.socketsByNumberFinder = socketsByNumberFinder;
	}


	@Override
	public void receive(DatagramPacket packet) throws IOException {
		checkNotCrashed();
		copyInto(waitForPacket(), packet);
	}


	private void copyInto(DatagramPacket original, DatagramPacket copy) {
		int len = original.getLength();
		System.arraycopy(original.getData(), 0, copy.getData(), 0, len);
		copy.setLength(len);
		copy.setSocketAddress(original.getSocketAddress());
	}


	private DatagramPacket waitForPacket() throws Crashed {
		try {
			return incomingPackets.take();
		} catch (IllegalMonitorStateException e) { //Possible if the producer or consumer thread is killed preemptively during a test, for example.
			crash(); throw new Crashed();
		} catch (InterruptedException e) {
			crash(); throw new Crashed();
		}
	}


	@Override
	public void send(DatagramPacket packet) throws IOException {
		checkNotCrashed();
		checkLocalhost(packet);
		
		InProcessUdpSocket dest = socketsByNumberFinder.evaluate(packet.getPort());
		if (dest == null) return;
		dest.incomingPackets.add(asMine(packet));
	}


	private void checkLocalhost(DatagramPacket packet) {
		if (!packet.getAddress().getHostAddress().equals(LOCALHOST.getHostAddress()))
			throw new IllegalArgumentException("InProcessNetwork supports only "+LOCALHOST.getHostAddress()+" addresses. Not " + packet.getAddress().getHostAddress());
	}


	private void checkNotCrashed() throws IOException {
		if (isCrashed()) throw new Crashed();
	}


	private DatagramPacket asMine(DatagramPacket original) {
		byte[] data = Arrays.copyOf(original.getData(), original.getLength());
		return new DatagramPacket(data, data.length, LOCALHOST, portNumber);
	}


	@Override
	public void crash() {
		isCrashed = true;
	}

	
	boolean isCrashed() {
		return isCrashed;
	}

	
	private static InetAddress localhost() {
		try {
			return InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}
}
