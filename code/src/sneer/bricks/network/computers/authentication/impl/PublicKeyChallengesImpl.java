package sneer.bricks.network.computers.authentication.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.keys.signatures.Signatures;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.authentication.PublicKeyChallenges;
import sneer.bricks.pulp.network.ByteArraySocket;

class PublicKeyChallengesImpl implements PublicKeyChallenges {

	private final SecureRandom _random = new SecureRandom();
	
	@Override
	public boolean challenge(Seal contactsSeal, ByteArraySocket socket) throws IOException {
		byte[] myChallenge = generateChallenge();

		socket.write(ownPublicKey());
		socket.write(myChallenge);
		
		PublicKey publicKey = readPublicKey(socket);
		@SuppressWarnings("unused")	byte[] hisChallenge = socket.read();

		check(contactsSeal, publicKey);
		
		byte[] challengeSignature = socket.read();
		return my(Signatures.class).verifySignature(myChallenge, publicKey, challengeSignature);
	}


	private byte[] ownPublicKey() {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}


	private void check(Seal seal, PublicKey publicKey) throws IOException {
		Hash hash = my(Crypto.class).digest(publicKey.getEncoded());
		if (!Arrays.equals(seal.bytes.copy(), hash.bytes.copy()))
			throw new IOException("Public Key did not match Seal.");
	}


	private PublicKey readPublicKey(ByteArraySocket socket) throws IOException {
		byte[] publicKeyBytes = socket.read();
		return decode(publicKeyBytes);
	}


	private PublicKey decode(byte[] publicKeyBytes) throws IOException {
		KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance("ECDSA", "BC");
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
		
		try {
			return keyFactory.generatePublic(publicKeySpec);
		} catch (InvalidKeySpecException e) {
			throw new IOException(e);
		}
	}

	private byte[] generateChallenge() {
		byte[] result = new byte[64];
		_random.nextBytes(result);
		return result;
	}

}
