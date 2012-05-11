package sneer.bricks.network.computers.udp.server.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import basis.lang.Consumer;

import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.computers.udp.server.UdpServer;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;




public class UdpServerImpl implements UdpServer, Consumer<DatagramPacket> {

	private Light sendError = my(BlinkingLights.class).prepare(LightType.ERROR);


	{
		init();
	}

	private void init() {
		final UdpSocket socket;
		socket = tryToOpenSocket();
		if(socket == null) return;
		socket.initReceiver(this);
		my(UdpConnectionManager.class).initSender(new Consumer<DatagramPacket>() { @Override public void consume(DatagramPacket packet) {
			send(socket, packet);
		}});
	}

	private UdpSocket tryToOpenSocket() {
		int ownPort = ownPort();
		try {
			return my(UdpNetwork.class).openSocket(ownPort);
		} catch (SocketException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Network Error", "Unable to open UDP server on port " + ownPort, e);
			return null;
		}
	}
	
	private void send(final UdpSocket socket, DatagramPacket packet) {
		try {
			socket.send(packet);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOnIfNecessary(sendError, "Error sending UDP packet", e);
		}
	}
	
	private int ownPort() {
		return my(Attributes.class).myAttributeValue(OwnPort.class).currentValue();
	}


	@Override
	public void consume(DatagramPacket packet) {
		my(UdpConnectionManager.class).handle(packet);
	}
}
