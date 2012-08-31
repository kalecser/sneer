package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Data;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Hail;
import static sneer.bricks.network.computers.udp.connections.impl.UdpByteConnectionUtils.prepare;
import static sneer.bricks.network.computers.udp.connections.impl.UdpByteConnectionUtils.send;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

import sneer.bricks.hardware.cpu.crypto.ECBCipher;
import sneer.bricks.hardware.cpu.crypto.ecb.ECBCiphers;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
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
	private final ConnectionMonitor monitor;
	private ECBCipher encrypter;
	private ECBCipher decrypter = decrypter();
	private Consumer<? super ByteBuffer> receiver;

	
	UdpByteConnection(Contact contact) {
		this.contact = contact;
		this.monitor = new ConnectionMonitor(contact);
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
		if (encrypter() == null) return;
		
		ByteBuffer byteBuffer = sender.produce();
		byte[] bytes = new byte[byteBuffer.remaining()]; 
		byteBuffer.get(bytes);
		
		byte[] payload = encrypter().encrypt(bytes);
		ByteBuffer buf = prepare(Data);
		
		if (payload.length > buf.remaining()) {
			my(BlinkingLights.class).turnOnIfNecessary(error, "Packet too long", "Trying to send packet of size: " + payload.length + ". Max is " + buf.remaining());
			return;
		}
		
		buf.put(payload);
		send(buf, monitor.lastSighting());
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
		receiver.consume(ByteBuffer.wrap(decrypter.decrypt(payload)));
	}
	
	
	private ECBCipher encrypter() {
		if (encrypter != null) return encrypter;

		Seal seal = my(ContactSeals.class).sealGiven(contact).currentValue();
		if (seal == null) return null;
		
		byte[] sealBytes = seal.bytes.copy();
		byte[] key = Arrays.copyOf(sealBytes, 256 / 8);
		encrypter = my(ECBCiphers.class).newAES256(key);
		return encrypter;
	}
	
	
	private ECBCipher decrypter() {
		byte[] key = Arrays.copyOf(my(OwnSeal.class).get().currentValue().bytes.copy(), 256/8);
		return my(ECBCiphers.class).newAES256(key);
	}	
	
	
}
