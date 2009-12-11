package sneer.bricks.hardware.cpu.algorithms.crypto.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import sneer.bricks.hardware.cpu.algorithms.crypto.Crypto;
import sneer.bricks.hardware.cpu.algorithms.crypto.Digester;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;

class CryptoImpl implements Crypto {

	private static final int FILE_BLOCK_SIZE = 10240;

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
		Digester digester = newDigester();
		FileInputStream input = new FileInputStream(file);

		try {
			byte[] block = new byte[FILE_BLOCK_SIZE];
			for (int numOfBytes = input.read(block); numOfBytes != -1; numOfBytes = input.read(block))
				digester.update(block, 0, numOfBytes);
		} finally {
			try { input.close(); } catch (Throwable ignore) {}
		}
		
		return digester.digest();
	}

	@Override
	public Sneer1024 unmarshallSneer1024(byte[] bytes) {
		return new Sneer1024Impl(bytes);
	}

	@Override
	public String toHexa(byte[] bytes) {
		return new String(Hex.encode(bytes));
	}

}
