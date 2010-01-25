package sneer.bricks.hardware.cpu.codecs.crypto.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sneer.bricks.hardware.cpu.codecs.crypto.Crypto;
import sneer.bricks.hardware.cpu.codecs.crypto.Digester;
import sneer.bricks.hardware.cpu.codecs.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;

class CryptoImpl implements Crypto {

	private static final int FILE_BLOCK_SIZE = 1024 * 100;

	static {
		Security.addProvider(new BouncyCastleProvider()); //Optimize: remove this static dependency. Use Bouncycastle classes directly
	}

	@Override
	public Sneer1024 digest(byte[] input) {
		return newDigester().digest(input);
	}

	@Override
	public Digester newDigester() {
		return new DigesterImpl(messageDigest("SHA-512", "SUN"), messageDigest("WHIRLPOOL", "BC"));
	}

	private MessageDigest messageDigest(String algorithm, String provider) {
		try {
			return MessageDigest.getInstance(algorithm, provider);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	
	@Override
	public Sneer1024 digest(File file) throws IOException {
		if (file.isDirectory()) throw new IllegalArgumentException("The parameter cannot be a directory");

		Digester digester = newDigester();
		FileInputStream input = new FileInputStream(file);
		try {
			byte[] block = new byte[FILE_BLOCK_SIZE];
			for (int numOfBytes = input.read(block); numOfBytes != -1; numOfBytes = input.read(block)) {
				my(CpuThrottle.class).yield();
				digester.update(block, 0, numOfBytes);
			}
		} finally {
			try { input.close(); } catch (Throwable ignore) {}
		}

		return digester.digest();
	}


	@Override
	public Sneer1024 unmarshallSneer1024(byte[] bytes) {
		return new Sneer1024Impl(bytes);
	}

}
