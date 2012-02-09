package sneer.bricks.pulp.network.udp.impl;

import java.io.IOException;

import sneer.bricks.pulp.network.ByteArrayServerSocket;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network2010;


public class UdpNetworkImpl implements Network2010 {

	@Override
	public ByteArraySocket openSocket(String remoteAddress, int remotePort) throws IOException {
		return new OutgoingUdpSocket(remoteAddress, remotePort);
	}

	@Override
	public ByteArrayServerSocket openServerSocket(int port) throws IOException {
		return new UdpServerSocket(port);
	}

	@Override
	public String remoteIpFor(ByteArraySocket socket) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

}
