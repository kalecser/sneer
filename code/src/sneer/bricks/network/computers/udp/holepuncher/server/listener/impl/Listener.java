package sneer.bricks.network.computers.udp.holepuncher.server.listener.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import sneer.bricks.network.computers.udp.holepuncher.server.listener.StunServerListeners;
import sneer.bricks.network.computers.udp.receiver.ReceiverThreads;
import basis.lang.Consumer;

abstract class Listener implements StunServerListeners, Consumer<DatagramPacket> {

	private final UdpSocket socket;

	Listener(String name, int port) {
		// Implement Auto-generated constructor stub
		
		my(Logger.class).log("Opening Server: " + name + " port: " + port + "...");
		try {
			socket = my(UdpNetwork.class).openSocket(port);
		} catch (SocketException e) {
			throw new IllegalStateException(e);
		}
		my(Logger.class).log("Server port open.");
		
		my(ReceiverThreads.class).start(name + " Server", socket, this);
	}
	
	
	@Override
	public void consume(DatagramPacket packet) {
		tryToSend(repliesFor(packet));
	}


	abstract DatagramPacket[] repliesFor(DatagramPacket packet);


	private void tryToSend(DatagramPacket[] replies) {
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
