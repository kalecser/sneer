package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Data;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Hail;
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

class UdpByteConnection implements ByteConnection {
	

	private final Light error = my(BlinkingLights.class).prepare(LightType.ERROR);
	private final Contact contact;
	private final ConnectionMonitor monitor;
	//private ECBCipher cipher;
	private Consumer<? super byte[]> receiver;

	
	UdpByteConnection(Contact contact) {
		this.contact = contact;
		this.monitor = new ConnectionMonitor(contact);
		//this.cipher = cipherFor(contact);
	}


	@Override
	public Signal<Boolean> isConnected() {
		return monitor.isConnected();
	}

	
	@Override
	public void initCommunications(final PacketScheduler scheduler, Consumer<? super byte[]> receiver) {
		if (this.receiver != null) throw new IllegalStateException();
		this.receiver = receiver;
		my(Threads.class).startStepping("ByteConnection", new Closure() { @Override public void run() {
			tryToSendPacketFor(scheduler);
		}});
	}
	
	
	private void tryToSendPacketFor(PacketScheduler scheduler) {
		byte[] payload = scheduler.highestPriorityPacketToSend();
		ByteBuffer buf = prepare(Data);
		
		if (payload.length > buf.remaining()) {
			my(BlinkingLights.class).turnOnIfNecessary(error, "Packet too long", "Trying to send packet of size: " + payload.length + ". Max is " + buf.remaining());
			scheduler.previousPacketWasSent();
			return;
		}
		
		//buf.put(cipher().encrypt(payload));
		buf.put(payload);
		if (send(buf, monitor.lastSighting()))
			scheduler.previousPacketWasSent();
	}
	
	void handle(UdpPacketType type, InetSocketAddress origin, ByteBuffer data) {
		my(SightingKeeper.class).keep(contact, origin);
		
		if(type == Hail) {
			long hailTimestamp = data.getLong();
			monitor.handleHail(origin, hailTimestamp);
			return;
		}
		
		if (receiver == null) return;
		byte[] payload = new byte[data.remaining()];
		data.get(payload);
		//receiver.consume(cipher().decrypt(payload));
		receiver.consume(payload);
	}
	
	
//	private ECBCipher cipher() {
//		byte[] sealBytes = my(ContactSeals.class).sealGiven(contact).currentValue().bytes.copy();
//		byte[] key = Arrays.copyOf(sealBytes, 256 / 8);
//		return my(Crypto.class).newAES256Cipher(key);
//	}
	
	
}
