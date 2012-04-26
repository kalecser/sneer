package sneer.bricks.network.computers.udp.server.impl;

import static basis.environments.Environments.my;

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
import sneer.bricks.pulp.blinkinglights.LightType;




public class UdpServerImpl implements UdpServer, Consumer<DatagramPacket> {

	{
		init();
	}

	private void init() {
		UdpSocket socket;
		int ownPort = ownPort();
		try {
			socket = my(UdpNetwork.class).openSocket(ownPort);
		} catch (SocketException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Network Error", "Unable to open UDP server on port " + ownPort, e);
			return;
		}
		socket.initReceiver(this);
	}

	
	private int ownPort() {
		return my(Attributes.class).myAttributeValue(OwnPort.class).currentValue();
	}


	@Override
	public void consume(DatagramPacket packet) {
		my(UdpConnectionManager.class).handle(packet);
	}
}
