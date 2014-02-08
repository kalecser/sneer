package sneer.bricks.network.computers.udp.holepuncher.server.listener.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;

import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.server.StunServer;
import sneer.bricks.network.computers.udp.holepuncher.server.listener.StunServerListeners;

class StunServerListenersImpl implements StunServerListeners {


	{
		int port = my(StunProtocol.class).serverPort();
		
		new Listener("Stun", port) { @Override DatagramPacket[] repliesFor(DatagramPacket packet) {
			return my(StunServer.class).repliesFor(packet);
		}};
		new Listener("Stun Alternate1", port + 1) { @Override DatagramPacket[] repliesFor(DatagramPacket packet) {
			return my(StunServer.class).repliesForAlternate(packet);
		}};
		new Listener("Stun Alternate2", port + 2) { @Override DatagramPacket[] repliesFor(DatagramPacket packet) {
			return my(StunServer.class).repliesForAlternate(packet);
		}};

	}

}
