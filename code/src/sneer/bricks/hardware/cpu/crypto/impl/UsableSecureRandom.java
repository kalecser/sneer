/**
 * 
 */
package sneer.bricks.hardware.cpu.crypto.impl;

import java.security.SecureRandom;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import org.bouncycastle.crypto.prng.RandomGenerator;

/**
 * Using SecureRandom instances provided by the java security mechanism is a bloody mess.
 * There is no way of creating a new Secure random implementation with a given seed and specific algorithm. 
 */
class UsableSecureRandom extends SecureRandom {
	
	private RandomGenerator _delegate;

	UsableSecureRandom(byte[] seed) {
		_delegate = new DigestRandomGenerator(new SHA256Digest());
		_delegate.addSeedMaterial(seed);
	}

	@Override
	public String getAlgorithm() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void setSeed(byte[] seed) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSeed(long seed) {
		if (seed == 0) return; //ignore initial seed set by superclasses
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] generateSeed(int numBytes) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void nextBytes(byte[] bytes) {
		_delegate.nextBytes(bytes);
	}
	
}