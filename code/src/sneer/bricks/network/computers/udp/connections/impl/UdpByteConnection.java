package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Data;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Hail;
import static sneer.bricks.network.computers.udp.connections.impl.UdpByteConnectionUtils.prepare;
import static sneer.bricks.network.computers.udp.connections.impl.UdpByteConnectionUtils.send;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.ECBCipher;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.crypto.ecb.ECBCiphers;
import sneer.bricks.hardware.cpu.crypto.ecdh.ECDHKeyAgreement;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.keys.own.OwnKeys;
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
	private final Object handshakeMonitor = new Object();
	private Consumer<? super ByteBuffer> receiver;
	private ECBCipher cipher;

	
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
		waitUntilHandshake();
		
		ByteBuffer byteBuffer = sender.produce();
		byte[] bytes = new byte[byteBuffer.remaining()]; 
		byteBuffer.get(bytes);
		
		byte[] payload = cipher.encrypt(bytes);
		ByteBuffer buf = prepare(Data);
		
		if (payload.length > buf.remaining()) {
			my(BlinkingLights.class).turnOnIfNecessary(error, "Packet too long", "Trying to send packet of size: " + payload.length + ". Max is " + buf.remaining());
			return;
		}
		
		buf.put(payload);
		buf.flip();
		send(buf, monitor.lastSighting());
	}


	private void waitUntilHandshake() {
		synchronized (handshakeMonitor) {
			if (cipher != null) return; 
			my(Threads.class).waitWithoutInterruptions(handshakeMonitor);
		}
	}
	
	
	void handle(UdpPacketType type, InetSocketAddress origin, ByteBuffer data) {
		my(SightingKeeper.class).keep(contact, origin);
		
		if(type == Hail) {
			long hailTimestamp = data.getLong();
			monitor.handleHail(origin, hailTimestamp);
			initializeCipherIfNecessary(data);
			return;
		}
		
		if (receiver == null || cipher == null) return;
		byte[] payload = new byte[data.remaining()];
		data.get(payload);
		receiver.consume(ByteBuffer.wrap(cipher.decrypt(payload)));
	}
	
	
	private void initializeCipherIfNecessary(ByteBuffer data) {
		synchronized (handshakeMonitor) {
			if (cipher != null) return;
			
			byte[] receivedPublicKey = publicKeyFrom(data);
			checkReceivedPublicKey(receivedPublicKey);
						
			Hash secret = my(ECDHKeyAgreement.class).generateSecret(receivedPublicKey);
			byte[] secret256bits = new byte[256/8];
			secret.bytes.copyTo(secret256bits, 256/8);
			cipher = my(ECBCiphers.class).newAES256(secret256bits);
			handshakeMonitor.notify();
		}
	}


	private void checkReceivedPublicKey(byte[] publicKey) {
		byte[] sealFromPublicKey = my(Crypto.class).digest(publicKey).bytes.copy();
		byte[] knowSeal = my(ContactSeals.class).sealGiven(contact).currentValue().bytes.copy();
		
		if (!Arrays.equals(knowSeal, sealFromPublicKey)) 
			throw new IllegalStateException("Public key from " + contact + " seems to be corrupted");
	}


	private byte[] publicKeyFrom(ByteBuffer data) {
		byte[] otherPeerPublicKey = new byte[OwnKeys.PUBLIC_KEY_SIZE_IN_BYTES];
		data.get(otherPeerPublicKey);

		return otherPeerPublicKey;
	}
	
}
