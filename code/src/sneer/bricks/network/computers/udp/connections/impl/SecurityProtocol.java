package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.connections.UdpConnectionManager.KEEP_ALIVE_PERIOD;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Handshake;
import static sneer.bricks.network.computers.udp.connections.impl.UdpByteConnectionUtils.prepare;
import static sneer.bricks.network.computers.udp.connections.impl.UdpByteConnectionUtils.send;

import java.nio.ByteBuffer;
import java.util.Arrays;

import basis.lang.Consumer;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.ECBCipher;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.crypto.ecb.ECBCiphers;
import sneer.bricks.hardware.cpu.crypto.ecdh.ECDHKeyAgreement;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.keys.own.OwnKeys;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;

class SecurityProtocol {

	private final Object handshakeMonitor = new Object();
	private final Contact contact;
	private final ConnectionMonitor monitor;
	private Hash sessionKey;
	private ECBCipher cipher;
	private WeakContract handshakeTimer;

	@SuppressWarnings("unused") private final WeakContract refToAvoidGC;

	
	SecurityProtocol(Contact contact, ConnectionMonitor monitor) {
		this.contact = contact;
		this.monitor = monitor;
		
		refToAvoidGC = monitor.isConnected().addReceiver(new Consumer<Boolean>() {  @Override public void consume(Boolean isConnected) {
			if (isConnected) startHandshaking();
			else resetHandshake();
		}});
	}


	private void startHandshaking() {
		if (handshakeTimer != null) return;
		handshakeTimer = my(Timer.class).wakeUpNowAndEvery(KEEP_ALIVE_PERIOD, new Runnable() { @Override public void run() {
			handshake();
		}});
	}
	

	private void handshake() {
		ByteBuffer buf = prepare(Handshake);
		buf.put(ownPublicKey());
		buf.put(sessionKeyBytes());
		buf.flip();
		
		send(buf, monitor.lastSighting());
	}
	
	
	private byte[] ownPublicKey() {
		byte[] ret = my(OwnKeys.class).ownPublicKey().currentValue().getEncoded();
		if (ret.length != OwnKeys.PUBLIC_KEY_SIZE_IN_BYTES) throw new IllegalStateException("Public key length is expected to be "+ OwnKeys.PUBLIC_KEY_SIZE_IN_BYTES +" bytes, was " + ret.length);
		return ret;
	}
	
	
	private byte[] sessionKeyBytes() {
		if (sessionKey == null)
			sessionKey = my(ECDHKeyAgreement.class).generateSessionKey();
		
		return sessionKey.bytes.copy();
	}
	
	
	private void resetHandshake() {
		stopHandshake();
		sessionKey = null;
	}


	private void stopHandshake() {
		if (handshakeTimer == null) return;
		handshakeTimer.dispose();
		handshakeTimer = null;
	}


	void waitUntilHandshake() {
		synchronized (handshakeMonitor) {
			if (cipher != null) return; 
			my(Threads.class).waitWithoutInterruptions(handshakeMonitor);
		}
	}
	
	
	void handleHandshake(ByteBuffer data) {
		synchronized (handshakeMonitor) {
			if (isHandshakeComplete()) { // TODO: Improve this to avoid loop
				handshake();
				return;
			}
			
			byte[] receivedPublicKey = publicKeyFrom(data);
			checkReceivedPublicKey(receivedPublicKey);
						
			Hash secret = my(ECDHKeyAgreement.class).generateSecret(receivedPublicKey);
			byte[] secret256bits = new byte[256/8];
			secret.bytes.copyTo(secret256bits, 256/8);
			cipher = my(ECBCiphers.class).newAES256(secret256bits);
			
			stopHandshake();
			handshakeMonitor.notify();
		}
	}


	private byte[] publicKeyFrom(ByteBuffer data) {
		byte[] otherPeerPublicKey = new byte[OwnKeys.PUBLIC_KEY_SIZE_IN_BYTES];
		data.get(otherPeerPublicKey);
		
		return otherPeerPublicKey;
	}


	private void checkReceivedPublicKey(byte[] publicKey) {
		byte[] sealFromPublicKey = my(Crypto.class).digest(publicKey).bytes.copy();
		byte[] knowSeal = my(ContactSeals.class).sealGiven(contact).currentValue().bytes.copy();
		
		if (!Arrays.equals(knowSeal, sealFromPublicKey)) 
			throw new IllegalStateException("Public key from " + contact + " seems to be corrupted");
	}


	byte[] encrypt(byte[] bytes) {
		if (cipher == null) throw new IllegalStateException("Should waint until handshake");
		return cipher.encrypt(bytes);
	}
	

	byte[] decrypt(byte[] payload) {
		if (cipher == null) throw new IllegalStateException("Should waint until handshake");
		return cipher.decrypt(payload);
	}


	boolean isHandshakeComplete() {
		return cipher != null;
	}

}
