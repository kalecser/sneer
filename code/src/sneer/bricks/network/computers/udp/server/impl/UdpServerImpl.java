package sneer.bricks.network.computers.udp.server.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.addresses.own.port.OwnPort;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.computers.udp.sender.UdpSender;
import sneer.bricks.network.computers.udp.server.UdpServer;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Signal;
import basis.lang.Consumer;


public class UdpServerImpl implements UdpServer {

	private UdpPortSession portSession;
	
	
	private final WeakContract portContract = ownPort().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer port) {
		handlePort(port);
	}});


	{
		my(UdpSender.class).init(new Consumer<DatagramPacket>() { @Override public void consume(DatagramPacket packet) {
			send(packet);
		}});
	}
	
	
	synchronized
	private void handlePort(int port) {
		closePort();
		if (port < 1) return; 
		portSession = new UdpPortSession(port, new Consumer<DatagramPacket>() { @Override public void consume(DatagramPacket packet) {
			my(UdpConnectionManager.class).handle(packet);
		}});
	}


	private void send(DatagramPacket packet) {
		if (portSession == null) return;
		portSession.send(packet);
	}
	
	
	@Override
	public void crash() {
		portContract.dispose();
		closePort();
	}


	private void closePort() {
		if (portSession == null) return;
		portSession.crash();
		portSession = null;
	}


	static private Signal<Integer> ownPort() {
		return my(Attributes.class).myAttributeValue(OwnPort.class);
	}

}
