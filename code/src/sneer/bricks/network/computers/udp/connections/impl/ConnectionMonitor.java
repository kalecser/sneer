package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;

class ConnectionMonitor {

	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	
	private final UdpByteConnection connection;

	public ConnectionMonitor(UdpByteConnection connection) {
		this.connection = connection;
	}
	
	void keepAlive() {
		hail();
		disconnectIfIdle();
	}

	private void hail() {
		connection.send(EMPTY_BYTE_ARRAY);
	}

	private void disconnectIfIdle() {
		long now = my(Clock.class).time().currentValue();
		if (now - connection.lastPeerSightingTime() >= UdpConnectionManager.IDLE_PERIOD)
			connection.becameDisconnected();
	}

}
