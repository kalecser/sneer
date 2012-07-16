package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Hail;
import static sneer.bricks.network.computers.udp.connections.impl.UdpByteConnectionUtils.prepare;
import static sneer.bricks.network.computers.udp.connections.impl.UdpByteConnectionUtils.send;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.name.OwnName;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.lang.Closure;

class ConnectionMonitor {
	
	private static final Charset UTF8 = Charset.forName("UTF-8");
	
	private SocketAddress fastestPeerSighting = null;
	private long lastPeerSightingTime = -UdpConnectionManager.IDLE_PERIOD;
	private Register<Boolean> isConnected = my(Signals.class).newRegister(false);
	private long fastestHailDelay = 0;
	private final Signal<InetSocketAddress> address;
	private final SetSignal<InetSocketAddress> sightings;
	
	@SuppressWarnings("unused") private WeakContract refToAvoidGC;	
	@SuppressWarnings("unused")	private WeakContract refToAvoidGC2;

	
	
	ConnectionMonitor(Signal<InetSocketAddress> address, SetSignal<InetSocketAddress> sightings) {
		this.address = address;
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
			hail();
		}});
	}
	
	
	private void keepAlive() {
		hail();
		disconnectIfIdle();
	}

	
	private void hail() {
		long now = my(Clock.class).preciseTime();
		ByteBuffer buf = prepare(Hail).putLong(now);
		buf.put(ownNameBytes());
		send(buf, address.currentValue());
		for (SocketAddress addr : sightings)
			send(buf, addr);
	}
	
	
	private byte[] ownNameBytes() {
		return my(Attributes.class).myAttributeValue(OwnName.class).currentValue().getBytes(UTF8);
	}


	private void disconnectIfIdle() {
		long now = my(Clock.class).time().currentValue();
		if (now - lastPeerSightingTime >= UdpConnectionManager.IDLE_PERIOD)
			isConnected.setter().consume(false);
	}

}
