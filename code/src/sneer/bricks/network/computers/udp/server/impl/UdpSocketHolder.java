package sneer.bricks.network.computers.udp.server.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import basis.lang.Consumer;
import basis.lang.exceptions.Crashed;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import sneer.bricks.network.computers.udp.receiver.ReceiverThreads;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;

class UdpSocketHolder {
	
	private static final Light openError = my(BlinkingLights.class).prepare(LightType.ERROR);
	private static final Light sendError = my(BlinkingLights.class).prepare(LightType.ERROR);
	
	private final UdpSocket socket;
	private final Contract receiverThread;

	
	static UdpSocketHolder newHolderFor(int port, Consumer<DatagramPacket> receiver) {
		try {
			UdpSocket socket = my(UdpNetwork.class).openSocket(port);
			my(BlinkingLights.class).turnOffIfNecessary(openError);
			return new UdpSocketHolder(socket, receiver);
		} catch (SocketException e) {
			my(BlinkingLights.class).turnOnIfNecessary(openError, "Network Error", "Unable to open UDP server on port " + port, e);
			return null;
		}
	}
	
	
	private UdpSocketHolder(UdpSocket socket, Consumer<DatagramPacket> receiver) {
		this.socket = socket;
		this.receiverThread = my(ReceiverThreads.class).start(socket, receiver);
	}
	

	void send(DatagramPacket packet) {
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
		socket.crash();
		receiverThread.dispose();
	}
	

}
