package sneer.bricks.network.computers.udp.connections.impl;

import java.net.SocketAddress;

class UdpSighting {

	private final SocketAddress address;

	public UdpSighting(SocketAddress address) {
		if(address == null) throw new IllegalStateException();
		this.address = address;
	}

	public SocketAddress address() {
		return address;
	}

	public boolean isSameAddress(UdpSighting otherAddress) {
		if(otherAddress == null) return false;
		return address.equals(otherAddress.address);
	}

}
