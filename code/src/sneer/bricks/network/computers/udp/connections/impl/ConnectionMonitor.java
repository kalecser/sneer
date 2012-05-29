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
	private byte lastHailSequence = 0;
	
	Signal<Boolean> isConnected() {
		return isConnected.output();
	}
	
	void handleHail(SocketAddress sighting, byte hailSequence) {
		if(!isConnected().currentValue() || hailSequence > lastHailSequence) {
			isConnected.setter().consume(true);
			lastPeerSighting = sighting;
			lastHailSequence = hailSequence;
		}
		
		if(sighting.equals(lastPeerSighting))
			lastPeerSightingTime = my(Clock.class).time().currentValue();
	}

	void disconnectIfIdle() {
		long now = my(Clock.class).time().currentValue();
		if (now - lastPeerSightingTime >= UdpConnectionManager.IDLE_PERIOD)
			isConnected.setter().consume(false);
	}

	SocketAddress lastSighting() {
		return lastPeerSighting;
	}

}
