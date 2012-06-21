package sneer.tests.adapters.impl.utils.network.udp.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.Functor;


class InProcessUdpSocket implements UdpSocket {

	private static final InetAddress LOCALHOST = localhost();
	
	private final int portNumber;
	private final Functor<Integer, InProcessUdpSocket> socketsByNumberFinder;

	private final BlockingQueue<DatagramPacket> incomingPackets = new LinkedBlockingQueue<DatagramPacket>();

	private Thread thread;
	private volatile boolean isCrashed = false;
	


	InProcessUdpSocket(int portNumber, Functor<Integer, InProcessUdpSocket> socketsByNumberFinder) {
		this.portNumber = portNumber;
		this.socketsByNumberFinder = socketsByNumberFinder;
	}


	@Override
	synchronized
	public void initReceiver(final Consumer<DatagramPacket> receiver) {
		final Environment env = my(Environment.class);
		thread = new Thread("In Process Udp Socket") { { setDaemon(true); start(); } @Override public void run() {
			Environments.runWith(env, new Closure() { @Override public void run() {
				while (!isCrashed)
					try {
						receiver.consume(incomingPackets.take());
					} catch (InterruptedException e) {
						return;
					}
			}});
		}};
	}

	
	@Override
	synchronized
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
		if (isCrashed()) throw new IOException("UdpSocket on port " + portNumber + " already crashed.");
	}


	private DatagramPacket asMine(DatagramPacket original) {
		byte[] data = Arrays.copyOf(original.getData(), original.getLength());
		return new DatagramPacket(data, data.length, LOCALHOST, portNumber);
	}


	@Override
	public void crash() {
		isCrashed = true;
		thread.interrupt();
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
