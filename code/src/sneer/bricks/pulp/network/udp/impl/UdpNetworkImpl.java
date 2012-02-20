package sneer.bricks.pulp.network.udp.impl;

import java.net.SocketException;

import sneer.bricks.pulp.network.udp.UdpNetwork;


class UdpNetworkImpl implements UdpNetwork {

	@Override
	public UdpSocket openSocket(int port) throws SocketException {
		return new UdpSocketImpl(port);
	}

}
