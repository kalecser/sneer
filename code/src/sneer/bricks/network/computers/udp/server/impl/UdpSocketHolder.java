package sneer.bricks.network.computers.udp.server.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import sneer.bricks.network.computers.udp.receiver.ReceiverThreads;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import basis.lang.Consumer;
import basis.lang.exceptions.Crashed;

class UdpSocketHolder {
	
	private static final Light sendError = my(BlinkingLights.class).prepare(LightType.ERROR);
	
	private final UdpSocket socket;
	private final Contract receiverThread;

	
	UdpSocketHolder(int port, Consumer<DatagramPacket> receiver) throws SocketException {
		socket = my(UdpNetwork.class).openSocket(port);
		receiverThread = my(ReceiverThreads.class).start(threadName(port), socket, receiver);
	}



	void send(DatagramPacket packet) {
		try {
			socket.send(packet);
			my(BlinkingLights.class).turnOffIfNecessary(sendError);
		} catch (Crashed e) {
		} catch (IOException e) {
			my(BlinkingLights.class).turnOnIfNecessary(sendError, "Error sending UDP packet", e);
		}
	}


	void crash() {
		my(BlinkingLights.class).turnOffIfNecessary(sendError);
		socket.crash();
		receiverThread.dispose();
	}
	
	private static String threadName(int port) {
		return UdpSocketHolder.class.getSimpleName() + " on port " + port;
	}

}

