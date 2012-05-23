package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

import java.net.SocketAddress;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

class ConnectionMonitor {

	private SocketAddress lastPeerSighting = null;
	private long lastPeerSightingTime = -UdpConnectionManager.IDLE_PERIOD;
	private Register<Boolean> isConnected = my(Signals.class).newRegister(false);
	
	Signal<Boolean> isConnected() {
		return isConnected.output();
	}
	
	void handleSighting(SocketAddress sighting) {
		if(!isConnected().currentValue()) {
			lastPeerSighting = sighting;
			isConnected.setter().consume(true);
		}
		if(sighting.equals(lastPeerSighting))
			lastPeerSightingTime = my(Clock.class).time().currentValue();
	}

	void keepAlive() {
		disconnectIfIdle();
	}

	private void disconnectIfIdle() {
		long now = my(Clock.class).time().currentValue();
		if (now - lastPeerSightingTime >= UdpConnectionManager.IDLE_PERIOD)
			isConnected.setter().consume(false);
	}

	SocketAddress lastSighting() {
		return lastPeerSighting;
	}

}
