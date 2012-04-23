package sneer.bricks.network.computers.udp.inprocess.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.Functor;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;


class InProcessUdpSocket implements UdpSocket {

	private static final InetAddress LOCALHOST = localhost();
	
	private final int portNumber;
	private final Functor<Integer, InProcessUdpSocket> socketsByNumberFinder;

	private Contract receivingContract;
	private final BlockingQueue<DatagramPacket> incomingPackets = new LinkedBlockingQueue<DatagramPacket>();

	private boolean isCrashed = false;


	InProcessUdpSocket(int portNumber, Functor<Integer, InProcessUdpSocket> socketsByNumberFinder) {
		this.portNumber = portNumber;
		this.socketsByNumberFinder = socketsByNumberFinder;
	}


	@Override
	synchronized
	public void initReceiver(final Consumer<DatagramPacket> receiver) {
		if (receivingContract != null) throw new IllegalStateException();
		receivingContract = my(Threads.class).startStepping(new Closure() { @Override public void run() {
			receiver.consume(waitForNextPacket());
		}});
	}

	
	@Override
	synchronized
	public void send(DatagramPacket packet) throws IOException {
		checkNotCrashed();
		checkLocalhost(packet);
		
		InProcessUdpSocket dest = socketsByNumberFinder.evaluate(packet.getPort());
		dest.incomingPackets.add(asMine(packet));
	}


	private void checkLocalhost(DatagramPacket packet) {
		if (!packet.getAddress().getHostAddress().equals(LOCALHOST.getHostAddress()))
			throw new IllegalArgumentException("InProcessNetwork supports only "+LOCALHOST.getHostAddress()+" addresses. Not " + packet.getAddress().getHostAddress());
	}


	private void checkNotCrashed() throws IOException {
		if (isCrashed) throw new IOException("UdpSocket on port " + portNumber + " already crashed.");
	}


	private DatagramPacket asMine(DatagramPacket original) {
		byte[] data = Arrays.copyOf(original.getData(), original.getLength());
		return new DatagramPacket(data, data.length, LOCALHOST, portNumber);
	}


	@Override
	public void crash() {
		receivingContract.dispose();
		isCrashed = true;
	}

	
	boolean isCrashed() {
		return isCrashed;
	}

	
	private DatagramPacket waitForNextPacket() {
		try {
			return incomingPackets.take();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
	
	
	private static InetAddress localhost() {
		try {
			return InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}
}
