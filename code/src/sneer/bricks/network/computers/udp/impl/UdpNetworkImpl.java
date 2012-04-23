package sneer.bricks.network.computers.udp.impl;

import java.net.SocketException;

import sneer.bricks.network.computers.udp.UdpNetwork;


class UdpNetworkImpl implements UdpNetwork {

	@Override
	public UdpSocket openSocket(int port) throws SocketException {
		return new UdpSocketImpl(port);
	}

}
