package spikes.klaus.crypto;

import java.security.SecureRandom;

class CountingSecureRandom extends SecureRandom {
	
	int _bytesRequested = 0;
	
	@Override
	public synchronized void nextBytes(byte[] bytes) {
		_bytesRequested += bytes.length;
		super.nextBytes(bytes);
	}
	
}