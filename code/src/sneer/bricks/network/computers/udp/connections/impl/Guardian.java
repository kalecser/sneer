package sneer.bricks.network.computers.udp.connections.impl;

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

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

import static basis.environments.Environments.my;
import static sneer.bricks.hardware.cpu.crypto.ecdh.ECDHKeyAgreement.SESSION_KEY_SIZE;
import static sneer.bricks.identity.keys.own.OwnKeys.PUBLIC_KEY_SIZE_IN_BYTES;
import static sneer.bricks.network.computers.udp.connections.UdpConnectionManager.KEEP_ALIVE_PERIOD;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Handshake;
import static sneer.bricks.network.computers.udp.connections.impl.UdpByteConnectionUtils.prepare;
import static sneer.bricks.network.computers.udp.connections.impl.UdpByteConnectionUtils.send;

class Guardian {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private final Object handshakeMonitor = new Object();
	private final Contact contact;
	private final ConnectionMonitor monitor;
	private byte[] sessionKey;
	private ECBCipher cipher;
	private WeakContract handshakeTimer;

	@SuppressWarnings("unused") private final WeakContract refToAvoidGC;


	Guardian(Contact contact, ConnectionMonitor monitor) {
		this.contact = contact;
		this.monitor = monitor;

		refToAvoidGC = monitor.isConnected().addReceiver(new Consumer<Boolean>() {  @Override public void consume(Boolean isConnected) {
			if (isConnected)
				handleConnect();
			else
				handleDisconnect();
		}});
	}


	private void handleConnect() {
		handshakeTimer = my(Timer.class).wakeUpNowAndEvery(KEEP_ALIVE_PERIOD, new Runnable() { @Override public void run() {
			handshake();
		}});
	}

	
	private void handleDisconnect() {
		stopHandshake();
        cipher = null;
		sessionKey = null;
	}
	

	private void handshake() {
		ByteBuffer buf = prepare(Handshake);
		buf.put((byte)(isHandshakeComplete() ? 1 : 0));
		buf.put(ownPublicKey());
		buf.put(ownSessionKeyBytes());
		buf.flip();
		
		send(buf, monitor.lastSighting());
	}
	
	
	private byte[] ownPublicKey() {
		byte[] ret = my(OwnKeys.class).ownPublicKey().currentValue().getEncoded();
		if (ret.length != OwnKeys.PUBLIC_KEY_SIZE_IN_BYTES) throw new IllegalStateException("Public key length is expected to be "+ OwnKeys.PUBLIC_KEY_SIZE_IN_BYTES +" bytes, was " + ret.length);
		return ret;
	}
	
	
	private byte[] ownSessionKeyBytes() {
		if (sessionKey == null)
			sessionKey = randomBytes();
		
		return sessionKey;
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
			if (shouldReplySecurityInfo(data)) {
				handshake();
				return;
			}
			
			byte[] receivedPublicKey = publicKeyFrom(data);
			checkReceivedPublicKey(receivedPublicKey);
			
			byte[] otherPeerSessionKey = sessionKeyFrom(data);
			Hash encryptSecret = my(ECDHKeyAgreement.class).generateSecret(receivedPublicKey, ownSessionKeyBytes());
			Hash decryptSecret = my(ECDHKeyAgreement.class).generateSecret(receivedPublicKey, otherPeerSessionKey);
			cipher = my(ECBCiphers.class).newAES256(secret256bits(encryptSecret), secret256bits(decryptSecret));
			
			stopHandshake();
			handshakeMonitor.notify();
		}
	}


	private boolean shouldReplySecurityInfo(ByteBuffer data) {
		boolean isCompletedFromOtherSide = data.get() == 1;
		return isHandshakeComplete() && !isCompletedFromOtherSide;
	}


	private byte[] publicKeyFrom(ByteBuffer data) {
		byte[] otherPeerPublicKey = new byte[PUBLIC_KEY_SIZE_IN_BYTES];
		data.get(otherPeerPublicKey);
		
		return otherPeerPublicKey;
	}

	
	private void checkReceivedPublicKey(byte[] publicKey) {
		byte[] sealFromPublicKey = my(Crypto.class).digest(publicKey).bytes.copy();
		byte[] knowSeal = my(ContactSeals.class).sealGiven(contact).currentValue().bytes.copy();
		
		if (!Arrays.equals(knowSeal, sealFromPublicKey)) 
			throw new IllegalStateException("Public key from " + contact + " seems to be corrupted");
	}
	
	
	private byte[] sessionKeyFrom(ByteBuffer data) {
		byte[] ret = new byte[SESSION_KEY_SIZE];
		data.get(ret);
		return ret;
	}
	
	
	private byte[] secret256bits(Hash decryptSecret) {
		byte[] secret256bits = new byte[SESSION_KEY_SIZE];
		decryptSecret.bytes.copyTo(secret256bits, SESSION_KEY_SIZE);
		return secret256bits;
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


	private static byte[] randomBytes() {
		byte[] bytes = new byte[SESSION_KEY_SIZE];
		SECURE_RANDOM.nextBytes(bytes);
		return bytes;
	}
}


