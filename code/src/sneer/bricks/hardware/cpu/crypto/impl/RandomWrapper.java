/**
 * 
 */
package sneer.bricks.hardware.cpu.crypto.impl;

import java.security.SecureRandom;

/**
 * Using SecureRandom instances provided by the java security mechanism is a bloody mess.
 * There is no way of creating a new Secure random implementation with a given seed and specific algorithm. 
 */
class RandomWrapper extends SecureRandom {
	

	private byte[] randomBytes;

	RandomWrapper(byte[] randomBytes) {
		this.randomBytes = randomBytes;
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
		if (bytes.length != randomBytes.length) throw new IllegalStateException();
		System.arraycopy(randomBytes, 0, bytes, 0, randomBytes.length);
		randomBytes = null;
	}
	
}