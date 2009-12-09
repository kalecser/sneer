package sneer.bricks.pulp.crypto.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import sneer.bricks.pulp.crypto.Crypto;
import sneer.bricks.pulp.crypto.Digester;
import sneer.bricks.pulp.crypto.Sneer1024;

class CryptoImpl implements Crypto {

	private static final int FILE_BLOCK_SIZE = 10240;

	static {
		Security.addProvider(new BouncyCastleProvider()); //Optimize: remove this static dependency. Use Bouncycastle classes directly
	}

	private final DigesterImpl _digester = new DigesterImpl(messageDigest("SHA-512", "SUN"), messageDigest("WHIRLPOOL", "BC"));
	
	@Override
	public synchronized Sneer1024 digest(byte[] input) {
		byte[] sha512 = _digester.sha512().digest(input);
		byte[] whirlPool = _digester.whirlPool().digest(input);
		byte[] result = _digester.merge(sha512, whirlPool); 
		return wrap(result);
	}

	private Sneer1024 wrap(byte[] sneer1024Bytes) {
		return new Sneer1024Impl(sneer1024Bytes);
	}

	@Override
	public Digester newDigester() {
		return new DigesterImpl(messageDigest("SHA-512", "SUN"), messageDigest("WHIRLPOOL", "BC"));
	}

	private MessageDigest messageDigest(String algorithm, String provider) {
		try {
			return MessageDigest.getInstance(algorithm, provider);
		} catch (Exception e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Implement Handle this exception.
		}
	}

	@Override
	public Sneer1024 digest(File file) throws IOException {
		FileInputStream input = null;
		Digester digester = null;
		try {
			input = new FileInputStream(file);
			digester = newDigester();
			int result = -1;
			byte[] block = new byte[FILE_BLOCK_SIZE];
			do {
				result = input.read(block);
				digester.update(block);			
			} while(result != -1);
		} finally {
			try { input.close(); } catch (Throwable ignore) { }
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


