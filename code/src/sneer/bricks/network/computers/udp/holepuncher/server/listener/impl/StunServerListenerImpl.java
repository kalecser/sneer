package sneer.bricks.network.computers.udp.holepuncher.server.listener.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.server.StunServer;
import sneer.bricks.network.computers.udp.holepuncher.server.listener.StunServerListener;
import sneer.bricks.network.computers.udp.receiver.ReceiverThreads;
import basis.lang.Consumer;

class StunServerListenerImpl implements StunServerListener {

	private UdpSocket socket;

	{
		my(Logger.class).log("Opening Stun Server port...");
		int port = my(StunProtocol.class).serverAddress().getPort();
		try {
			socket = my(UdpNetwork.class).openSocket(port);
		} catch (SocketException e) {
			throw new IllegalStateException(e);
		}
		my(Logger.class).log("Stun Server port open.");
		
		my(ReceiverThreads.class).start(socket, new Consumer<DatagramPacket>() { @Override public void consume(DatagramPacket packet) {
			handle(packet);
		}});
	}

	
	private void handle(DatagramPacket packet) {
		DatagramPacket[] replies = my(StunServer.class).repliesFor(packet);
		for (DatagramPacket reply : replies)
			tryToSend(reply);
	}
	
	
	private void tryToSend(DatagramPacket reply) {
		try {
			socket.send(reply);
		} catch (IOException e) {
			my(ExceptionLogger.class).log(e);
		}
	}

}
