package sneer.bricks.network.computers.udp.server.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import sneer.bricks.network.computers.udp.receiver.ReceiverThreads;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.exceptions.Crashed;


class UdpPortSession {

	private static final long RETRY_PERIOD = 3 * 1000;
	private final Light sendError = my(BlinkingLights.class).prepare(LightType.ERROR);
	private final Light openError = my(BlinkingLights.class).prepare(LightType.ERROR);
	private UdpSocket socket;
	private Contract receiverThread;
	private WeakContract retryContract;
	private final int port;
	private final Consumer<DatagramPacket> receiver;
	
	
	UdpPortSession(int port, Consumer<DatagramPacket> receiver) {
		this.port = port;
		this.receiver = receiver;
		retryContract = my(Timer.class).wakeUpNowAndEvery(RETRY_PERIOD, new Closure() { @Override public void run() {
			retryUntilOpen();
		}});
	}


	synchronized
	private void retryUntilOpen() {
		socket = tryToOpenSocket();
		if (socket == null) return;
		
		retryContract.dispose();
		receiverThread = my(ReceiverThreads.class).start(socket, receiver);
	}

	
	private UdpSocket tryToOpenSocket() {
		try {
			UdpSocket ret = my(UdpNetwork.class).openSocket(port);
			my(BlinkingLights.class).turnOffIfNecessary(openError);
			return ret;
		} catch (SocketException e) {
			my(BlinkingLights.class).turnOnIfNecessary(openError, "Network Error", "Unable to open UDP server on port " + port, e);
			return null;
		}
	}
	
	
	void send(DatagramPacket packet) {
		if (socket == null) return;
		try {
			socket.send(packet);
			my(BlinkingLights.class).turnOffIfNecessary(sendError);
		} catch (Crashed e) {
			//Crashed in test mode.
		} catch (IOException e) {
			my(BlinkingLights.class).turnOnIfNecessary(sendError, "Error sending UDP packet", e);
		}
	}
	
	
	void crash() {
		if (retryContract != null) retryContract.dispose();
		if (receiverThread != null) receiverThread.dispose();
		if (socket != null) socket.crash();
	}

}
