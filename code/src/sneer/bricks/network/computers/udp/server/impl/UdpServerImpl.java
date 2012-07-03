package sneer.bricks.network.computers.udp.server.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.computers.udp.receiver.ReceiverThread;
import sneer.bricks.network.computers.udp.receiver.ReceiverThreads;
import sneer.bricks.network.computers.udp.sender.UdpSender;
import sneer.bricks.network.computers.udp.server.UdpServer;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Signal;
import basis.lang.Consumer;
import basis.lang.exceptions.Crashed;


public class UdpServerImpl implements UdpServer, Consumer<DatagramPacket> {

	private final Light sendError = my(BlinkingLights.class).prepare(LightType.ERROR);
	private UdpSocket socket;
	private ReceiverThread receiverThread;
	
	
	@SuppressWarnings("unused") private WeakContract refToAvoidGC = ownPort().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer port) {
		handlePort(port);
	}});
	
	
	{
		my(UdpSender.class).init(new Consumer<DatagramPacket>() { @Override public void consume(DatagramPacket packet) {
			send(packet);
		}});
	}
	
	
	static private Signal<Integer> ownPort() {
		return my(Attributes.class).myAttributeValue(OwnPort.class);
	}
	

	private void handlePort(Integer port) {
		if (port == null) return;
		openUpdSocket(port);
	}

	
	synchronized
	private void openUpdSocket(int port) {
		closeSocketIfNecessary();
		socket = tryToOpenSocket(port);
		if(socket == null) return;
		receiverThread = my(ReceiverThreads.class).start(socket, this);
	}
	

	private void closeSocketIfNecessary() {
		if (socket == null) return;
		receiverThread.crash();
		socket.crash();
		socket = null;
	}
	

	private UdpSocket tryToOpenSocket(int port) {
		try {
			return my(UdpNetwork.class).openSocket(port);
		} catch (SocketException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Network Error", "Unable to open UDP server on port " + port, e);
			return null;
		}
	}
	
	
	private void send(DatagramPacket packet) {
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
	
	
	@Override
	public void consume(DatagramPacket packet) {
		my(UdpConnectionManager.class).handle(packet);
	}

	
	@Override
	public void crash() {
		if (socket == null) return;
		receiverThread.crash();
		socket.crash();
	}

}
