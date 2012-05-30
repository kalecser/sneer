package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

import java.net.SocketAddress;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

class ConnectionMonitor {

	private SocketAddress fastestPeerSighting = null;
	private long lastPeerSightingTime = -UdpConnectionManager.IDLE_PERIOD;
	private Register<Boolean> isConnected = my(Signals.class).newRegister(false);
	private long fastestHailDelay = 0;
	
	Signal<Boolean> isConnected() {
		return isConnected.output();
	}
	
	void handleHail(SocketAddress sighting, long timestamp) {
		Long now = my(Clock.class).preciseTime();
		long hailDelay = now - timestamp;
		
		if(!isConnected().currentValue() || hailDelay < fastestHailDelay) {
			isConnected.setter().consume(true);
			fastestPeerSighting = sighting;
			fastestHailDelay = hailDelay;
		}
		
		if(sighting.equals(fastestPeerSighting))
			lastPeerSightingTime = now;
	}

	void disconnectIfIdle() {
		long now = my(Clock.class).time().currentValue();
		if (now - lastPeerSightingTime >= UdpConnectionManager.IDLE_PERIOD)
			isConnected.setter().consume(false);
	}

	SocketAddress lastSighting() {
		return fastestPeerSighting;
	}

}
