package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import basis.lang.Consumer;

class ConnectionMonitor {

	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	
	private final UdpByteConnection connection;
	private UdpSighting lastPeerSighting = null;
	private long lastPeerSightingTime = -UdpConnectionManager.IDLE_PERIOD;
	@SuppressWarnings("unused") private final WeakContract refToAvoidGC;

	ConnectionMonitor(UdpByteConnection connection) {
		this.connection = connection;
		refToAvoidGC = connection.lastSighting().addReceiver(new Consumer<UdpSighting>() { @Override public void consume(UdpSighting sighting) {
			handleSighting(sighting);
		}});
		keepAlive();
	}
	
	private void handleSighting(UdpSighting sighting) {
		if(sighting == null) return;
		if(!connection.isConnected().currentValue()) {
			lastPeerSighting = sighting;
			connection.becameConnected();
		}
		if(sighting.isSameAddress(lastPeerSighting))
			lastPeerSightingTime = my(Clock.class).time().currentValue();
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
		if (now - lastPeerSightingTime >= UdpConnectionManager.IDLE_PERIOD)
			connection.becameDisconnected();
	}

}
