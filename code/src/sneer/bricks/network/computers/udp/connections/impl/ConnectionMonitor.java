package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Hail;
import static sneer.bricks.network.computers.udp.connections.impl.UdpByteConnectionUtils.send;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.lang.Closure;

class ConnectionMonitor {

	private SocketAddress fastestPeerSighting = null;
	private long lastPeerSightingTime = -UdpConnectionManager.IDLE_PERIOD;
	private Register<Boolean> isConnected = my(Signals.class).newRegister(false);
	private long fastestHailDelay = 0;
	private final SetSignal<InetSocketAddress> sightings;
	
	@SuppressWarnings("unused") private WeakContract refToAvoidGC;	
	@SuppressWarnings("unused")	private WeakContract refToAvoidGC2;
	
	ConnectionMonitor(SetSignal<InetSocketAddress> sightings) {
		this.sightings = sightings;
		startHailing();
	}

	Signal<Boolean> isConnected() {
		return isConnected.output();
	}
	
	SocketAddress lastSighting() {
		return fastestPeerSighting;
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
	
	private void startHailing() {
		refToAvoidGC = my(Timer.class).wakeUpEvery(UdpConnectionManager.KEEP_ALIVE_PERIOD, new Runnable() { @Override public void run() {
			keepAlive();
		}});
		
		refToAvoidGC2 = sightings.addPulseReceiver(new Closure() { @Override public void run() {
			keepAlive();
		}});
	}
	
	private void keepAlive() {
		hail();
		disconnectIfIdle();
	}

	private void hail() {
		long now = my(Clock.class).preciseTime();
		byte[] hailBytes = ByteBuffer.allocate(8).putLong(now).array(); //Optimize: Reuse buffer
		for (SocketAddress addr : sightings)
			send(Hail, hailBytes, addr);
	}
	
	private void disconnectIfIdle() {
		long now = my(Clock.class).time().currentValue();
		if (now - lastPeerSightingTime >= UdpConnectionManager.IDLE_PERIOD)
			isConnected.setter().consume(false);
	}

}
