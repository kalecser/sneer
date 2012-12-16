package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

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
import sneer.bricks.network.social.Contact;

class SecurityProtocol {

	private final Object handshakeMonitor = new Object();
	private final Contact contact;
	private ECBCipher cipher;

	SecurityProtocol(Contact contact) {
		this.contact = contact;
	}
	

	void waitUntilHandshake() {
		synchronized (handshakeMonitor) {
			if (cipher != null) return; 
			my(Threads.class).waitWithoutInterruptions(handshakeMonitor);
		}
	}
	
	
	void handleHandshake(ByteBuffer data) {
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
