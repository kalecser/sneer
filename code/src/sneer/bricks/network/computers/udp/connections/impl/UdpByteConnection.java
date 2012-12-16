package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Data;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Hail;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Handshake;
import static sneer.bricks.network.computers.udp.connections.impl.UdpByteConnectionUtils.prepare;
import static sneer.bricks.network.computers.udp.connections.impl.UdpByteConnectionUtils.send;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.udp.connections.UdpPacketType;
import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Signal;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.Producer;

class UdpByteConnection implements ByteConnection {
	

	private final Light error = my(BlinkingLights.class).prepare(LightType.ERROR);
	private final Contact contact;
	private final SecurityProtocol security;
	private final ConnectionMonitor monitor;
	private Consumer<? super ByteBuffer> receiver;

	
	UdpByteConnection(Contact contact) {
		this.contact = contact;
		this.monitor = new ConnectionMonitor(contact);
		this.security = new SecurityProtocol(contact, monitor);
	}


	@Override
	public Signal<Boolean> isConnected() {
		return monitor.isConnected();
	}

	
	@Override
	public void initCommunications(final Producer<? extends ByteBuffer> sender, Consumer<? super ByteBuffer> receiver) {
		if (this.receiver != null) throw new IllegalStateException();
		this.receiver = receiver;
		my(Threads.class).startStepping("ByteConnection", new Closure() { @Override public void run() {
			tryToSendPacketFor(sender);
		}});
	}
	
	
	private void tryToSendPacketFor(Producer<? extends ByteBuffer> sender) {
		security.waitUntilHandshake();
		
		ByteBuffer byteBuffer = sender.produce();
		byte[] bytes = new byte[byteBuffer.remaining()]; 
		byteBuffer.get(bytes);
		
		byte[] payload = security.encrypt(bytes);
		ByteBuffer buf = prepare(Data);
		
		if (payload.length > buf.remaining()) {
			my(BlinkingLights.class).turnOnIfNecessary(error, "Packet too long", "Trying to send packet of size: " + payload.length + ". Max is " + buf.remaining());
			return;
		}
		
		buf.put(payload);
		buf.flip();
		send(buf, monitor.lastSighting());
	}


	void handle(UdpPacketType type, InetSocketAddress origin, ByteBuffer data) {
		my(SightingKeeper.class).keep(contact, origin);
		
		if (type == Handshake) {
			security.handleHandshake(data);
			return;
		}
		
		if(type == Hail) {
			long hailTimestamp = data.getLong();
			monitor.handleHail(origin, hailTimestamp);
			return;
		}
		
		if (receiver == null || !security.isHandshakeComplete()) 
			return;
		
		byte[] payload = new byte[data.remaining()];
		data.get(payload);
		receiver.consume(ByteBuffer.wrap(security.decrypt(payload)));
	}
	
}
