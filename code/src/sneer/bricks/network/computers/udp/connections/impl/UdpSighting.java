package sneer.bricks.network.computers.udp.connections.impl;

import java.net.SocketAddress;

public class UdpSighting {

	private final SocketAddress address;

	public UdpSighting(SocketAddress address) {
		this.address = address;
	}

	public SocketAddress address() {
		return address;
	}

}
